package paige.navic.di

import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import paige.navic.shared.AndroidMediaPlayerViewModel

actual val platformModule: Module = module {

	includes(androidModule)

	viewModel {
		AndroidMediaPlayerViewModel(
			application = androidApplication(),
			storage = get(),
			tracksRepository = get(),
			albumDao = get()
		)
	}
}