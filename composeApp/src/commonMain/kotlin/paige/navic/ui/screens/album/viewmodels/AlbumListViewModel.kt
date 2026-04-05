package paige.navic.ui.screens.album.viewmodels

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zt64.subsonic.api.model.AlbumListType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import paige.navic.domain.repositories.AlbumRepository
import paige.navic.data.session.SessionManager
import paige.navic.domain.models.DomainAlbum
import paige.navic.utils.UiState

@OptIn(ExperimentalCoroutinesApi::class)
open class AlbumListViewModel(
	initialListType: AlbumListType = AlbumListType.AlphabeticalByArtist,
	private val repository: AlbumRepository,
) : ViewModel() {
	private val _albumsState = MutableStateFlow<UiState<List<DomainAlbum>>>(UiState.Loading())
	val albumsState = _albumsState.asStateFlow()

	private val _starredState = MutableStateFlow<UiState<Boolean>>(UiState.Success(false))
	val starredState = _starredState.asStateFlow()

	private val _selectedAlbum = MutableStateFlow<DomainAlbum?>(null)
	val selectedAlbum = _selectedAlbum.asStateFlow()

	private val _listType = MutableStateFlow(initialListType)
	val listType = _listType.asStateFlow()

	val gridState = LazyGridState()

	init {
		viewModelScope.launch {
			SessionManager.isLoggedIn.collect { if (it) refreshAlbums(false) }
		}
	}

	fun refreshAlbums(fullRefresh: Boolean) {
		viewModelScope.launch {
			repository.getAlbumsFlow(fullRefresh, _listType.value).collect {
				_albumsState.value = it
			}
		}
	}

	fun selectAlbum(album: DomainAlbum?) {
		viewModelScope.launch {
			_selectedAlbum.value = album
			if (album == null) return@launch
			_starredState.value = UiState.Loading()
			try {
				val isStarred = repository.isAlbumStarred(album)
				_starredState.value = UiState.Success(isStarred)
			} catch(e: Exception) {
				_starredState.value = UiState.Error(e)
			}
		}
	}

	fun starAlbum(starred: Boolean) {
		viewModelScope.launch {
			val selection = _selectedAlbum.value ?: return@launch
			runCatching {
				if (starred) {
					repository.starAlbum(selection)
				} else {
					repository.unstarAlbum(selection)
				}
			}
		}
	}

	fun setListType(listType: AlbumListType) {
		_listType.value = listType
	}

	fun clearError() {
		_albumsState.value = UiState.Success(_albumsState.value.data.orEmpty())
	}
}