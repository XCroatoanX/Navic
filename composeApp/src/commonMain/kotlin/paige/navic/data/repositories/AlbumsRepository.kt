package paige.navic.data.repositories

import dev.zt64.subsonic.api.model.AlbumListType
import kotlinx.coroutines.flow.Flow
import paige.navic.data.database.DbContainer
import paige.navic.data.database.dao.AlbumDao
import paige.navic.data.database.mappers.toEntity
import paige.navic.data.database.relations.AlbumWithSongs
import paige.navic.data.session.SessionManager
import paige.navic.domain.models.DomainAlbum
import kotlin.time.Clock

open class AlbumsRepository(
	private val albumDao: AlbumDao = DbContainer.albumDao,
) {
	fun getAlbumsFlow(
		offset: Int,
		listType: AlbumListType
	): Flow<List<AlbumWithSongs>> {
		val totalToLoad = 30 + offset
		return when (listType) {
			is AlbumListType.AlphabeticalByArtist -> albumDao.getAlbumsAlphabeticalByArtist(totalToLoad)
			is AlbumListType.Newest -> albumDao.getAlbumsNewest(totalToLoad)
			is AlbumListType.Random -> albumDao.getAlbumsRandom(totalToLoad)
			is AlbumListType.Starred -> albumDao.getAlbumsStarred(totalToLoad)
			is AlbumListType.Frequent -> albumDao.getAlbumsFrequent(totalToLoad)
			is AlbumListType.Recent -> albumDao.getAlbumsRecent(totalToLoad)
			is AlbumListType.ByGenre -> albumDao.getAlbumsByGenre(listType.genre)
			else -> albumDao.getAlbumsAlphabeticalByName(totalToLoad)
		}
	}

	suspend fun syncAlbums(listType: AlbumListType, offset: Int) {
		val remote = SessionManager.api.getAlbums(
			type = listType,
			size = 30,
			offset = offset
		)
		albumDao.insertAlbums(remote.map { it.toEntity() })
	}

	suspend fun isAlbumStarred(album: DomainAlbum): Boolean {
		return albumDao.isAlbumStarred(album.id)
	}
	suspend fun starAlbum(album: DomainAlbum) {
		val starredEntity = album.toEntity().copy(
			starredAt = Clock.System.now(),
			isPendingSync = true
		)
		albumDao.insertAlbum(starredEntity)

		try {
			SessionManager.api.star(album.id)
			albumDao.insertAlbum(starredEntity.copy(isPendingSync = false))
		} catch (e: Exception) {
			//scheduleSyncJob(album.id, isStarring = true) TODO implement
		}
	}

	suspend fun unstarAlbum(album: DomainAlbum) {
		val unstarredEntity = album.toEntity().copy(
			starredAt = null,
			isPendingSync = true
		)
		albumDao.insertAlbum(unstarredEntity)

		try {
			SessionManager.api.unstar(album.id)
			albumDao.insertAlbum(unstarredEntity.copy(isPendingSync = false))
		} catch (e: Exception) {
			//scheduleSyncJob(album.id, isStarring = false)
		}
	}
}