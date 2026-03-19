package paige.navic

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.ui.window.ComposeUIViewController

private fun initDb() {
	val db = getRoomDatabase(getDatabaseBuilder())
	AppContainer.setup(db)
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun MainViewController() = ComposeUIViewController {
	remember { initDb() }
	App()
}
