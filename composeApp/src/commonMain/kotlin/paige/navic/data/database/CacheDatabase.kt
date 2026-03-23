package paige.navic.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import paige.navic.data.database.dao.AlbumDao
import paige.navic.data.database.dao.PlaylistDao
import paige.navic.data.database.dao.SongDao
import paige.navic.data.database.entities.AlbumEntity
import paige.navic.data.database.entities.PlaylistEntity
import paige.navic.data.database.entities.SongEntity

@Database(
	version = 1,
	entities = [
		AlbumEntity::class,
		PlaylistEntity::class,
		SongEntity::class,
	]
)
@TypeConverters(Converters::class)
abstract class CacheDatabase : RoomDatabase() {
	abstract fun albumDao(): AlbumDao
	abstract fun playlistDao(): PlaylistDao
	abstract fun songDao(): SongDao
}

expect fun provideCacheDatabase(): CacheDatabase
