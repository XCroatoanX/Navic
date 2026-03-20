package paige.navic.data.models

import paige.navic.data.database.SongEntity
import kotlin.time.Duration

data class TrackCollectionUiModel(
	val id: String,
	val name: String,
	val coverArtId: String?,
	val duration: Duration?,
	val songCount: Int,
	val isAlbum: Boolean,
	val songs: List<SongEntity> = emptyList()
)