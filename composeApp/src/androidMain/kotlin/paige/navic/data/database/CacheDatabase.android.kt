package paige.navic.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

private lateinit var applicationContext: Context

fun initAndroidContext(context: Context) {
	applicationContext = context.applicationContext
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<CacheDatabase> {
	val dbFile = applicationContext.getDatabasePath("navic.db")

	return Room.databaseBuilder<CacheDatabase>(
		context = applicationContext,
		name = dbFile.absolutePath
	)
}