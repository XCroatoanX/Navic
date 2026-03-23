package paige.navic.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import paige.navic.data.database.entities.SongEntity

@Dao
@Suppress("unused")
interface SongDao {
	@Query("SELECT * FROM navidrome_songs ORDER BY date_cached DESC")
	fun getAllNavidromeSongs(): Flow<List<SongEntity>>

	@Query("SELECT * FROM navidrome_songs WHERE playlist_id = :playlistId ORDER BY track_number ASC")
	fun getSongsByPlaylist(playlistId: String): Flow<List<SongEntity>>

	@Query("SELECT * FROM navidrome_songs WHERE title LIKE '%' || :query || '%' OR artistName LIKE '%' || :query || '%'")
	fun searchSongs(query: String): Flow<List<SongEntity>>

	@Query("SELECT * FROM navidrome_songs WHERE id IN (:ids)")
	fun getSongsByIds(ids: List<String>): Flow<List<SongEntity>>

	@Query("SELECT * FROM navidrome_songs WHERE album_id = :albumId ORDER BY disc_number ASC, track_number ASC")
	suspend fun getSongListByAlbumId(albumId: String): List<SongEntity>

	@Query("SELECT * FROM navidrome_songs WHERE playlist_id = :playlistId")
	suspend fun getSongListByPlaylistId(playlistId: String): List<SongEntity>

	@Query("SELECT * FROM navidrome_songs ORDER BY disc_number ASC, track_number ASC")
	suspend fun getSongList(): List<SongEntity>

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

	@Query("SELECT EXISTS(SELECT 1 FROM navidrome_songs WHERE id = :songId AND starred_at IS NOT NULL)")
	suspend fun isSongStarred(songId: String): Boolean

	@Query("SELECT * FROM navidrome_songs WHERE album_id = :albumId ORDER BY disc_number ASC, track_number ASC")
	fun getSongsFlowByAlbum(albumId: String): Flow<List<SongEntity>>

	@Query("DELETE FROM navidrome_songs")
	suspend fun clearAllSongs()
}