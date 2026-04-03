package paige.navic.ui.screens.track.viewmodels

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zt64.subsonic.api.model.AlbumInfo
import dev.zt64.subsonic.api.model.Artist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import paige.navic.domain.models.DomainSongCollection
import paige.navic.data.repositories.TracksRepository
import paige.navic.data.session.SessionManager
import paige.navic.domain.models.DomainAlbum
import paige.navic.domain.models.DomainSong
import paige.navic.utils.UiState

class TrackListViewModel(
    private val partialCollection: DomainSongCollection,
    private val repository: TracksRepository
) : ViewModel() {
	private val _tracksState = MutableStateFlow<UiState<DomainSongCollection>>(UiState.Loading())
	val tracksState: StateFlow<UiState<DomainSongCollection>> = _tracksState.asStateFlow()

	private val _selectedTrack = MutableStateFlow<DomainSong?>(null)
	val selectedTrack: StateFlow<DomainSong?> = _selectedTrack.asStateFlow()

	private val _selectedIndex = MutableStateFlow<Int?>(null)
	val selectedIndex: StateFlow<Int?> = _selectedIndex.asStateFlow()

	private val _albumInfoState = MutableStateFlow<UiState<AlbumInfo>>(UiState.Loading())
	val albumInfoState = _albumInfoState.asStateFlow()

	private val _starredState = MutableStateFlow<UiState<Boolean>>(UiState.Success(false))
	val starredState = _starredState.asStateFlow()

	private val _artistState = MutableStateFlow<UiState<Artist>>(UiState.Loading())
	val artistState = _artistState.asStateFlow()

	val listState = LazyListState()

	init {
		viewModelScope.launch {
			SessionManager.isLoggedIn.collect {
				refreshTracks()
				refreshArtist()
			}
		}
	}

	fun refreshTracks() {
		viewModelScope.launch {
			_tracksState.value = UiState.Loading()
			try {
				val localCollection = repository.fetchWithAllTracks(partialCollection)
				_tracksState.value = UiState.Success(localCollection)

				if (localCollection is DomainAlbum) {
					try {
						val albumInfo = repository.getAlbumInfo(localCollection.id)
						_albumInfoState.value = UiState.Success(albumInfo)
					} catch (e: Exception) {
						_albumInfoState.value = UiState.Error(e)
					}
				} else {
					_albumInfoState.value = UiState.Error(Exception("No album info for playlists"))
				}
			} catch (e: Exception) {
				_tracksState.value = UiState.Error(e)
			}
		}
	}

	fun refreshArtist() {
		if (partialCollection is DomainAlbum) {
			viewModelScope.launch {
				_artistState.value = UiState.Loading()
				try {
					_artistState.value = UiState.Success(SessionManager.api.getArtist(partialCollection.id))
				} catch (e: Exception) {
					e.printStackTrace()
					_artistState.value = UiState.Error(e)
				}
			}
		} else {
			_artistState.value = UiState.Error(Exception("do something here idk"))
		}
	}

	fun selectTrack(track: DomainSong, index: Int) {
		viewModelScope.launch {
			_selectedTrack.value = track
			_selectedIndex.value = index
			_starredState.value = UiState.Loading()
			_albumInfoState.value = UiState.Loading()
			try {
				val isStarred = repository.isTrackStarred(track.id)
				_starredState.value = UiState.Success(isStarred)
			} catch(e: Exception) {
				_starredState.value = UiState.Error(e)
			}
		}
	}

	fun clearSelection() {
		_selectedTrack.value = null
		_selectedIndex.value = null
	}

	fun removeFromPlaylist() {
		val selection = _selectedIndex.value ?: return
		clearSelection()
		viewModelScope.launch {
			try {
				SessionManager.api.updatePlaylist(
					id = partialCollection.id,
					songIndicesToRemove = listOf(selection)
				)
				refreshTracks()
			} catch (_: Exception) { }
		}
	}

	fun starSelectedTrack() {
		viewModelScope.launch {
			try {
				repository.starTrack(_selectedTrack.value!!)
			} catch(_: Exception) { }
		}
	}

	fun unstarSelectedTrack() {
		viewModelScope.launch {
			try {
				repository.unstarTrack(_selectedTrack.value!!)
			} catch(_: Exception) { }
		}
	}
}