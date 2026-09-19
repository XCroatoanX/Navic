package paige.navic.ui.screens.settings.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import paige.navic.domain.manager.NavbarManager
import paige.navic.domain.models.settings.NavbarConfig
import paige.navic.domain.models.settings.NavbarTab
import paige.navic.ui.core.UiState

class NavtabsViewModel(
	private val navbarManager: NavbarManager
) : ViewModel() {

	val state: StateFlow<UiState<NavbarConfig>> = navbarManager.config
		.map { UiState.Success(it) }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = UiState.Success(navbarManager.config.value)
		)

	fun move(from: Int, to: Int) {
		navbarManager.move(from, to)
	}

	fun toggleVisibility(id: NavbarTab.Id) {
		navbarManager.toggleVisibility(id)
	}
}
