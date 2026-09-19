package paige.navic.domain.manager

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import paige.navic.domain.models.settings.NavbarConfig
import paige.navic.domain.models.settings.NavbarTab

class NavbarManager(private val settings: Settings) {
	private val json = Json
	private val _config = MutableStateFlow(loadConfig())
	val config: StateFlow<NavbarConfig> = _config.asStateFlow()

	private fun loadConfig(): NavbarConfig {
		val raw = settings.getStringOrNull(NavbarConfig.KEY)
			?: return NavbarConfig.default
		return try {
			val config: NavbarConfig = json.decodeFromString(raw)
			if (config.version == NavbarConfig.VERSION) config else NavbarConfig.default
		} catch (_: Exception) {
			NavbarConfig.default
		}
	}

	private fun saveConfig(newConfig: NavbarConfig) {
		_config.value = newConfig
		settings[NavbarConfig.KEY] = json.encodeToString(newConfig)
	}

	fun move(from: Int, to: Int) {
		val current = _config.value
		val newTabs = current.tabs.toMutableList().apply {
			add(to, removeAt(from))
		}
		saveConfig(current.copy(tabs = newTabs))
	}

	fun toggleVisibility(id: NavbarTab.Id) {
		val current = _config.value
		val newTabs = current.tabs.map {
			if (it.id == id) it.copy(visible = !it.visible) else it
		}
		saveConfig(current.copy(tabs = newTabs))
	}
}
