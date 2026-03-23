package paige.navic.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import paige.navic.data.database.entities.PlaylistEntity

@Suppress("unused")
@Dao
interface PlaylistDao {
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

	@Query("DELETE FROM navidrome_playlists")
	suspend fun clearAllPlaylists()
}