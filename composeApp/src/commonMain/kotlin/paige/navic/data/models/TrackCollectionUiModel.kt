package paige.navic.data.models

import kotlinx.serialization.Serializable
import paige.navic.data.database.entities.SongEntity
import kotlin.time.Duration

@Serializable
data class TrackCollectionUiModel(
	val id: String,
	val name: String,
	val coverArtId: String?,
	val duration: Duration?,
	val songCount: Int,
	val isAlbum: Boolean,
	val songs: List<SongEntity> = emptyList()
)