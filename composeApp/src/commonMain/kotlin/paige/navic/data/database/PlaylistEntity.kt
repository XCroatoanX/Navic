package paige.navic.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import paige.navic.data.models.TrackCollectionUiModel
import dev.zt64.subsonic.api.model.Playlist as ApiPlaylist
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

@Entity(tableName = "navidrome_playlists")
data class PlaylistEntity(
	@PrimaryKey val id: String,
	val name: String,
	val comment: String?,
	val owner: String?,
	@ColumnInfo(name = "cover_art_id")
	val coverArtId: String?,
	@ColumnInfo(name = "song_count")
	val songCount: Int,
	val duration: Duration,
	val public: Boolean,
	@ColumnInfo(name = "read_only")
	val readOnly: Boolean,
	@ColumnInfo(name = "created_at")
	val createdAt: Instant?,
	@ColumnInfo(name = "modified_at")
	val modifiedAt: Instant?,
	@ColumnInfo(name = "valid_until")
	val validUntil: Instant?,
	@ColumnInfo(name = "allowed_users")
	val allowedUsers: String,
	val dateCached: Long = Clock.System.now().toEpochMilliseconds()
)

fun ApiPlaylist.toEntity(): PlaylistEntity {
	return PlaylistEntity(
		id = this.id,
		name = this.name,
		comment = this.comment,
		owner = this.owner,
		coverArtId = this.coverArtId,
		songCount = this.songCount,
		duration = this.duration,
		public = this.public ?: false,
		readOnly = this.readOnly ?: false,
		createdAt = this.createdAt,
		modifiedAt = this.modifiedAt,
		validUntil = this.validUntil,
		allowedUsers = this.allowedUsers.joinToString(",")
	)
}

fun PlaylistEntity.toUiModel(songs: List<SongEntity>) = TrackCollectionUiModel(
	id = id,
	name = name,
	coverArtId = coverArtId,
	duration = duration,
	songCount = songCount,
	isAlbum = false,
	songs = songs
)