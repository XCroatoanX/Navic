package paige.navic.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.zt64.subsonic.api.model.Playlist
import kotlin.time.Clock
import kotlin.time.Duration

@Entity(tableName = "navidrome_playlists")
data class PlaylistEntity(
	@PrimaryKey val id: String,
	val name: String,
	val comment: String?,
	val owner: String?,
	@ColumnInfo(name = "cover_art_id") val coverArtId: String?,
	@ColumnInfo(name = "song_count") val songCount: Int,
	val duration: Duration,
	val dateCached: Long = Clock.System.now().toEpochMilliseconds()
)

fun Playlist.toEntity(): PlaylistEntity {
	return PlaylistEntity(
		id = this.id,
		name = this.name,
		comment = this.comment,
		owner = this.owner,
		coverArtId = this.coverArtId,
		songCount = this.songCount,
		duration = this.duration
	)
}