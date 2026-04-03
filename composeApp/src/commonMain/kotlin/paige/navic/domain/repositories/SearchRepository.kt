package paige.navic.domain.repositories

import paige.navic.data.database.dao.AlbumDao
import paige.navic.data.database.dao.SongDao
import paige.navic.data.database.mappers.toDomainModel
import paige.navic.data.session.SessionManager

class SearchRepository(
	private val albumDao: AlbumDao,
	private val songDao: SongDao
) {
	suspend fun search(query: String): List<Any> {
		val data = SessionManager.api.searchID3(query)
		return listOf(
			data.albums.mapNotNull { albumDao.getAlbumById(it.id)?.toDomainModel() },
			data.artists,
			data.songs.mapNotNull { songDao.getSongById(it.id)?.toDomainModel() }
		).flatten()
	}
}