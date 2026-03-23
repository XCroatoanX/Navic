package paige.navic.data.repositories

import dev.zt64.subsonic.api.model.AlbumInfo
import paige.navic.data.database.DbContainer
import paige.navic.data.database.dao.AlbumDao
import paige.navic.data.database.dao.PlaylistDao
import paige.navic.data.database.dao.SongDao
import paige.navic.data.database.mappers.toDomainModel
import paige.navic.data.database.mappers.toEntity
import paige.navic.domain.models.DomainSongCollection
import paige.navic.data.session.SessionManager
import paige.navic.domain.models.DomainAlbum
import paige.navic.domain.models.DomainPlaylist
import paige.navic.domain.models.DomainSong
import kotlin.time.Clock

class TracksRepository(
	private val albumDao: AlbumDao = DbContainer.albumDao,
	private val playlistDao: PlaylistDao = DbContainer.playlistDao,
	private val songDao: SongDao = DbContainer.songDao,
) {
	suspend fun fetchWithAllTracks(collection: DomainSongCollection): DomainSongCollection {
		if (collection.songs.isNotEmpty()) {
			return collection
		} else {
			try {
				println("collection ${collection.name} does not have songs, refreshing")
				return when (collection) {
					is DomainAlbum -> {
						val album = SessionManager.api.getAlbum(collection.id)
						songDao.insertSongs(album.songs.map { it.toEntity() })
						albumDao.getAlbumById(album.id)!!.toDomainModel()
					}

					is DomainPlaylist -> {
						val playlist = SessionManager.api.getPlaylist(collection.id)
						songDao.insertSongs(playlist.songs.map { it.toEntity() })
						playlistDao.getPlaylistById(playlist.id)!!.toDomainModel()
					}
				}
			} catch (e: Exception) {
				e.printStackTrace()
				println("failed to get collection with songs, returning original one from db")
				return collection
			}
		}
	}

	suspend fun getAlbumInfo(albumId: String): AlbumInfo {
		return SessionManager.api.getAlbumInfo(albumId)
	}

	suspend fun isTrackStarred(trackId: String): Boolean {
		return songDao.isSongStarred(trackId)
	}

	suspend fun starTrack(track: DomainSong) {
		SessionManager.api.star(track.id)
		songDao.insertSong(track.toEntity().copy(starredAt = Clock.System.now()))
	}

	suspend fun unstarTrack(track: DomainSong) {
		SessionManager.api.unstar(track.id)
		songDao.insertSong(track.toEntity().copy(starredAt = null))
	}
}