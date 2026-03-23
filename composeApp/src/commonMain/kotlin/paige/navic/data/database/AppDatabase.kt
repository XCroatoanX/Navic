package paige.navic.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import paige.navic.data.database.dao.DatabaseDao
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
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun getDao(): DatabaseDao
}

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
	override fun initialize(): AppDatabase
}

fun getRoomDatabase(
	builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
	return builder
		.setDriver(BundledSQLiteDriver())
		.setQueryCoroutineContext(Dispatchers.IO)
		.build()
}