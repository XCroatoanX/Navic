package paige.navic.ui.screens.genre.viewmodels

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import paige.navic.data.database.relations.GenreWithAlbums
import paige.navic.domain.repositories.GenreRepository
import paige.navic.data.session.SessionManager
import paige.navic.utils.UiState

class GenreListViewModel(
	private val repository: GenreRepository
) : ViewModel() {

	private val _isRefreshing = MutableStateFlow(false)
	val isRefreshing = _isRefreshing.asStateFlow()

	private val _genresState = MutableStateFlow<UiState<List<GenreWithAlbums>>>(UiState.Loading())
	val genresState = _genresState.asStateFlow()

	val gridState = LazyGridState()

	init {
		viewModelScope.launch {
			repository.getGenresWithAlbumsFlow().collect { dbGenres ->
				if (dbGenres.isNotEmpty()) {
					_genresState.value = UiState.Success(dbGenres)
				}
			}
		}

		viewModelScope.launch {
			SessionManager.isLoggedIn.collect { if (it) refreshGenres() }
		}
	}

	fun refreshGenres() {
		viewModelScope.launch {
			val hasData = (_genresState.value as? UiState.Success)?.data?.isNotEmpty() == true

			if (hasData) {
				_isRefreshing.value = true
			} else {
				_genresState.value = UiState.Loading()
			}

			try {
				repository.syncGenres()
			} catch (e: Exception) {
				if (!hasData) {
					_genresState.value = UiState.Error(e)
				}
			} finally {
				_isRefreshing.value = false
			}
		}
	}
}