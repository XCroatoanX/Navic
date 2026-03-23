package paige.navic.ui.screens.playlist.viewmodels

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import paige.navic.data.database.entities.PlaylistEntity
import paige.navic.data.database.mappers.toDomainModel
import paige.navic.domain.models.DomainSongCollection
import paige.navic.data.models.settings.Settings
import paige.navic.data.repositories.PlaylistsRepository
import paige.navic.data.session.SessionManager
import paige.navic.utils.UiState
import paige.navic.utils.sortedByMode

class PlaylistListViewModel(
	private val repository: PlaylistsRepository = PlaylistsRepository()
) : ViewModel() {
	private val _playlistsState = MutableStateFlow<UiState<List<PlaylistEntity>>>(UiState.Loading)
	val playlistsState = _playlistsState.asStateFlow()

	private val _isRefreshing = MutableStateFlow(false)
	val isRefreshing = _isRefreshing.asStateFlow()

	private val _selectedPlaylist = MutableStateFlow<PlaylistEntity?>(null)
	val selectedPlaylist = _selectedPlaylist.asStateFlow()

	val gridState = LazyGridState()

	init {
		viewModelScope.launch {
			repository.getPlaylistsFlow().collect { dbPlaylists ->
				if (dbPlaylists.isNotEmpty()) {
					_playlistsState.value = UiState.Success(dbPlaylists)
					sortPlaylists()
				}
			}
		}
		viewModelScope.launch {
			SessionManager.isLoggedIn.collect { if (it) refreshPlaylists() }
		}
	}

	fun selectPlaylist(playlist: PlaylistEntity) {
		_selectedPlaylist.value = playlist
	}

	fun clearSelection() {
		_selectedPlaylist.value = null
	}

	fun refreshPlaylists() {
		viewModelScope.launch {
			val hasData = (_playlistsState.value as? UiState.Success)?.data?.isNotEmpty() == true
			if (hasData) _isRefreshing.value = true else _playlistsState.value = UiState.Loading

			try {
				repository.refreshPlaylists()
			} catch (e: Exception) {
				_playlistsState.value = UiState.Error(e)
			} finally {
				_isRefreshing.value = false
			}
		}
	}

	fun sortPlaylists() {
		val currentState = _playlistsState.value as? UiState.Success ?: return
		val sorted = currentState.data.sortedByMode(
			Settings.shared.playlistSortMode,
			Settings.shared.playlistsReversed
		)
		_playlistsState.value = UiState.Success(sorted)
	}

	suspend fun getPlaylistTracks(playlist: PlaylistEntity): DomainSongCollection {
		val songs = repository.getSongsByPlaylistId(playlist.id)
		print(songs)
		return playlist.toDomainModel(songs)
	}
}