package paige.navic.data.repositories

import kotlinx.coroutines.flow.Flow
import paige.navic.data.database.DbContainer
import paige.navic.data.database.dao.PlaylistDao
import paige.navic.data.database.dao.SongDao
import paige.navic.data.database.entities.PlaylistEntity
import paige.navic.data.database.entities.SongEntity
import paige.navic.data.database.mappers.toEntity
import paige.navic.data.session.SessionManager

class PlaylistsRepository(
	private val playlistDao: PlaylistDao = DbContainer.playlistDao,
	private val songDao: SongDao = DbContainer.songDao,
) {

	fun getPlaylistsFlow(): Flow<List<PlaylistEntity>> = playlistDao.getAllPlaylists()

	suspend fun refreshPlaylists() {
		val remotePlaylists = SessionManager.api.getPlaylists()
		playlistDao.insertPlaylists(remotePlaylists.map { it.toEntity() })
	}

	suspend fun getSongsByPlaylistId(playlistId: String): List<SongEntity> {
		val localSongs = songDao.getSongListByPlaylistId(playlistId)

		return localSongs.ifEmpty {
			val remoteSongs = SessionManager.api.getPlaylist(playlistId).songs
			val entities = remoteSongs.map { it.toEntity() }
			songDao.insertSongs(entities)
			entities
		}
	}
}