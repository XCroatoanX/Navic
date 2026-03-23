package paige.navic.data.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

actual fun provideCacheDatabase(): CacheDatabase {
	val dbPath = File(System.getProperty("user.home"), "cache.db").absolutePath

	return Room
		.databaseBuilder<CacheDatabase>(dbPath)
		.setDriver(BundledSQLiteDriver())
		.build()
}
