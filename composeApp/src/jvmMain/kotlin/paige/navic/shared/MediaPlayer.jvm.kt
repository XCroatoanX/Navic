package paige.navic.shared

import paige.navic.domain.repositories.TrackRepository
import paige.navic.domain.models.DomainSong
import paige.navic.domain.models.DomainSongCollection

// TODO: implement this class
class JvmMediaPlayerViewModel(
	storage: PlayerStateStorage,
	tracksRepository: TrackRepository
) : MediaPlayerViewModel(storage, tracksRepository) {
	override fun addToQueueSingle(track: DomainSong) {
	}

	override fun addToQueue(tracks: DomainSongCollection) {
	}

	override fun removeFromQueue(index: Int) {
	}

	override fun moveQueueItem(fromIndex: Int, toIndex: Int) {
	}

	override fun clearQueue() {
	}

	override fun playAt(index: Int) {
	}

	override fun pause() {
	}

	override fun resume() {
	}

	override fun seek(normalized: Float) {
	}

	override fun next() {
	}

	override fun previous() {
	}

	override fun toggleShuffle() {
	}

	override fun toggleRepeat() {
	}

	override fun shufflePlay(tracks: DomainSongCollection) {
	}

	override fun syncPlayerWithState(state: PlayerUiState) {
	}
}
