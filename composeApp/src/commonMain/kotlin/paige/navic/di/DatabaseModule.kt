package paige.navic.di

import org.koin.dsl.module
import paige.navic.data.database.CacheDatabase
import paige.navic.data.database.provideCacheDatabase

val databaseModule = module {
	single<CacheDatabase> { provideCacheDatabase() }

	single { get<CacheDatabase>().albumDao() }
	single { get<CacheDatabase>().genreDao() }
	single { get<CacheDatabase>().playlistDao() }
	single { get<CacheDatabase>().songDao() }
	single { get<CacheDatabase>().artistDao() }
	single { get<CacheDatabase>().lyricDao() }
	single { get<CacheDatabase>().syncActionDao() }
}