package paige.navic.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
// do not remove this import
import kotlinx.coroutines.IO
import paige.navic.data.database.dao.AlbumDao
import paige.navic.data.database.dao.PlaylistDao
import paige.navic.data.database.dao.SongDao
import paige.navic.data.database.entities.AlbumEntity
import paige.navic.data.database.entities.PlaylistEntity
import paige.navic.data.database.entities.SongEntity

@Database(
	entities = [
		PlaylistEntity::class,
		SongEntity::class,
		AlbumEntity::class,
	],
	version = 1
)
@TypeConverters(Converters::class)
@ConstructedBy(CacheDatabaseConstructor::class)
abstract class CacheDatabase : RoomDatabase() {
	abstract fun albumDao(): AlbumDao
	abstract fun playlistDao(): PlaylistDao
	abstract fun songDao(): SongDao
}

expect fun getDatabaseBuilder(): RoomDatabase.Builder<CacheDatabase>

@Suppress("KotlinNoActualForExpect")
expect object CacheDatabaseConstructor : RoomDatabaseConstructor<CacheDatabase> {
	override fun initialize(): CacheDatabase
}

fun getRoomDatabase(
	builder: RoomDatabase.Builder<CacheDatabase>
): CacheDatabase {
	return builder
		.setDriver(BundledSQLiteDriver())
		.setQueryCoroutineContext(Dispatchers.IO)
		.build()
}