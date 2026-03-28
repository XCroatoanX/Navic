package paige.navic.data.database

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

private lateinit var applicationContext: Context

fun initAndroidContext(context: Context) {
	applicationContext = context.applicationContext
}

actual fun provideCacheDatabase(): CacheDatabase {
	//applicationContext.deleteDatabase("cache.db")???
	val dbPath = applicationContext.getDatabasePath("cache.db").absolutePath

	return Room
		.databaseBuilder<CacheDatabase>(applicationContext, dbPath)
		.setDriver(BundledSQLiteDriver())
		.build()
}
