package paige.navic.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import paige.navic.data.database.CacheDatabase
import paige.navic.shared.DATASTORE_FILE_NAME
import paige.navic.shared.DataStorePlayerStorage
import paige.navic.shared.DataStoreSingleton
import paige.navic.shared.IOSMediaPlayerViewModel
import paige.navic.shared.MediaPlayerViewModel
import paige.navic.shared.PlayerStateStorage
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual val platformModule = module {
	single<CacheDatabase> {
		val dbPath = documentDirectory() + "/cache.db"
		Room
			.databaseBuilder<CacheDatabase>(dbPath)
			.setDriver(BundledSQLiteDriver())
			.build()
	}

	single<PlayerStateStorage> {
		val producePath = {
			@OptIn(ExperimentalForeignApi::class)
			val directory = NSFileManager.defaultManager.URLForDirectory(
				directory = NSDocumentDirectory,
				inDomain = NSUserDomainMask,
				appropriateForURL = null,
				create = true,
				error = null
			)
			directory?.path + "/$DATASTORE_FILE_NAME"
		}
		DataStorePlayerStorage(DataStoreSingleton.getInstance(producePath))
	}

	viewModel<MediaPlayerViewModel> {
		IOSMediaPlayerViewModel(
			storage = get(),
			tracksRepository = get()
		)
	}
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
