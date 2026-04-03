package paige.navic.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import paige.navic.data.database.CacheDatabase
import paige.navic.shared.DATASTORE_FILE_NAME
import paige.navic.shared.DataStorePlayerStorage
import paige.navic.shared.DataStoreSingleton
import paige.navic.shared.JvmMediaPlayerViewModel
import paige.navic.shared.MediaPlayerViewModel
import paige.navic.shared.PlayerStateStorage
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

	single<PlayerStateStorage> {
		val producePath = {
			val home = System.getProperty("user.home")
			val os = System.getProperty("os.name").lowercase()
			val directory = when {
				os.contains("linux") -> {
					val xdgConfig = System.getenv("XDG_CONFIG_HOME")
					if (!xdgConfig.isNullOrBlank()) {
						File(xdgConfig, "navic")
					} else {
						File(home, ".config/navic")
					}
				}
				os.contains("mac") -> File(home, "Library/Application Support/Navic")
				os.contains("win") -> File(System.getenv("AppData"), "Navic")
				else -> File(home, ".navic")
			}
			if (!directory.exists()) directory.mkdirs()
			File(directory, DATASTORE_FILE_NAME).absolutePath
		}
		DataStorePlayerStorage(DataStoreSingleton.getInstance(producePath))
	}

	viewModel<MediaPlayerViewModel> {
		JvmMediaPlayerViewModel(
			storage = get(),
			tracksRepository = get()
		)
	}
}
