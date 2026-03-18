package paige.navic.data.repositories

import dev.zt64.subsonic.api.model.Album
import dev.zt64.subsonic.api.model.AlbumListType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import paige.navic.data.database.DatabaseDao
import paige.navic.data.database.PlaylistEntity
import paige.navic.data.database.toEntity
import paige.navic.data.session.SessionManager
import kotlin.coroutines.cancellation.CancellationException

class DbRepository(
	private val musicDao: DatabaseDao
) {
	private val api = SessionManager.api
	private val concurrentRequestLimit = Semaphore(20)

	suspend fun syncEverything(): Result<Unit> = withContext(Dispatchers.IO) {
		try {
			syncLibrarySongs()

			val playlistsResult = syncPlaylists()
			if (playlistsResult.isSuccess) {
				val playlists = playlistsResult.getOrNull() ?: emptyList()
				playlists.forEach { playlist ->
					syncPlaylistSongs(playlist.id)
				}
			}

			Result.success(Unit)
		} catch (e: Exception) {
			if (e is CancellationException) throw e
			Result.failure(e)
		}
	}

	suspend fun syncLibrarySongs(): Result<Int> = withContext(Dispatchers.IO) {

		try {
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

			if (allAlbumSummaries.isEmpty()) return@withContext Result.success(0)

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

			musicDao.insertAlbums(albumEntities)
			musicDao.insertSongs(songEntities)

			if (songEntities.isNotEmpty() || albumEntities.isNotEmpty()) {
				println("Sync Completed: ${albumEntities.size} albums, ${songEntities.size} songs")
			}

			Result.success(songEntities.size)
		} catch (e: Exception) {
			if (e is CancellationException) throw e
			Result.failure(e)
		}
	}

	suspend fun syncPlaylists(): Result<List<PlaylistEntity>> = withContext(Dispatchers.IO) {
		try {
			val remotePlaylists = api.getPlaylists()

			if (remotePlaylists.isEmpty() && musicDao.getPlaylistCount() > 0) {
				return@withContext Result.success(emptyList())
			}

			val playlistEntities = remotePlaylists.map { it.toEntity() }
			val remoteIds = playlistEntities.map { it.id }.toSet()
			val localPlaylists = musicDao.getAllPlaylistsList()

			localPlaylists.forEach { local ->
				if (local.id !in remoteIds) {
					musicDao.deletePlaylist(local.id)
					musicDao.deleteSongsByPlaylist(local.id)
				}
			}

			musicDao.insertPlaylists(playlistEntities)
			println("Playlists Synced: ${playlistEntities.size} playlists found")

			Result.success(playlistEntities)
		} catch (e: Exception) {
			if (e is CancellationException) throw e
			Result.failure(e)
		}
	}

	suspend fun syncPlaylistSongs(playlistId: String): Result<Int> = withContext(Dispatchers.IO) {
		try {
			val playlist = api.getPlaylist(playlistId)
			val songs = playlist.songs
			val songEntities = songs.map { it.toEntity(playlistId = playlistId) }

			if (songEntities.isNotEmpty()) {
				musicDao.deleteSongsByPlaylist(playlistId)
				musicDao.insertSongs(songEntities)
			}

			println("Playlist [$playlistId] synced: ${songEntities.size} songs")
			Result.success(songEntities.size)
		} catch (e: Exception) {
			if (e is CancellationException) throw e
			Result.failure(e)
		}
	}
}