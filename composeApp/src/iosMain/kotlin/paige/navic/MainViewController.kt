package paige.navic

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import paige.navic.data.database.DbContainer
import paige.navic.data.database.provideCacheDatabase

private fun initDb() {
	DbContainer.setup(provideCacheDatabase())
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun MainViewController() = ComposeUIViewController {
	// TODO: Remove this after making a DatabaseModule with Koin
	remember { initDb() }
	App()
}
