package paige.navic.data.database.mappers

import paige.navic.data.database.entities.AlbumEntity
import paige.navic.data.database.entities.SongEntity
import paige.navic.data.models.TrackCollectionUiModel
import dev.zt64.subsonic.api.model.Album as ApiAlbum

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

fun AlbumEntity.toDomainModel(songs: List<SongEntity>) = TrackCollectionUiModel(
	id = id,
	name = name,
	coverArtId = coverArtId,
	duration = duration,
	songCount = songCount,
	isAlbum = true,
	songs = songs
)
