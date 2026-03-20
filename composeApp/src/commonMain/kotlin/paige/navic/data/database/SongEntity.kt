package paige.navic.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant
import dev.zt64.subsonic.api.model.Song as ApiSong

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
	val title: String,
	val artist: String,
	@ColumnInfo(name = "artist_id")
	val artistId: String,
	val album: String?,
	@ColumnInfo(name = "album_id")
	val albumId: String?,
	@ColumnInfo(name = "cover_art_id")
	val coverArtId: String?,
	val duration: Duration,
	@ColumnInfo(name = "track_number")
	val trackNumber: Int?,
	@ColumnInfo(name = "disc_number")
	val discNumber: Int?,
	val year: Int?,
	val genre: String?,
	@ColumnInfo(name = "bit_rate")
	val bitRate: Int,
	@ColumnInfo(name = "mime_type")
	val mimeType: String,
	val suffix: String,
	val path: String?,
	@ColumnInfo(name = "starred_at")
	val starredAt: Instant?,
	@ColumnInfo(name = "date_cached")
	val dateCached: Long
)

fun ApiSong.toEntity(playlistId: String = "__library__"): SongEntity {
	return SongEntity(
		id = "${this.id}_$playlistId",
		navidromeId = this.id,
		playlistId = playlistId,
		title = this.title,
		artist = this.artistName,
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
		dateCached = Clock.System.now().toEpochMilliseconds()
	)
}