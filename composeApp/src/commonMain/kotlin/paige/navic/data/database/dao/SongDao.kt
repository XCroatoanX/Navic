package paige.navic.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import paige.navic.data.database.entities.SongEntity

@Dao
@Suppress("unused")
interface SongDao {
	@Query("SELECT * FROM SongEntity WHERE songId = :songId LIMIT 1")
	suspend fun getSongById(songId: String): SongEntity?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertSong(song: SongEntity)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertSongs(songs: List<SongEntity>)

	@Query("DELETE FROM SongEntity WHERE songId = :songId")
	suspend fun deleteSong(songId: String)

	// TODO
	@Query("SELECT EXISTS(SELECT 1 FROM SongEntity WHERE songId = :songId AND starredAt IS NOT NULL)")
	suspend fun isSongStarred(songId: String): Boolean

	@Query("DELETE FROM SongEntity")
	suspend fun clearAllSongs()
}