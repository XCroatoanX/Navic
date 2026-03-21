package paige.navic.ui.viewmodels

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zt64.subsonic.api.model.Album
import dev.zt64.subsonic.api.model.AlbumInfo
import dev.zt64.subsonic.api.model.Artist
import dev.zt64.subsonic.api.model.SongCollection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import paige.navic.data.database.AlbumEntity
import paige.navic.data.database.SongEntity
import paige.navic.data.models.TrackCollectionUiModel
import paige.navic.data.repositories.TracksRepository
import paige.navic.data.session.SessionManager
import paige.navic.utils.UiState

class TracksViewModel(
	private val partialCollection: TrackCollectionUiModel,
	private val repository: TracksRepository = TracksRepository()
) : ViewModel() {
	private val _tracksState = MutableStateFlow<UiState<TrackCollectionUiModel>>(UiState.Loading)
	val tracksState: StateFlow<UiState<TrackCollectionUiModel>> = _tracksState.asStateFlow()

	private val _selectedTrack = MutableStateFlow<SongEntity?>(null)
	val selectedTrack: StateFlow<SongEntity?> = _selectedTrack.asStateFlow()

	private val _selectedIndex = MutableStateFlow<Int?>(null)
	val selectedIndex: StateFlow<Int?> = _selectedIndex.asStateFlow()

	private val _albumInfoState = MutableStateFlow<UiState<AlbumInfo>>(UiState.Loading)
	val albumInfoState = _albumInfoState.asStateFlow()

	private val _starredState = MutableStateFlow<UiState<Boolean>>(UiState.Success(false))
	val starredState = _starredState.asStateFlow()

	private val _artistState = MutableStateFlow<UiState<Artist>>(UiState.Loading)
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
			_tracksState.value = UiState.Loading
			try {
				val localCollection = repository.fetchWithAllTracks(partialCollection)

				if (localCollection != null) {
					_tracksState.value = UiState.Success(localCollection)

					if (localCollection.isAlbum) {
						try {
							val albumInfo = repository.getAlbumInfo(localCollection.id)
							_albumInfoState.value = UiState.Success(albumInfo)
						} catch (e: Exception) {
							_albumInfoState.value = UiState.Error(e)
						}
					} else {
						_albumInfoState.value = UiState.Error(Exception("No album info for playlists"))
					}
				} else {
					_tracksState.value = UiState.Error(Exception("Collection not found in local database"))
				}
			} catch (e: Exception) {
				_tracksState.value = UiState.Error(e)
			}
		}
	}

	fun refreshArtist() {
		if (partialCollection.isAlbum) {
			viewModelScope.launch {
				_artistState.value = UiState.Loading
				try {
					_artistState.value = UiState.Success(SessionManager.api.getArtist(partialCollection.id))
				} catch (e: Exception) {
					e.printStackTrace()
					_artistState.value = UiState.Error(e)
				}
			}
		} else {
			// Free the UI from the loading state if it's a playlist
			_artistState.value = UiState.Error(Exception("Not an album, no artist to fetch"))
			// Or use a default/empty Success state depending on your UI logic
		}
	}

	fun selectTrack(track: SongEntity, index: Int) {
		viewModelScope.launch {
			_selectedTrack.value = track
			_selectedIndex.value = index
			_starredState.value = UiState.Loading
			_albumInfoState.value = UiState.Loading
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