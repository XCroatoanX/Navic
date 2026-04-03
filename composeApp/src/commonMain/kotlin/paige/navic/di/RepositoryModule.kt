package paige.navic.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import paige.navic.data.repositories.AlbumsRepository
import paige.navic.data.repositories.ArtistsRepository
import paige.navic.data.repositories.DbRepository
import paige.navic.data.repositories.GenresRepository
import paige.navic.data.repositories.LyricsRepository
import paige.navic.data.repositories.SearchRepository
import paige.navic.data.repositories.SharesRepository
import paige.navic.data.repositories.TracksRepository
import paige.navic.domain.repositories.PlaylistRepository

val repositoryModule = module {
	singleOf(::AlbumsRepository)
	singleOf(::ArtistsRepository)
	singleOf(::DbRepository)
	singleOf(::GenresRepository)
	singleOf(::LyricsRepository)
	singleOf(::SearchRepository)
	singleOf(::SharesRepository)
	singleOf(::TracksRepository)
	singleOf(::PlaylistRepository)
}
