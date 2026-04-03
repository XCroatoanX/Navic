package paige.navic.di

import org.koin.dsl.module
import paige.navic.data.repositories.DbRepository
import paige.navic.data.repositories.TracksRepository

val repositoryModule = module {
	single {
		DbRepository(
			albumDao = get(),
			playlistDao = get(),
			songDao = get()
		)
	}

	single {
		TracksRepository(
			albumDao = get(),
			playlistDao = get(),
			songDao = get(),
			syncManager = get()
		)
	}
}
