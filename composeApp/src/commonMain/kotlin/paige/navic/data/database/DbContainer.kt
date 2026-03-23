package paige.navic.data.database

import paige.navic.data.database.dao.DatabaseDao

object DbContainer {
	private lateinit var database: AppDatabase

	fun setup(db: AppDatabase) {
		database = db
	}

	val dao: DatabaseDao by lazy { database.getDao() }
}