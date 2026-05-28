package paige.navic.ui.components.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import paige.navic.LocalSnackbarState

@Composable
fun NavicSnackbar(
	snackbarData: SnackbarData,
	modifier: Modifier = Modifier
) {
	Snackbar(
		modifier = modifier,
		snackbarData = snackbarData,
		shape = MaterialTheme.shapes.large,
		containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
		contentColor = MaterialTheme.colorScheme.onSurface,
		actionColor = MaterialTheme.colorScheme.primary,
		actionContentColor = MaterialTheme.colorScheme.primary
	)
}

@Composable
fun InfoSnackbar(
	message: String?,
	onDismiss: () -> Unit
) {
	if (message == null) return
	val snackbarState = LocalSnackbarState.current
	LaunchedEffect(message) {
		snackbarState.showSnackbar(message)
		onDismiss()
	}
}

