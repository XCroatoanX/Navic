package paige.navic.di

import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import paige.navic.shared.AndroidMediaPlayerViewModel
import paige.navic.shared.DataStorePlayerStorage
import paige.navic.shared.DataStoreSingleton
import paige.navic.shared.PlayerStateStorage

val androidModule = module {

	single<PlayerStateStorage> {
		val context = androidApplication()
		val producePath = {
			context.filesDir.resolve("player_state.json").absolutePath
		}
		DataStorePlayerStorage(DataStoreSingleton.getInstance(producePath))
	}

	viewModel {
		AndroidMediaPlayerViewModel(
			application = androidApplication(),
			storage = get(),
			tracksRepository = get(),
			albumDao = get()
		)
	}
}