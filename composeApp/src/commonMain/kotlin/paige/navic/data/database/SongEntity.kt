package paige.navic.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import paige.navic.data.models.LocalContributor
import paige.navic.data.models.LocalReplayGain
import kotlin.time.Clock
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
	val contributors: List<LocalContributor>,
	@ColumnInfo(name = "play_count")
	val playCount: Int = 0,
	@ColumnInfo(name = "user_rating")
	val userRating: Int?,
	@ColumnInfo(name = "average_rating")
	val averageRating: Int?,
	@ColumnInfo(name = "starred_at")
	val starredAt: Instant?,
	@ColumnInfo(name = "bit_rate")
	val bitRate: Int,
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
	val replayGain: LocalReplayGain?,
	@ColumnInfo(name = "explicit_status")
	val explicitStatus: ApiSong.ExplicitStatus?
)

fun ApiSong.toEntity(playlistId: String = "__library__"): SongEntity {
	return SongEntity(
		id = "${this.id}_$playlistId",
		navidromeId = this.id,
		playlistId = playlistId,
		title = this.title,
		artistName = this.artistName,
		artistId = this.artistId,
		album = this.albumTitle,
		albumId = this.albumId,
		coverArtId = this.coverArtId,
		duration = this.duration,
		trackNumber = this.trackNumber,
		discNumber = this.discNumber,
		year = this.year,
		genre = this.genre,
		bitRate = this.bitRate,
		mimeType = this.mimeType,
		suffix = this.fileExtension,
		path = this.filePath,
		starredAt = this.starredAt,
		dateCached = Clock.System.now().toEpochMilliseconds(),
		parentId = this.parentId,
		genres = this.genres,
		moods = this.moods,
		isrc = this.isrc,
		bpm = this.bpm,
		comment = this.comment,
		contributors = this.contributors.map {
			LocalContributor(
				role = it.role,
				subRole = it.subRole,
				artistId = it.artist.id,
				artistName = it.artist.name
			)
		},
		playCount = this.playCount,
		userRating = this.userRating,
		averageRating = this.averageRating,
		bitDepth = this.bitDepth,
		sampleRate = this.sampleRate,
		audioChannelCount = this.audioChannelCount,
		fileSize = this.fileSize,
		replayGain = this.replayGain?.let {
			LocalReplayGain(
				albumGain = it.albumGain,
				albumPeak = it.albumPeak,
				trackGain = it.trackGain,
				trackPeak = it.trackPeak,
				baseGain = it.baseGain,
				fallbackGain = it.fallbackGain
			)
		},
		explicitStatus = this.explicitStatus
	)
}