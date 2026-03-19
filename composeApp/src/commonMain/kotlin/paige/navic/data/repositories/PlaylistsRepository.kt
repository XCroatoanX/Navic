package paige.navic.data.repositories

import kotlinx.coroutines.flow.Flow
import paige.navic.data.database.DatabaseDao
import paige.navic.data.database.DbContainer
import paige.navic.data.database.PlaylistEntity
import paige.navic.data.database.toEntity
import paige.navic.data.session.SessionManager

class PlaylistsRepository(
	private val dao: DatabaseDao = DbContainer.dao
) {
//	suspend fun getPlaylists(): List<Playlist> = SessionManager.api.getPlaylists()

	fun getPlaylistsFlow(): Flow<List<PlaylistEntity>> = dao.getAllPlaylists()

	suspend fun refreshPlaylists() {
		val remotePlaylists = SessionManager.api.getPlaylists()
		dao.insertPlaylists(remotePlaylists.map { it.toEntity() })
	}
}