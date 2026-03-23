package paige.navic.data.repositories

import dev.zt64.subsonic.api.model.AlbumInfo
import kotlinx.coroutines.flow.first
import paige.navic.data.database.DbContainer
import paige.navic.data.database.dao.AlbumDao
import paige.navic.data.database.dao.PlaylistDao
import paige.navic.data.database.dao.SongDao
import paige.navic.data.database.entities.SongEntity
import paige.navic.domain.models.DomainSongCollection
import paige.navic.data.session.SessionManager
import kotlin.time.Clock

class TracksRepository(
	private val albumDao: AlbumDao = DbContainer.albumDao,
	private val playlistDao: PlaylistDao = DbContainer.playlistDao,
	private val songDao: SongDao = DbContainer.songDao,
) {
	suspend fun fetchWithAllTracks(collection: DomainSongCollection): DomainSongCollection? {
		return if (collection.isAlbum) {
				val album = albumDao.getAlbumById(collection.id) ?: return null
				val songs = songDao.getSongsFlowByAlbum(collection.id).first()

				DomainSongCollection(
					id = album.id,
					name = album.name,
					coverArtId = album.coverArtId,
					duration = album.duration,
					songCount = album.songCount,
					isAlbum = true,
					songs = songs
				)
			} else {
				val playlist = playlistDao.getPlaylistById(collection.id) ?: return null
				val songs = songDao.getSongsByPlaylist(collection.id).first()

				DomainSongCollection(
					id = playlist.id,
					name = playlist.name,
					coverArtId = playlist.coverArtId,
					duration = playlist.duration,
					songCount = playlist.songCount,
					isAlbum = false,
					songs = songs
				)
			}
		}

	suspend fun getAlbumInfo(albumId: String): AlbumInfo {
		return SessionManager.api.getAlbumInfo(albumId)
	}

	suspend fun isTrackStarred(trackId: String): Boolean {
		return songDao.isSongStarred(trackId)
	}

	suspend fun starTrack(track: SongEntity) {
		SessionManager.api.star(track.navidromeId)
		songDao.insertSong(track.copy(starredAt = Clock.System.now()))
	}

	suspend fun unstarTrack(track: SongEntity) {
		SessionManager.api.unstar(track.navidromeId)
		songDao.insertSong(track.copy(starredAt = null))
	}
}