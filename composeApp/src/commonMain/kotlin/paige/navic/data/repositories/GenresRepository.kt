package paige.navic.data.repositories

import dev.zt64.subsonic.api.model.AlbumListType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import paige.navic.data.database.dao.AlbumDao
import paige.navic.data.database.dao.GenreDao
import paige.navic.data.database.mappers.toEntity
import paige.navic.data.database.relations.GenreWithAlbums
import paige.navic.data.session.SessionManager
import kotlin.random.Random

open class GenresRepository(
	private val genreDao: GenreDao,
	private val albumDao: AlbumDao
) {

	fun getGenresWithAlbumsFlow(): Flow<List<GenreWithAlbums>> {
		return genreDao.getGenresWithAlbumsFlow()
	}

	suspend fun syncGenres() = coroutineScope {
		val apiGenres = SessionManager.api.getGenres()

		val fetchedData = apiGenres.map { genre ->
			async {
				val albums = SessionManager.api.getAlbums(
					type = AlbumListType.ByGenre(genre.name),
					size = 5
				).shuffled(Random(genre.name.hashCode()))
				genre to albums
			}
		}.awaitAll()

		val genreEntities = fetchedData.map { (apiGenre, _) -> apiGenre.toEntity() }
		val albumEntities = fetchedData.flatMap { (apiGenre, apiAlbums) ->
			apiAlbums.map { it.toEntity().copy(genre = apiGenre.name) }
		}

		genreDao.insertGenres(genreEntities)
		albumDao.insertAlbums(albumEntities)
	}
}