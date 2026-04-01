package paige.navic.shared

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import paige.navic.domain.models.DomainSong
import paige.navic.domain.models.DomainSongCollection
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import paige.navic.data.repositories.TracksRepository
import kotlin.time.Clock

@Serializable
data class PlayerUiState(
    val queue: List<DomainSong> = emptyList(),
    val currentTrack: DomainSong? = null,
    val currentCollection: DomainSongCollection? = null,
    val currentIndex: Int = -1,
    val isPaused: Boolean = false,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: Int = 0,
    val progress: Float = 0f,
    val isLoading: Boolean = false
)

abstract class MediaPlayerViewModel(
	private val storage: PlayerStateStorage
) : ViewModel() {
	protected val _uiState = MutableStateFlow(PlayerUiState())
	val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()
	private val tracksRepository: TracksRepository = TracksRepository()

	init {
		viewModelScope.launch {
			restoreState()
			observeAndSaveState()
		}
	}

	abstract  fun addToQueueSingle(track: DomainSong)
	abstract  fun addToQueue(tracks: DomainSongCollection)
	abstract fun removeFromQueue(index: Int)
	abstract fun moveQueueItem(fromIndex: Int, toIndex: Int)
	abstract fun clearQueue()
	abstract fun playAt(index: Int)
	abstract fun pause()
	abstract fun resume()
	abstract fun seek(normalized: Float)
	abstract fun next()
	abstract fun previous()
	abstract fun toggleShuffle()
	abstract fun toggleRepeat()
	abstract fun shufflePlay(tracks: DomainSongCollection)

	fun togglePlay() {
		if (!_uiState.value.isPaused) {
			pause()
		} else {
			resume()
		}
	}

	fun starTrack() {
		val track = _uiState.value.currentTrack ?: return

		viewModelScope.launch {
			_uiState.value = _uiState.value.copy(
				currentTrack = track.copy(starredAt = Clock.System.now())
			)

			tracksRepository.starTrack(track)
		}
	}

	fun unstarTrack() {
		val track = _uiState.value.currentTrack ?: return

		viewModelScope.launch {
			_uiState.value = _uiState.value.copy(
				currentTrack = track.copy(starredAt = null)
			)

			tracksRepository.unstarTrack(track)
		}
	}

	abstract fun syncPlayerWithState(state: PlayerUiState)

	private suspend fun restoreState() {
		val savedJson = storage.loadState()
		if (!savedJson.isNullOrBlank()) {
			try {
				val restoredState = Json.decodeFromJsonElement<PlayerUiState>(
					Json
						.parseToJsonElement(savedJson)
						.filterKeys("genres")
				)
				val stateToApply = restoredState.copy(isPaused = true, isLoading = false)

				_uiState.value = stateToApply

				syncPlayerWithState(stateToApply)

			} catch (e: Exception) {
				e.printStackTrace()
				println("Failed to restore state: ${e.message}")
				_uiState.value = PlayerUiState()
			}
		}
	}

	// TODO: shitty temporary workaround for bug in subsonic-kotlin, remove when fixed upstream
	// see https://canary.discord.com/channels/1468073950016835709/1468074620631519232/1486078368494522621
	private fun JsonElement.filterKeys(targetKey: String): JsonElement {
		return when (this) {
			is JsonObject -> {
				val filteredMap = this.filter { it.key != targetKey }
					.mapValues { it.value.filterKeys(targetKey) }
				JsonObject(filteredMap)
			}
			is JsonArray -> {
				JsonArray(this.map { it.filterKeys(targetKey) })
			}
			else -> this
		}
	}

	@OptIn(FlowPreview::class)
	private fun observeAndSaveState() {
		viewModelScope.launch {
			_uiState
				.debounce(1000L)
				.collect { state ->
					try {
						val jsonString = Json.encodeToString(state)
						storage.saveState(jsonString)
					} catch (e: Exception) {
						e.printStackTrace()
						println("Failed to save state: ${e.message}")
					}
				}
		}
	}
}

@Composable
expect fun rememberMediaPlayer(): MediaPlayerViewModel