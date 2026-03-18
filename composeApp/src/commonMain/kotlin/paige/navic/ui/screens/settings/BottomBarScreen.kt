package paige.navic.ui.screens.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.capsule.ContinuousRoundedRectangle
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.option_hide_bars_on_scroll
import navic.composeapp.generated.resources.option_navbar_tab_positions
import navic.composeapp.generated.resources.option_progress_in_bar_is_seekable
import navic.composeapp.generated.resources.option_short_navigation_bar
import navic.composeapp.generated.resources.option_show_bars_on_all_screens
import navic.composeapp.generated.resources.option_show_progress_in_bar
import navic.composeapp.generated.resources.option_swipe_to_skip
import navic.composeapp.generated.resources.option_use_detached_bar
import navic.composeapp.generated.resources.subtitle_show_bars_on_all_screens
import navic.composeapp.generated.resources.suibtitle_progress_in_bar_is_seekable
import navic.composeapp.generated.resources.title_bottom_app_bar
import org.jetbrains.compose.resources.stringResource
import paige.navic.LocalCtx
import paige.navic.data.models.Settings
import paige.navic.data.models.Settings.ThemeMode
import paige.navic.ui.components.common.Form
import paige.navic.ui.components.common.FormRow
import paige.navic.ui.components.dialogs.NavtabsDialog
import paige.navic.ui.components.layouts.NestedTopBar
import paige.navic.ui.components.layouts.RootBottomBar
import paige.navic.ui.components.settings.SettingSwitchRow
import paige.navic.ui.theme.defaultFont
import paige.navic.utils.fadeFromTop

@Composable
fun BottomBarScreen() {
	val ctx = LocalCtx.current
	var showNavtabsDialog by rememberSaveable { mutableStateOf(false) }

	Scaffold(
		topBar = { NestedTopBar(
			{ Text(stringResource(Res.string.title_bottom_app_bar)) },
			hideBack = ctx.sizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
		) },
		bottomBar = {
			BottomBarPreview()
		},
		contentWindowInsets = WindowInsets.statusBars
	) { innerPadding ->
		CompositionLocalProvider(
			LocalMinimumInteractiveComponentSize provides 0.dp
		) {
			Column(
				Modifier
					.padding(innerPadding)
					.verticalScroll(rememberScrollState())
					.padding(top = 16.dp, end = 16.dp, start = 16.dp)
					.fadeFromTop()
			) {
				Form {
					SettingSwitchRow(
						title = { Text(stringResource(Res.string.option_short_navigation_bar)) },
						value = Settings.shared.useShortNavbar,
						onSetValue = { Settings.shared.useShortNavbar = it }
					)

					SettingSwitchRow(
						title = { Text(stringResource(Res.string.option_use_detached_bar)) },
						value = Settings.shared.detachedBar,
						onSetValue = { Settings.shared.detachedBar = it }
					)

					SettingSwitchRow(
						title = { Text(stringResource(Res.string.option_swipe_to_skip)) },
						value = Settings.shared.swipeToSkip,
						onSetValue = { Settings.shared.swipeToSkip = it }
					)

					SettingSwitchRow(
						title = { Text(stringResource(Res.string.option_show_progress_in_bar)) },
						value = Settings.shared.showProgressInBar,
						onSetValue = { Settings.shared.showProgressInBar = it }
					)

					if (Settings.shared.showProgressInBar) {
						SettingSwitchRow(
							title = { Text(stringResource(Res.string.option_progress_in_bar_is_seekable)) },
							subtitle = { Text(stringResource(Res.string.suibtitle_progress_in_bar_is_seekable)) },
							value = Settings.shared.progressInBarIsSeekable,
							onSetValue = { Settings.shared.progressInBarIsSeekable = it }
						)
					}

					SettingSwitchRow(
						title = { Text(stringResource(Res.string.option_hide_bars_on_scroll)) },
						value = Settings.shared.hideBarsOnScroll,
						onSetValue = { Settings.shared.hideBarsOnScroll = it }
					)

					SettingSwitchRow(
						title = { Text(stringResource(Res.string.option_show_bars_on_all_screens)) },
						subtitle = { Text(stringResource(Res.string.subtitle_show_bars_on_all_screens)) },
						value = Settings.shared.showBarsOnAllScreens,
						onSetValue = { Settings.shared.showBarsOnAllScreens = it }
					)

					FormRow(
						onClick = { showNavtabsDialog = true }
					) {
						Text(stringResource(Res.string.option_navbar_tab_positions))
					}
				}
			}
		}
		NavtabsDialog(
			presented = showNavtabsDialog,
			onDismissRequest = { showNavtabsDialog = false }
		)
	}
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BottomBarPreview() {
	val inDarkTheme = isSystemInDarkTheme()
	val isDark = remember(Settings.shared.themeMode) {
		when (Settings.shared.themeMode) {
			ThemeMode.System -> inDarkTheme
			ThemeMode.Dark -> true
			ThemeMode.Light -> false
		}
	}
	val spec = MaterialTheme.motionScheme.defaultSpatialSpec<Dp>()
	val padding by animateDpAsState(
		if (Settings.shared.detachedBar) 12.dp else 32.dp, spec
	)
	val rounding by animateDpAsState(
		if (Settings.shared.detachedBar) 28.dp else 12.dp, spec
	)
	Column(Modifier.padding(16.dp).navigationBarsPadding()) {
		Text(
			"Preview",
			style = MaterialTheme.typography.titleSmallEmphasized,
			fontWeight = FontWeight(600),
			fontFamily = defaultFont(round = 100f),
			textAlign = TextAlign.Center,
			modifier = Modifier.fillMaxWidth()
		)
		Spacer(Modifier.height(10.dp))
		RootBottomBar(
			scrolled = false,
			shadows = false,
			modifier = Modifier
				.clip(ContinuousRoundedRectangle(rounding))
				.background(
					if (isDark)
						MaterialTheme.colorScheme.surfaceContainerLow
					else MaterialTheme.colorScheme.surfaceContainerHighest
				)
				.fillMaxWidth()
				.padding(top = padding),
			bottomBarWindowInsets = WindowInsets()
		)
	}
}
