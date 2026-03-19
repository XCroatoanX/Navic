package paige.navic.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Navidrome/Subsonic cached data.
 */
@Dao
interface DatabaseDao {

	// SONGS

	@Query("SELECT * FROM navidrome_songs ORDER BY date_cached DESC")
	fun getAllNavidromeSongs(): Flow<List<SongEntity>>

	@Query("SELECT * FROM navidrome_songs WHERE playlist_id = :playlistId ORDER BY track_number ASC")
	fun getSongsByPlaylist(playlistId: String): Flow<List<SongEntity>>

	@Query("SELECT * FROM navidrome_songs WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%'")
	fun searchSongs(query: String): Flow<List<SongEntity>>

	@Query("SELECT * FROM navidrome_songs WHERE id IN (:ids)")
	fun getSongsByIds(ids: List<String>): Flow<List<SongEntity>>

	@Query("SELECT * FROM navidrome_songs WHERE navidrome_id = :navidromeId LIMIT 1")
	suspend fun getSongByNavidromeId(navidromeId: String): SongEntity?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertSongs(songs: List<SongEntity>)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertSong(song: SongEntity)

	@Query("DELETE FROM navidrome_songs WHERE id = :songId")
	suspend fun deleteSong(songId: String)

	@Query("DELETE FROM navidrome_songs WHERE playlist_id = :playlistId")
	suspend fun deleteSongsByPlaylist(playlistId: String)

	@Query("SELECT * FROM navidrome_songs WHERE album_id = :albumId ORDER BY disc_number ASC, track_number ASC")
	fun getSongsByAlbum(albumId: String): Flow<List<SongEntity>>

	// PLAYLISTS

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertPlaylist(playlist: PlaylistEntity)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertPlaylists(playlists: List<PlaylistEntity>)

	@Query("SELECT * FROM navidrome_playlists ORDER BY name ASC")
	fun getAllPlaylists(): Flow<List<PlaylistEntity>>

	@Query("SELECT * FROM navidrome_playlists")
	suspend fun getAllPlaylistsList(): List<PlaylistEntity>

	@Query("SELECT * FROM navidrome_playlists WHERE id = :playlistId LIMIT 1")
	suspend fun getPlaylistById(playlistId: String): PlaylistEntity?

	@Query("DELETE FROM navidrome_playlists WHERE id = :playlistId")
	suspend fun deletePlaylist(playlistId: String)

	@Query("SELECT COUNT(*) FROM navidrome_playlists")
	suspend fun getPlaylistCount(): Int

	@Query("SELECT DISTINCT navidrome_id FROM navidrome_songs")
	suspend fun getAllDistinctNavidromeIds(): List<String>

	@Query("DELETE FROM navidrome_songs WHERE playlist_id = '__library__'")
	suspend fun clearLibrarySongs()

	// ALBUMS

	@Query("SELECT * FROM navidrome_albums ORDER BY name ASC")
	fun getAllAlbums(): Flow<List<AlbumEntity>>

	@Query("SELECT * FROM navidrome_albums")
	suspend fun getAllAlbumsList(): List<AlbumEntity>

	@Query("SELECT * FROM navidrome_albums WHERE id = :albumId LIMIT 1")
	suspend fun getAlbumById(albumId: String): AlbumEntity?

	@Query("SELECT * FROM navidrome_albums WHERE artist_id = :artistId ORDER BY year DESC")
	fun getAlbumsByArtist(artistId: String): Flow<List<AlbumEntity>>

	@Query("SELECT * FROM navidrome_albums WHERE name LIKE '%' || :query || '%'")
	fun searchAlbums(query: String): Flow<List<AlbumEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAlbum(album: AlbumEntity)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAlbums(albums: List<AlbumEntity>)

	@Query("DELETE FROM navidrome_albums WHERE id = :albumId")
	suspend fun deleteAlbum(albumId: String)

	// CLEAR ALL

	@Query("DELETE FROM navidrome_songs")
	suspend fun clearAllSongs()

	@Query("DELETE FROM navidrome_playlists")
	suspend fun clearAllPlaylists()

	@Query("DELETE FROM navidrome_albums")
	suspend fun clearAllAlbums()
}