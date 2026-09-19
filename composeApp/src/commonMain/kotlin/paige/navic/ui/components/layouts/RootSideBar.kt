package paige.navic.ui.components.layouts

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun RootSideBar(
	modifier: Modifier = Modifier,
	containerColor: Color = NavigationRailDefaults.ContainerColor,
	windowInsets: WindowInsets = NavigationRailDefaults.windowInsets,
) {
	SideBar(
		modifier = modifier,
		containerColor = containerColor,
		windowInsets = windowInsets
	)
}
