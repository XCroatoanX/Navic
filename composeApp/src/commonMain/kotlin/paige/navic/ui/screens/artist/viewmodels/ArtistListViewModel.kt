package paige.navic.ui.screens.artist.viewmodels

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import paige.navic.data.database.mappers.toDomainModel
import paige.navic.data.repositories.ArtistListType
import paige.navic.data.repositories.ArtistsRepository
import paige.navic.data.session.SessionManager
import paige.navic.domain.models.DomainArtist
import paige.navic.utils.UiState

class ArtistListViewModel(
	private val repository: ArtistsRepository
) : ViewModel() {
	private val _artistsState = MutableStateFlow<UiState<List<DomainArtist>>>(UiState.Loading())
	val artistsState = _artistsState.asStateFlow()

	private val _starredState = MutableStateFlow<UiState<Boolean>>(UiState.Success(false))
	val starredState = _starredState.asStateFlow()

	private val _selectedArtist = MutableStateFlow<DomainArtist?>(null)
	val selectedArtist: StateFlow<DomainArtist?> = _selectedArtist.asStateFlow()

	val gridState = LazyGridState()

	private var artistsJob: Job? = null

	init {
		viewModelScope.launch {
			SessionManager.isLoggedIn.collect { isLoggedIn ->
				if (isLoggedIn) {
					syncRemoteArtists()
					refreshArtists()
				}
			}
		}
	}

	private fun syncRemoteArtists() {
		viewModelScope.launch {
			try {
				repository.syncArtists()
			} catch (e: Exception) {
			}
		}
	}

	fun refreshArtists(
		offset: Int = 0,
		listType: ArtistListType = ArtistListType.AlphabeticalByName
	) {
		artistsJob?.cancel()

		artistsJob = viewModelScope.launch {
			_artistsState.value = UiState.Loading()
			try {
				repository.getArtistsFlow(offset, listType).collect { entities ->
					val domainArtists = entities.map { it.toDomainModel() }
					_artistsState.value = UiState.Success(domainArtists)
				}
			} catch (e: Exception) {
				_artistsState.value = UiState.Error(e)
			}
		}
	}

	fun selectArtist(artist: DomainArtist) {
		viewModelScope.launch {
			_selectedArtist.value = artist
			_starredState.value = UiState.Loading()
			try {
				val isStarred = repository.isArtistStarred(artist)
				_starredState.value = UiState.Success(isStarred)
			} catch(e: Exception) {
				_starredState.value = UiState.Error(e)
			}
		}
	}

	fun clearSelection() {
		_selectedArtist.value = null
	}

	fun starSelectedArtist() {
		val artist = _selectedArtist.value ?: return
		viewModelScope.launch {
			try {
				repository.starArtist(artist)
				_starredState.value = UiState.Success(true)
			} catch(_: Exception) { }
		}
	}

	fun unstarSelectedArtist() {
		val artist = _selectedArtist.value ?: return
		viewModelScope.launch {
			try {
				repository.unstarArtist(artist)
				_starredState.value = UiState.Success(false)
			} catch(_: Exception) { }
		}
	}
}