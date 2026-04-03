package paige.navic.di

import org.koin.dsl.module
import paige.navic.data.repositories.ArtistsRepository
import paige.navic.data.repositories.DbRepository
import paige.navic.data.repositories.GenresRepository
import paige.navic.data.repositories.LyricsRepository
import paige.navic.data.repositories.SearchRepository
import paige.navic.data.repositories.TracksRepository
import paige.navic.domain.repositories.PlaylistRepository

val repositoryModule = module {
	single {
		DbRepository(
			albumDao = get(),
			playlistDao = get(),
			songDao = get(),
			genreDao = get(),
			artistDao = get(),
			lyricDao = get()
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

	single {
		ArtistsRepository(
			artistDao = get(),
			syncManager = get()
		)
	}

	single {
		GenresRepository(
			genreDao = get(),
			albumDao = get()
		)
	}

	single {
		LyricsRepository(
			lyricDao = get(),
			client = get(),
			settings = get()
		)
	}

	single {
		SearchRepository(
			albumDao = get(),
			songDao = get()
		)
	}

	single {
		PlaylistRepository(
			playlistDao = get()
		)
	}

}
