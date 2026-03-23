package paige.navic.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import paige.navic.data.database.entities.AlbumEntity

data class AlbumWithSongs(
    @Embedded val album: AlbumEntity,
    @Relation(
		parentColumn = "albumId",
		entityColumn = "belongsToAlbumId"
	)
	val songs: List<SongWithExtras>
)