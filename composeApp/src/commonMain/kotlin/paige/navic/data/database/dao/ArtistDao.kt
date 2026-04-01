package paige.navic.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import paige.navic.data.database.entities.ArtistEntity

@Suppress("unused")
@Dao
interface ArtistDao {
	@Query("SELECT * FROM ArtistEntity ORDER BY name COLLATE NOCASE ASC LIMIT :limit")
	fun getArtistsAlphabeticalByName(limit: Int): Flow<List<ArtistEntity>>

	@Query("SELECT * FROM ArtistEntity ORDER BY RANDOM() LIMIT :limit")
	fun getArtistsRandom(limit: Int): Flow<List<ArtistEntity>>

	@Query("SELECT * FROM ArtistEntity WHERE starredAt IS NOT NULL ORDER BY starredAt DESC LIMIT :limit")
	fun getArtistsStarred(limit: Int): Flow<List<ArtistEntity>>

	@Query("SELECT * FROM ArtistEntity ORDER BY name COLLATE NOCASE ASC")
	fun getAllArtists(): Flow<List<ArtistEntity>>

	@Query("SELECT * FROM ArtistEntity")
	suspend fun getAllArtistsList(): List<ArtistEntity>

	@Query("SELECT * FROM ArtistEntity WHERE artistId = :artistId LIMIT 1")
	suspend fun getArtistById(artistId: String): ArtistEntity?

	@Query("SELECT EXISTS(SELECT 1 FROM ArtistEntity WHERE artistId = :artistId AND starredAt IS NOT NULL)")
	suspend fun isArtistStarred(artistId: String): Boolean

	@Query("SELECT * FROM ArtistEntity WHERE name LIKE '%' || :query || '%' COLLATE NOCASE")
	fun searchArtists(query: String): Flow<List<ArtistEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertArtist(artist: ArtistEntity)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertArtists(artists: List<ArtistEntity>)

	@Query("DELETE FROM ArtistEntity WHERE artistId = :artistId")
	suspend fun deleteArtist(artistId: String)

	@Query("DELETE FROM ArtistEntity")
	suspend fun clearAllArtists()
}