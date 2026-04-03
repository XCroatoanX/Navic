package paige.navic.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import paige.navic.data.database.CacheDatabase
import paige.navic.shared.AndroidMediaPlayerViewModel
import paige.navic.shared.DATASTORE_FILE_NAME
import paige.navic.shared.DataStorePlayerStorage
import paige.navic.shared.DataStoreSingleton
import paige.navic.shared.MediaPlayerViewModel
import paige.navic.shared.PlayerStateStorage

actual val platformModule = module {
	single<CacheDatabase> {
		val dbPath = androidApplication()
			.getDatabasePath("cache.db")
			.absolutePath
		Room
			.databaseBuilder<CacheDatabase>(get(), dbPath)
			.setDriver(BundledSQLiteDriver())
			.build()
	}

	single<PlayerStateStorage> {
		val context = androidApplication()
		val producePath = {
			context.filesDir.resolve(DATASTORE_FILE_NAME).absolutePath
		}
		DataStorePlayerStorage(DataStoreSingleton.getInstance(producePath))
	}

	viewModel<MediaPlayerViewModel> {
		AndroidMediaPlayerViewModel(
			application = androidApplication(),
			storage = get(),
			tracksRepository = get(),
			albumDao = get()
		)
	}
}