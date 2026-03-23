package paige.navic.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import paige.navic.data.database.entities.AlbumEntity

@Suppress("unused")
@Dao
interface AlbumDao {
	@Query("SELECT * FROM navidrome_albums ORDER BY name COLLATE NOCASE ASC LIMIT :limit")
	fun getAlbumsAlphabeticalByName(limit: Int): Flow<List<AlbumEntity>>

	@Query("SELECT * FROM navidrome_albums ORDER BY artist COLLATE NOCASE ASC LIMIT :limit")
	fun getAlbumsAlphabeticalByArtist(limit: Int): Flow<List<AlbumEntity>>

	@Query("SELECT * FROM navidrome_albums ORDER BY created_at ASC, name COLLATE NOCASE ASC LIMIT :limit")
	fun getAlbumsNewest(limit: Int): Flow<List<AlbumEntity>>

	@Query("SELECT * FROM navidrome_albums ORDER BY RANDOM() LIMIT :limit")
	fun getAlbumsRandom(limit: Int): Flow<List<AlbumEntity>>

	@Query("SELECT * FROM navidrome_albums WHERE starred_at IS NOT NULL ORDER BY starred_at DESC LIMIT :limit")
	fun getAlbumsStarred(limit: Int): Flow<List<AlbumEntity>>

	@Query("SELECT * FROM navidrome_albums WHERE last_played_at IS NOT NULL ORDER BY last_played_at DESC, name COLLATE NOCASE ASC LIMIT :limit")
	fun getAlbumsRecent(limit: Int): Flow<List<AlbumEntity>>

	@Query("SELECT * FROM navidrome_albums WHERE play_count IS NOT 0 ORDER BY play_count DESC, name COLLATE NOCASE ASC LIMIT :limit")
	fun getAlbumsFrequent(limit: Int): Flow<List<AlbumEntity>>

	@Query("SELECT * FROM navidrome_albums ORDER BY name ASC")
	fun getAllAlbums(): Flow<List<AlbumEntity>>

	@Query("SELECT * FROM navidrome_albums")
	suspend fun getAllAlbumsList(): List<AlbumEntity>

	@Query("SELECT * FROM navidrome_albums WHERE id = :albumId LIMIT 1")
	suspend fun getAlbumById(albumId: String): AlbumEntity?

	@Query("SELECT EXISTS(SELECT 1 FROM navidrome_albums WHERE id = :albumId AND starred_at IS NOT NULL)")
	suspend fun isAlbumStarred(albumId: String): Boolean

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

	@Query("DELETE FROM navidrome_albums")
	suspend fun clearAllAlbums()
}
