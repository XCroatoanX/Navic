package paige.navic.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock
import dev.zt64.subsonic.api.model.Album as ApiAlbum
import kotlin.time.Duration
import kotlin.time.Instant

@Entity(tableName = "navidrome_albums")
data class AlbumEntity(
	@PrimaryKey
	@ColumnInfo(name = "id")
	val id: String,
	@ColumnInfo(name = "name")
	val name: String,
	@ColumnInfo(name = "artist_id")
	val artistId: String,
	@ColumnInfo(name = "artist")
	val artistName: String,
	@ColumnInfo(name = "cover_art")
	val coverArtId: String?,
	@ColumnInfo(name = "song_count")
	val songCount: Int,
	@ColumnInfo(name = "duration")
	val duration: Duration,
	@ColumnInfo(name = "year")
	val year: Int?,
	@ColumnInfo(name = "genre")
	val genre: String?,
	@ColumnInfo(name = "starred_at")
	val starredAt: Instant?,
	@ColumnInfo(name = "user_rating")
	val userRating: Int?,
	@ColumnInfo(name = "music_brainz_id")
	val musicBrainzId: String?,
	@ColumnInfo(name = "created_at")
	val createdAt: Instant,
	@ColumnInfo(name = "last_played_at")
	val lastPlayedAt: Instant?,
	@ColumnInfo(name = "play_count")
	val playCount: Int = 0,
	@ColumnInfo(name = "date_cached")
	val dateCached: Long = Clock.System.now().toEpochMilliseconds()
)

fun ApiAlbum.toEntity(): AlbumEntity {
	return AlbumEntity(
		id = this.id,
		name = this.name,
		artistId = this.artistId,
		artistName = this.artistName,
		coverArtId = this.coverArtId,
		songCount = this.songCount,
		duration = this.duration,
		year = this.year,
		genre = this.genre,
		starredAt = this.starredAt,
		userRating = this.userRating,
		musicBrainzId = this.musicBrainzId,
		createdAt = this.createdAt,
		lastPlayedAt = this.lastPlayedAt,
		playCount = this.playCount
	)
}
