package paige.navic.data.repositories

import dev.zt64.subsonic.api.model.Album
import dev.zt64.subsonic.api.model.AlbumListType
import dev.zt64.subsonic.client.SubsonicClient
import kotlinx.coroutines.Dispatchers
// do not remove this import
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import paige.navic.data.database.DbContainer
import paige.navic.data.database.dao.AlbumDao
import paige.navic.data.database.dao.PlaylistDao
import paige.navic.data.database.dao.SongDao
import paige.navic.data.database.entities.PlaylistEntity
import paige.navic.data.database.mappers.toEntity
import paige.navic.data.session.SessionManager
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.cancellation.CancellationException

class DbRepository(
	private val albumDao: AlbumDao = DbContainer.albumDao,
	private val playlistDao: PlaylistDao = DbContainer.playlistDao,
	private val songDao: SongDao = DbContainer.songDao,
	private val api: SubsonicClient = SessionManager.api
) {
	private val concurrentRequestLimit = Semaphore(20)

	private suspend fun <T> runDbOp(block: suspend () -> T): Result<T> = withContext(Dispatchers.IO) {
		try {
			Result.success(block())
		} catch (e: Exception) {
			if (e is CancellationException) throw e
			Result.failure(e)
		}
	}

	suspend fun removeEverything(): Result<Unit> = runDbOp {
		albumDao.clearAllAlbums()
		playlistDao.clearAllPlaylists()
		songDao.clearAllSongs()
		println("Database wiped completely.")
	}

	suspend fun syncEverything(
		onProgress: (Float, String) -> Unit = { _, _ -> }
	): Result<Unit> = runDbOp {
		onProgress(0.0f, "Starting sync...")

		onProgress(0.05f, "Fetching playlists...")
		val playlists = syncPlaylists().getOrThrow()

		syncLibrarySongs { localProgress, message ->
			val globalProgress = 0.05f + (localProgress * 0.70f)
			onProgress(globalProgress, message)
		}.getOrThrow()

		val totalPlaylists = playlists.size
		if (totalPlaylists > 0) {
			playlists.forEachIndexed { index, playlist ->
				val globalProgress = 0.75f + (0.25f * ((index + 1).toFloat() / totalPlaylists))
				onProgress(globalProgress, "Syncing playlist: ${playlist.name}...")
				syncPlaylistSongs(playlist.playlistId).getOrThrow()
			}
		}

		onProgress(1.0f, "Sync complete!")
	}

	suspend fun syncLibrarySongs(
		onProgress: (Float, String) -> Unit = { _, _ -> }
	): Result<Int> = runDbOp {
		val pageSize = 500
		var offset = 0
		val allAlbumSummaries = mutableListOf<Album>()

		onProgress(0.0f, "Fetching album list...")
		while (true) {
			val batch = api.getAlbums(AlbumListType.AlphabeticalByName, pageSize, offset)
			if (batch.isEmpty()) break
			allAlbumSummaries.addAll(batch)
			if (batch.size < pageSize) break
			offset += pageSize
		}

		if (allAlbumSummaries.isEmpty()) return@runDbOp 0

		val totalAlbums = allAlbumSummaries.size
		val completedAlbums = AtomicInteger(0)

		onProgress(0.1f, "Fetching 0/$totalAlbums albums...")

		val fullAlbums = coroutineScope {
			allAlbumSummaries.map { summary ->
				async {
					concurrentRequestLimit.withPermit {
						val album = api.getAlbum(summary.id)
						val done = completedAlbums.incrementAndGet()

						if (done % 5 == 0 || done == totalAlbums) {
							val fetchProgress = 0.1f + (0.8f * (done.toFloat() / totalAlbums))
							onProgress(fetchProgress, "Fetching $done/$totalAlbums albums...")
						}
						album
					}
				}
			}.awaitAll()
		}

		onProgress(0.9f, "Saving library to database...")

		val albumEntities = fullAlbums.map { it.toEntity() }
		val songEntities = fullAlbums.flatMap { album ->
			album.songs.map { it.toEntity() }
		}

		albumDao.insertAlbums(albumEntities)
		songDao.insertSongs(songEntities)

		if (songEntities.isNotEmpty() || albumEntities.isNotEmpty()) {
			println("Sync Completed: ${albumEntities.size} albums, ${songEntities.size} songs")
		}

		onProgress(1.0f, "Library saved.")
		songEntities.size
	}

	suspend fun syncPlaylists(): Result<List<PlaylistEntity>> = runDbOp {
		val remotePlaylists = api.getPlaylists()

		if (remotePlaylists.isEmpty() && playlistDao.getPlaylistCount() > 0) {
			return@runDbOp emptyList()
		}

		val playlistEntities = remotePlaylists.map { it.toEntity() }
		val remoteIds = playlistEntities.map { it.playlistId }.toSet()
		val localPlaylists = playlistDao.getAllPlaylists()

		localPlaylists.forEach { local ->
			if (local.playlist.playlistId !in remoteIds) {
				playlistDao.deletePlaylist(local.playlist.playlistId)
			}
		}

		playlistDao.insertPlaylists(playlistEntities)
		println("Playlists Synced: ${playlistEntities.size} playlists found")

		playlistEntities
	}

	suspend fun syncPlaylistSongs(playlistId: String): Result<Int> = runDbOp {
		val playlist = api.getPlaylist(playlistId)
		val songs = playlist.songs
		val songEntities = songs.map { it.toEntity() }

		if (songEntities.isNotEmpty()) {
			songDao.insertSongs(songEntities)
		}

		println("Playlist [$playlistId] synced: ${songEntities.size} songs")
		songEntities.size
	}
}