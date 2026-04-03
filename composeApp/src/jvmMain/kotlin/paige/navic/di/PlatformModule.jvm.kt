package paige.navic.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.dsl.module
import paige.navic.data.database.CacheDatabase
import java.io.File

actual val platformModule = module {
	single<CacheDatabase> {
		val dbPath = File(
			System.getProperty("user.home"),
			"cache.db"
		).absolutePath
		Room
			.databaseBuilder<CacheDatabase>(dbPath)
			.setDriver(BundledSQLiteDriver())
			.build()
	}
}
