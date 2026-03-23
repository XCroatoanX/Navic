package paige.navic.ui.screens.album.viewmodels

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zt64.subsonic.api.model.AlbumListType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import paige.navic.data.database.entities.AlbumEntity
import paige.navic.data.database.mappers.toDomainModel
import paige.navic.data.models.TrackCollectionUiModel
import paige.navic.data.repositories.AlbumsRepository
import paige.navic.data.session.SessionManager
import paige.navic.utils.UiState

@OptIn(ExperimentalCoroutinesApi::class)
open class AlbumListViewModel(
    initialListType: AlbumListType?,
    private val repository: AlbumsRepository = AlbumsRepository()
) : ViewModel() {
	private val _albumsState = MutableStateFlow<UiState<List<AlbumEntity>>>(UiState.Loading)
	val albumsState = _albumsState.asStateFlow()

	private val _isRefreshing = MutableStateFlow(false)
	val isRefreshing = _isRefreshing.asStateFlow()

	private val _starredState = MutableStateFlow<UiState<Boolean>>(UiState.Success(false))
	val starredState = _starredState.asStateFlow()

	private val _selectedAlbum = MutableStateFlow<AlbumEntity?>(null)
	val selectedAlbum = _selectedAlbum.asStateFlow()

	private val _offset = MutableStateFlow(0)
	private val _isPaginating = MutableStateFlow(false)
	val isPaginating: StateFlow<Boolean> = _isPaginating

	private val _listType = MutableStateFlow(initialListType ?: AlbumListType.AlphabeticalByArtist)
	val listType = _listType.asStateFlow()

	val gridState = LazyGridState()

	init {
		viewModelScope.launch {
			combine(_offset, _listType) { offset, type ->
				type to offset
			}.flatMapLatest { (type, offset) ->
				repository.getAlbumsFlow(offset, type)
			}.collect { dbAlbums ->
				_isPaginating.value = false

				if (dbAlbums.isNotEmpty()) {
					_albumsState.value = UiState.Success(dbAlbums)
				}
			}
		}

		viewModelScope.launch {
			SessionManager.isLoggedIn.collect { if (it) refreshAlbums() }
		}
	}

	fun refreshAlbums() {
		viewModelScope.launch {
			_offset.value = 0
			val hasData = (_albumsState.value as? UiState.Success)?.data?.isNotEmpty() == true

			if (hasData) _isRefreshing.value = true else _albumsState.value = UiState.Loading

			try {
				repository.syncAlbums(_listType.value, _offset.value)
			} catch (e: Exception) {
				_albumsState.value = UiState.Error(e)
			} finally {
				_isRefreshing.value = false
			}
		}
	}

	suspend fun getAlbumTracks(album: AlbumEntity): TrackCollectionUiModel {
		val songs = repository.getSongsByAlbumId(album.id)
		print(songs)
		return album.toDomainModel(songs)
	}

	fun paginate() {
		if (_isPaginating.value || _albumsState.value !is UiState.Success) return

		val currentDataSize = (_albumsState.value as UiState.Success).data.size
		val expectedSize = _offset.value + 30

		if (currentDataSize < expectedSize) return

		_isPaginating.value = true
		_offset.value += 30
	}

	fun selectAlbum(album: AlbumEntity?) {
		viewModelScope.launch {
			_selectedAlbum.value = album
			if (album == null) return@launch
			_starredState.value = UiState.Loading
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
}