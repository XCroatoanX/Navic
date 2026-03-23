package paige.navic.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import paige.navic.data.database.entities.AlbumEntity
import paige.navic.data.database.relations.AlbumWithSongs

@Suppress("unused")
@Dao
interface AlbumDao {
	@Transaction
	@Query("SELECT * FROM AlbumEntity ORDER BY name COLLATE NOCASE ASC LIMIT :limit")
	fun getAlbumsAlphabeticalByName(limit: Int): Flow<List<AlbumWithSongs>>

	@Transaction
	@Query("SELECT * FROM AlbumEntity ORDER BY artistName COLLATE NOCASE ASC LIMIT :limit")
	fun getAlbumsAlphabeticalByArtist(limit: Int): Flow<List<AlbumWithSongs>>

	@Transaction
	@Query("SELECT * FROM AlbumEntity ORDER BY createdAt ASC, name COLLATE NOCASE ASC LIMIT :limit")
	fun getAlbumsNewest(limit: Int): Flow<List<AlbumWithSongs>>

	@Transaction
	@Query("SELECT * FROM AlbumEntity ORDER BY RANDOM() LIMIT :limit")
	fun getAlbumsRandom(limit: Int): Flow<List<AlbumWithSongs>>

	@Transaction
	@Query("SELECT * FROM AlbumEntity WHERE starredAt IS NOT NULL ORDER BY starredAt DESC LIMIT :limit")
	fun getAlbumsStarred(limit: Int): Flow<List<AlbumWithSongs>>

	@Transaction
	@Query("SELECT * FROM AlbumEntity WHERE lastPlayedAt IS NOT NULL ORDER BY lastPlayedAt DESC, name COLLATE NOCASE ASC LIMIT :limit")
	fun getAlbumsRecent(limit: Int): Flow<List<AlbumWithSongs>>

	@Transaction
	@Query("SELECT * FROM AlbumEntity WHERE playCount IS NOT 0 ORDER BY playCount DESC, name COLLATE NOCASE ASC LIMIT :limit")
	fun getAlbumsFrequent(limit: Int): Flow<List<AlbumWithSongs>>

	@Transaction
	@Query("SELECT * FROM AlbumEntity ORDER BY name ASC")
	fun getAllAlbums(): Flow<List<AlbumWithSongs>>

	@Transaction
	@Query("SELECT * FROM AlbumEntity")
	suspend fun getAllAlbumsList(): List<AlbumWithSongs>

	@Transaction
	@Query("SELECT * FROM AlbumEntity WHERE albumId = :albumId LIMIT 1")
	suspend fun getAlbumById(albumId: String): AlbumWithSongs?

	@Query("SELECT EXISTS(SELECT 1 FROM AlbumEntity WHERE albumId = :albumId AND starredAt IS NOT NULL)")
	suspend fun isAlbumStarred(albumId: String): Boolean

	@Transaction
	@Query("SELECT * FROM AlbumEntity WHERE artistId = :artistId ORDER BY year DESC")
	fun getAlbumsByArtist(artistId: String): Flow<List<AlbumWithSongs>>

	@Transaction
	@Query("SELECT * FROM AlbumEntity WHERE name LIKE '%' || :query || '%'")
	fun searchAlbums(query: String): Flow<List<AlbumWithSongs>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAlbum(album: AlbumEntity)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAlbums(albums: List<AlbumEntity>)

	@Query("DELETE FROM AlbumEntity WHERE albumId = :albumId")
	suspend fun deleteAlbum(albumId: String)

	@Query("DELETE FROM AlbumEntity")
	suspend fun clearAllAlbums()
}
