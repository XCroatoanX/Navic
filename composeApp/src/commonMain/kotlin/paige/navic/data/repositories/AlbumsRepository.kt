package paige.navic.data.repositories

import dev.zt64.subsonic.api.model.AlbumListType
import kotlinx.coroutines.flow.Flow
import paige.navic.data.database.entities.AlbumEntity
import paige.navic.data.database.DbContainer
import paige.navic.data.database.dao.AlbumDao
import paige.navic.data.database.dao.SongDao
import paige.navic.data.database.entities.SongEntity
import paige.navic.data.database.mappers.toEntity
import paige.navic.data.session.SessionManager

open class AlbumsRepository(
	private val albumDao: AlbumDao = DbContainer.albumDao,
	private val songDao: SongDao = DbContainer.songDao,
) {
	fun getAlbumsFlow(
		offset: Int,
		listType: AlbumListType
	): Flow<List<AlbumEntity>> {
		val totalToLoad = 30 + offset
		return when (listType) {
			is AlbumListType.AlphabeticalByArtist -> albumDao.getAlbumsAlphabeticalByArtist(totalToLoad)
			is AlbumListType.Newest -> albumDao.getAlbumsNewest(totalToLoad)
			is AlbumListType.Random -> albumDao.getAlbumsRandom(totalToLoad)
			is AlbumListType.Starred -> albumDao.getAlbumsStarred(totalToLoad)
			is AlbumListType.Frequent -> albumDao.getAlbumsFrequent(totalToLoad)
			is AlbumListType.Recent -> albumDao.getAlbumsRecent(totalToLoad)
			else -> albumDao.getAlbumsAlphabeticalByName(totalToLoad)
		}
	}

	suspend fun getSongsByAlbumId(albumId: String): List<SongEntity> {
		val localSongs = songDao.getSongListByAlbumId(albumId)

		return localSongs.ifEmpty {
			val remoteSongs = SessionManager.api.getAlbum(albumId).songs
			val entities = remoteSongs.map { it.toEntity() }
			songDao.insertSongs(entities)
			entities
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

	suspend fun isAlbumStarred(album: AlbumEntity): Boolean {
		return albumDao.isAlbumStarred(album.id)
	}
	suspend fun starAlbum(album: AlbumEntity) {
		SessionManager.api.star(album.id)
//		albumDao.insertAlbum(album.copy(starred_at = Clock.System.now()))
	}

	suspend fun unstarAlbum(album: AlbumEntity) {
		SessionManager.api.unstar(album.id)
//		albumDao.insertAlbum(album.copy(starred_at = null))
	}
}