package paige.navic.data.repositories

import dev.zt64.subsonic.api.model.AlbumInfo
import kotlinx.coroutines.flow.first
import paige.navic.data.database.dao.DatabaseDao
import paige.navic.data.database.DbContainer
import paige.navic.data.database.entities.SongEntity
import paige.navic.data.models.TrackCollectionUiModel
import paige.navic.data.session.SessionManager
import kotlin.time.Clock

class TracksRepository(
	private val dao: DatabaseDao = DbContainer.dao
) {
	suspend fun fetchWithAllTracks(collection: TrackCollectionUiModel): TrackCollectionUiModel? {
		return if (collection.isAlbum) {
				val album = dao.getAlbumById(collection.id) ?: return null
				val songs = dao.getSongsFlowByAlbum(collection.id).first()

				TrackCollectionUiModel(
					id = album.id,
					name = album.name,
					coverArtId = album.coverArtId,
					duration = album.duration,
					songCount = album.songCount,
					isAlbum = true,
					songs = songs
				)
			} else {
				val playlist = dao.getPlaylistById(collection.id) ?: return null
				val songs = dao.getSongsByPlaylist(collection.id).first()

				TrackCollectionUiModel(
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
		return dao.isSongStarred(trackId)
	}

	suspend fun starTrack(track: SongEntity) {
		SessionManager.api.star(track.navidromeId)
		dao.insertSong(track.copy(starredAt = Clock.System.now()))
	}

	suspend fun unstarTrack(track: SongEntity) {
		SessionManager.api.unstar(track.navidromeId)
		dao.insertSong(track.copy(starredAt = null))
	}
}