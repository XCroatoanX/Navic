package paige.navic.data.database

import paige.navic.data.database.dao.*

object DbContainer {
	private lateinit var database: CacheDatabase

	fun setup(db: CacheDatabase) {
		database = db
	}

	val albumDao: AlbumDao by lazy { database.albumDao() }
	val playlistDao: PlaylistDao by lazy { database.playlistDao() }
	val songDao: SongDao by lazy { database.songDao() }
}