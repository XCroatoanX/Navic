package paige.navic.data.repositories

import dev.zt64.subsonic.api.model.Album
import dev.zt64.subsonic.api.model.AlbumListType
import kotlinx.coroutines.flow.Flow
import paige.navic.data.database.AlbumEntity
import paige.navic.data.database.DatabaseDao
import paige.navic.data.database.DbContainer
import paige.navic.data.database.PlaylistEntity
import paige.navic.data.database.toEntity
import paige.navic.data.session.SessionManager

open class AlbumsRepository(
	private val dao: DatabaseDao = DbContainer.dao
) {
	open suspend fun getAlbums(
		offset: Int = 0,
		listType: AlbumListType = AlbumListType.AlphabeticalByArtist
	): List<Album> {
		return SessionManager.api
			.getAlbums(type = listType, size = 30, offset = offset)
	}

	fun getAlbumsFlow(): Flow<List<AlbumEntity>> = dao.getAllAlbums()

	suspend fun syncAlbums(listType: AlbumListType, offset: Int) {
		val remote = SessionManager.api.getAlbums(
			type = listType,
			size = 30,
			offset = offset
		)
		dao.insertAlbums(remote.map { it.toEntity() })
	}

	suspend fun isAlbumStarred(album: Album): Boolean {
		return album in SessionManager.api.getStarred().albums
	}
	suspend fun starAlbum(album: Album) {
		SessionManager.api.star(album.id)
	}
	suspend fun unstarAlbum(album: Album) {
		SessionManager.api.unstar(album.id)
	}
}