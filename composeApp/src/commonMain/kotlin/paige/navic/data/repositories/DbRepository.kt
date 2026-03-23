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
import paige.navic.data.database.entities.toEntity
import paige.navic.data.session.SessionManager
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
		coroutineScope {
			onProgress(0.1f, "Fetching library songs...")
			val libraryDeferred = async { syncLibrarySongs() }

			val playlistsDeferred = async { syncPlaylists() }

			val libraryResult = libraryDeferred.await()
			if (libraryResult.isFailure) throw libraryResult.exceptionOrNull()!!
			onProgress(0.5f, "Library synced. Fetching playlists...")

			val playlistsResult = playlistsDeferred.await()
			if (playlistsResult.isFailure) throw playlistsResult.exceptionOrNull()!!
			onProgress(0.7f, "Syncing playlist tracks...")

			val playlists = playlistsResult.getOrNull() ?: emptyList()
			val totalPlaylists = playlists.size

			if (totalPlaylists > 0) {
				playlists.forEachIndexed { index, playlist ->
					syncPlaylistSongs(playlist.id)
					val currentProgress = 0.7f + (0.3f * ((index + 1).toFloat() / totalPlaylists))
					onProgress(currentProgress, "Syncing playlist: ${playlist.name}...")
				}
			}

			onProgress(1.0f, "Sync complete!")
		}
	}

	suspend fun syncLibrarySongs(): Result<Int> = runDbOp {
		val pageSize = 500
		var offset = 0
		val allAlbumSummaries = mutableListOf<Album>()

		while (true) {
			val batch = api.getAlbums(AlbumListType.AlphabeticalByName, pageSize, offset)
			if (batch.isEmpty()) break
			allAlbumSummaries.addAll(batch)
			if (batch.size < pageSize) break
			offset += pageSize
		}

		if (allAlbumSummaries.isEmpty()) return@runDbOp 0

		val fullAlbums = coroutineScope {
			allAlbumSummaries.map { summary ->
				async {
					concurrentRequestLimit.withPermit {
						api.getAlbum(summary.id)
					}
				}
			}.awaitAll()
		}

		val albumEntities = fullAlbums.map { it.toEntity() }
		val songEntities = fullAlbums.flatMap { album ->
			album.songs.map { it.toEntity(playlistId = "__library__") }
		}

		albumDao.insertAlbums(albumEntities)
		songDao.insertSongs(songEntities)

		if (songEntities.isNotEmpty() || albumEntities.isNotEmpty()) {
			println("Sync Completed: ${albumEntities.size} albums, ${songEntities.size} songs")
		}

		songEntities.size
	}

	suspend fun syncPlaylists(): Result<List<PlaylistEntity>> = runDbOp {
		val remotePlaylists = api.getPlaylists()

		if (remotePlaylists.isEmpty() && playlistDao.getPlaylistCount() > 0) {
			return@runDbOp emptyList()
		}

		val playlistEntities = remotePlaylists.map { it.toEntity() }
		val remoteIds = playlistEntities.map { it.id }.toSet()
		val localPlaylists = playlistDao.getAllPlaylistsList()

		localPlaylists.forEach { local ->
			if (local.id !in remoteIds) {
				playlistDao.deletePlaylist(local.id)
				songDao.deleteSongsByPlaylist(local.id)
			}
		}

		playlistDao.insertPlaylists(playlistEntities)
		println("Playlists Synced: ${playlistEntities.size} playlists found")

		playlistEntities
	}

	suspend fun syncPlaylistSongs(playlistId: String): Result<Int> = runDbOp {
		val playlist = api.getPlaylist(playlistId)
		val songs = playlist.songs
		val songEntities = songs.map { it.toEntity(playlistId = playlistId) }

		if (songEntities.isNotEmpty()) {
			songDao.deleteSongsByPlaylist(playlistId)
			songDao.insertSongs(songEntities)
		}

		println("Playlist [$playlistId] synced: ${songEntities.size} songs")
		songEntities.size
	}
}