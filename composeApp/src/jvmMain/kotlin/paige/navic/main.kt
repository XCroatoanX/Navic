package paige.navic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.application
import paige.navic.data.database.provideCacheDatabase
import paige.navic.di.initKoin

fun main() {
	initKoin {
		printLogger()
	}

	// disable vsync so that animations are smooth on >60hz monitors
	// this should probably be a setting
	System.setProperty("skiko.vsync.enabled", "false")

	// TODO: Remove this after making a DatabaseModule with Koin
	DbContainer.setup(provideCacheDatabase())

	application {
		var window by remember { mutableStateOf<ComposeWindow?>(null) }

		MainWindow(
			window = window,
			onSetWindow = { window = it }
		)

		MainTray(
			window = window
		)
	}
}
