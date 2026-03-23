package paige.navic.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import paige.navic.data.database.entities.PlaylistEntity
import paige.navic.data.database.relations.PlaylistWithSongs

@Suppress("unused")
@Dao
interface PlaylistDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertPlaylist(playlist: PlaylistEntity)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertPlaylists(playlists: List<PlaylistEntity>)


	@Transaction
	@Query("SELECT * FROM PlaylistEntity ORDER BY name ASC")
	fun getAllPlaylists(): List<PlaylistWithSongs>

	@Transaction
	@Query("SELECT * FROM PlaylistEntity ORDER BY name ASC")
	fun getAllPlaylistsFlow(): Flow<List<PlaylistWithSongs>>

	@Transaction
	@Query("SELECT * FROM PlaylistEntity WHERE playlistId = :playlistId LIMIT 1")
	suspend fun getPlaylistById(playlistId: String): PlaylistWithSongs?

	@Query("DELETE FROM PlaylistEntity WHERE playlistId = :playlistId")
	suspend fun deletePlaylist(playlistId: String)

	@Query("SELECT COUNT(*) FROM PlaylistEntity")
	suspend fun getPlaylistCount(): Int

	@Query("DELETE FROM PlaylistEntity")
	suspend fun clearAllPlaylists()
}