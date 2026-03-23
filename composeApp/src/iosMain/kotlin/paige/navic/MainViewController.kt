package paige.navic

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import paige.navic.data.database.DbContainer
import paige.navic.data.database.getDatabaseBuilder
import paige.navic.data.database.getRoomDatabase

private fun initDb() {
	val db = getRoomDatabase(getDatabaseBuilder())
	DbContainer.setup(db)
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun MainViewController() = ComposeUIViewController {
	remember { initDb() }
	App()
}
