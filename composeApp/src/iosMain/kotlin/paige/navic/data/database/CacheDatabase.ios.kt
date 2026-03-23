package paige.navic.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual fun getDatabaseBuilder(): RoomDatabase.Builder<CacheDatabase> {
	val dbFilePath = documentDirectory() + "/navic.db"
	return Room.databaseBuilder<CacheDatabase>(
		name = dbFilePath,
		factory = { CacheDatabaseConstructor.initialize() }
	)
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
	val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
		directory = NSDocumentDirectory,
		inDomain = NSUserDomainMask,
		appropriateForURL = null,
		create = false,
		error = null,
	)
	return requireNotNull(documentDirectory?.path)
}