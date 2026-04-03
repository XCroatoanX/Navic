package paige.navic.domain.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import paige.navic.data.database.dao.PlaylistDao
import paige.navic.data.database.mappers.toDomainModel
import paige.navic.data.session.SessionManager
import paige.navic.domain.models.DomainPlaylist
import paige.navic.utils.UiState

class PlaylistRepository(
    private val playlistDao: PlaylistDao = DbContainer.playlistDao
) {
	fun getPlaylistsFlow(): Flow<UiState<List<DomainPlaylist>>> = flow {
		val localData = playlistDao
			.getAllPlaylists()
			.map { it.toDomainModel() }
		emit(UiState.Loading(data = localData))
		try {
			val remoteData = SessionManager.api.getPlaylists()
				.mapNotNull { playlistDao.getPlaylistById(it.id)?.toDomainModel() }
			emit(UiState.Success(data = remoteData))
		} catch (error: Exception) {
			emit(UiState.Error(error = error, data = localData))
		}
	}.flowOn(Dispatchers.IO)
}
