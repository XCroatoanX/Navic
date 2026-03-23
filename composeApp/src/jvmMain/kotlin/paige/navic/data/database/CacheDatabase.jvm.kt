package paige.navic.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder(): RoomDatabase.Builder<CacheDatabase> {
	val dbFile = File(System.getProperty("user.home"), "navic.db")
	return Room.databaseBuilder<CacheDatabase>(
		name = dbFile.absolutePath,
		factory = { CacheDatabaseConstructor.initialize() }
	)
}