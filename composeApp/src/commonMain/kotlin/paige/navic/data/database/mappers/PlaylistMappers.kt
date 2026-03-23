package paige.navic.data.database.mappers

import dev.zt64.subsonic.api.model.Playlist as ApiPlaylist
import paige.navic.data.database.entities.PlaylistEntity
import paige.navic.data.database.entities.SongEntity
import paige.navic.data.models.TrackCollectionUiModel

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

fun PlaylistEntity.toDomainModel(songs: List<SongEntity>) = TrackCollectionUiModel(
	id = id,
	name = name,
	coverArtId = coverArtId,
	duration = duration,
	songCount = songCount,
	isAlbum = false,
	songs = songs
)
