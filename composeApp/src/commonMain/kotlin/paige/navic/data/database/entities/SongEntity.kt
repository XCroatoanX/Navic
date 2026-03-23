package paige.navic.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import paige.navic.domain.models.DomainContributor
import paige.navic.domain.models.DomainReplayGain
import kotlin.time.Duration
import kotlin.time.Instant
import dev.zt64.subsonic.api.model.Song as ApiSong

@Serializable
@Entity(
	tableName = "navidrome_songs",
	indices = [
		Index(value = ["navidrome_id"]),
		Index(value = ["album_id"]),
		Index(value = ["artist_id"]),
		Index(value = ["playlist_id"])
	]
)
data class SongEntity(
	@PrimaryKey
	val id: String,
	@ColumnInfo(name = "navidrome_id")
	val navidromeId: String,
	@ColumnInfo(name = "playlist_id")
	val playlistId: String,
	@ColumnInfo(name = "date_cached")
	val dateCached: Long,
	val title: String,
	val artistName: String,
	@ColumnInfo(name = "artist_id")
	val artistId: String,
	val album: String?,
	@ColumnInfo(name = "album_id")
	val albumId: String?,
	@ColumnInfo(name = "parent_id")
	val parentId: String?,
	@ColumnInfo(name = "cover_art_id")
	val coverArtId: String?,
	val duration: Duration,
	@ColumnInfo(name = "track_number")
	val trackNumber: Int?,
	@ColumnInfo(name = "disc_number")
	val discNumber: Int?,
	val year: Int?,
	val genre: String?,
	val genres: List<String>,
	val moods: List<String>,
	val isrc: List<String>,
	val bpm: Int?,
	val comment: String?,
	val contributors: List<DomainContributor>,
	@ColumnInfo(name = "play_count")
	val playCount: Int = 0,
	@ColumnInfo(name = "user_rating")
	val userRating: Int?,
	@ColumnInfo(name = "average_rating")
	val averageRating: Float?,
	@ColumnInfo(name = "starred_at")
	val starredAt: Instant?,
	@ColumnInfo(name = "bit_rate")
	val bitRate: Int?,
	@ColumnInfo(name = "bit_depth")
	val bitDepth: Int?,
	@ColumnInfo(name = "sample_rate")
	val sampleRate: Int?,
	@ColumnInfo(name = "audio_channel_count")
	val audioChannelCount: Int?,
	@ColumnInfo(name = "file_size")
	val fileSize: Long = 0L,
	@ColumnInfo(name = "mime_type")
	val mimeType: String,
	val suffix: String,
	val path: String?,
	@ColumnInfo(name = "replay_gain")
	val replayGain: DomainReplayGain?,
	@ColumnInfo(name = "explicit_status")
	val explicitStatus: ApiSong.ExplicitStatus?
)
