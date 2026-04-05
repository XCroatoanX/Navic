package paige.navic.domain.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import paige.navic.data.database.dao.PlaylistDao
import paige.navic.data.database.mappers.toDomainModel
import paige.navic.data.database.mappers.toEntity
import paige.navic.data.session.SessionManager
import paige.navic.domain.models.DomainPlaylist
import paige.navic.utils.UiState

class PlaylistRepository(
	private val playlistDao: PlaylistDao
) {
	private suspend fun getLocalData(): List<DomainPlaylist> {
		return playlistDao
			.getAllPlaylists()
			.map { it.toDomainModel() }
	}

	private suspend fun refreshLocalData(): List<DomainPlaylist> {
		val remotePlaylists = SessionManager.api.getPlaylists()
		val playlistEntities = remotePlaylists.map { it.toEntity() }
		playlistDao.updateAllPlaylists(playlistEntities)
		return getLocalData()
	}

	fun getPlaylistsFlow(): Flow<UiState<List<DomainPlaylist>>> = flow {
		val localData = getLocalData()
		emit(UiState.Loading(data = localData))
		try {
			emit(UiState.Success(data = refreshLocalData()))
		} catch (error: Exception) {
			emit(UiState.Error(error = error, data = localData))
		}
	}.flowOn(Dispatchers.IO)
}
