package paige.navic.data.repositories

import dev.zt64.subsonic.api.model.AlbumListType
import kotlinx.coroutines.flow.Flow
import paige.navic.data.database.AlbumEntity
import paige.navic.data.database.DatabaseDao
import paige.navic.data.database.DbContainer
import paige.navic.data.database.toEntity
import paige.navic.data.session.SessionManager
import kotlin.time.Clock

open class AlbumsRepository(
	private val dao: DatabaseDao = DbContainer.dao
) {
	fun getAlbumsFlow(
		offset: Int,
		listType: AlbumListType
	): Flow<List<AlbumEntity>> {
		val totalToLoad = 30 + offset
		return when (listType) {
			is AlbumListType.AlphabeticalByArtist -> dao.getAlbumsAlphabeticalByArtist(totalToLoad)
			is AlbumListType.Newest -> dao.getAlbumsNewest(totalToLoad)
			is AlbumListType.Random -> dao.getAlbumsRandom(totalToLoad)
			is AlbumListType.Starred -> dao.getAlbumsStarred(totalToLoad)
			is AlbumListType.Frequent -> dao.getAlbumsFrequent(totalToLoad)
			is AlbumListType.Recent -> dao.getAlbumsRecent(totalToLoad)
			else -> dao.getAlbumsAlphabeticalByName(totalToLoad)
		}
	}

	suspend fun syncAlbums(listType: AlbumListType, offset: Int) {
		val remote = SessionManager.api.getAlbums(
			type = listType,
			size = 30,
			offset = offset
		)
		dao.insertAlbums(remote.map { it.toEntity() })
	}

	suspend fun isAlbumStarred(album: AlbumEntity): Boolean {
		return dao.isAlbumStarred(album.id)
	}
	suspend fun starAlbum(album: AlbumEntity) {
		SessionManager.api.star(album.id)
		dao.insertAlbum(album.copy(starred_at = Clock.System.now()))
	}

	suspend fun unstarAlbum(album: AlbumEntity) {
		SessionManager.api.unstar(album.id)
		dao.insertAlbum(album.copy(starred_at = null))
	}
}