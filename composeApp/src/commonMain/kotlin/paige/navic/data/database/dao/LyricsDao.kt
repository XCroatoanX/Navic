package paige.navic.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import paige.navic.data.database.entities.LyricEntity

@Suppress("unused")
@Dao
interface LyricDao {
	@Query("SELECT * FROM LyricEntity WHERE songId = :songId")
	suspend fun getLyrics(songId: String): LyricEntity?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertLyrics(lyrics: LyricEntity)

	@Query("DELETE FROM LyricEntity")
	suspend fun clearAllLyrics()
}