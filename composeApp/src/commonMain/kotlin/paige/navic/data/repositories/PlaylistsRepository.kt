package paige.navic.data.repositories

import kotlinx.coroutines.flow.Flow
import paige.navic.data.database.DbContainer
import paige.navic.data.database.dao.PlaylistDao
import paige.navic.data.database.mappers.toEntity
import paige.navic.data.database.relations.PlaylistWithSongs
import paige.navic.data.session.SessionManager

class PlaylistsRepository(
	private val playlistDao: PlaylistDao = DbContainer.playlistDao,
) {

	fun getPlaylistsFlow(): Flow<List<PlaylistWithSongs>> = playlistDao.getAllPlaylistsFlow()

	suspend fun refreshPlaylists() {
		val remotePlaylists = SessionManager.api.getPlaylists()
		playlistDao.insertPlaylists(remotePlaylists.map { it.toEntity() })
	}
}