package paige.navic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.ktor.client.plugins.cache.storage.FileStorage
import paige.navic.data.database.DbContainer
import paige.navic.data.database.getDatabaseBuilder
import paige.navic.data.database.getRoomDatabase
import paige.navic.data.database.initAndroidContext
import paige.navic.data.session.SessionManager
import java.io.File

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initAndroidContext(this)

		val db = getRoomDatabase(getDatabaseBuilder())
		DbContainer.setup(db)

		enableEdgeToEdge()
		SessionManager.cacheStorage = FileStorage(File(cacheDir, "http_cache"))
		setContent { App() }
	}
}
