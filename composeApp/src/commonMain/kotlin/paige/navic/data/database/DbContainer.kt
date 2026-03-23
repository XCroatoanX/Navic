package paige.navic.data.database

import paige.navic.data.database.dao.DatabaseDao

object DbContainer {
	private lateinit var database: CacheDatabase

	fun setup(db: CacheDatabase) {
		database = db
	}

	val dao: DatabaseDao by lazy { database.getDao() }
}