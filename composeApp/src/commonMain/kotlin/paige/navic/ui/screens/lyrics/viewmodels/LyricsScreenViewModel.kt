package paige.navic.ui.screens.lyrics.viewmodels

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zt64.subsonic.api.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import paige.navic.data.repositories.LyricsRepository
import paige.navic.data.repositories.LyricsResult
import paige.navic.utils.UiState

class LyricsScreenViewModel(
    private val track: Song?,
    private val repository: LyricsRepository = LyricsRepository()
) : ViewModel() {
	private val _lyricsState = MutableStateFlow<UiState<LyricsResult?>>(UiState.Loading)
	val lyricsState = _lyricsState.asStateFlow()

	val listState = LazyListState()

	init {
		refreshResults()
	}

	fun refreshResults() {
		viewModelScope.launch {
			if (track == null) {
				_lyricsState.value = UiState.Success(null)
				return@launch
			}
			_lyricsState.value = UiState.Loading
			try {
				_lyricsState.value = UiState.Success(
					repository.fetchLyrics(track)
				)
			} catch (e: Exception) {
				_lyricsState.value = UiState.Error(e)
			}
		}
	}
}