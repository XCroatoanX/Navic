package paige.navic.ui.components.layouts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import paige.navic.di.LocalNavStack
import paige.navic.domain.manager.PreferenceManager
import paige.navic.domain.models.settings.NavbarConfig
import paige.navic.domain.models.settings.NavbarTab
import paige.navic.ui.core.UiState
import paige.navic.ui.navigation.NavItem
import paige.navic.ui.navigation.Screen
import paige.navic.ui.screens.settings.viewmodels.NavtabsViewModel
import paige.navic.ui.theme.NavicTheme
import paige.navic.ui.util.animatedTabIconPainter
import paige.navic.ui.util.rememberColorSchemeForCurrentSong
import paige.navic.ui.viewmodel.RootViewModel

@Composable
fun SideBar(
	modifier: Modifier = Modifier,
	containerColor: Color = NavigationRailDefaults.ContainerColor,
	windowInsets: WindowInsets = NavigationRailDefaults.windowInsets,
) {
	val viewModel = koinViewModel<NavtabsViewModel>()
	val rootViewModel = koinViewModel<RootViewModel>()
	val preferenceManager = koinInject<PreferenceManager>()
	val backStack = LocalNavStack.current
	val state by viewModel.state.collectAsState()
	val tabs = ((state as? UiState.Success)?.data ?: NavbarConfig.default)
		.tabs.filter { tab -> tab.visible }

	val colorScheme = if (preferenceManager.dynamicTheming) {
		rememberColorSchemeForCurrentSong(forceDark = false)
	} else {
		null
	}

	NavicTheme(colorScheme = colorScheme) {
		val onTabSelected = { destination: Screen ->
			if (backStack.lastOrNull() == destination) {
				rootViewModel.requestScrollToTop()
			} else {
				backStack.apply {
					clear()
					add(destination)
				}
			}
		}

		val allTabDestinations = tabs.map { tab ->
			when (tab.id) {
				NavbarTab.Id.LIBRARY -> Screen.Library()
				NavbarTab.Id.ALBUMS -> Screen.AlbumList()
				NavbarTab.Id.PLAYLISTS -> Screen.PlaylistList()
				NavbarTab.Id.ARTISTS -> Screen.ArtistList()
				NavbarTab.Id.SEARCH -> Screen.Search()
				NavbarTab.Id.GENRES -> Screen.GenreList()
				NavbarTab.Id.SONGS -> Screen.SongList()
				NavbarTab.Id.RADIOS -> Screen.RadioList()
			}
		}
		val currentActiveTab = backStack.lastOrNull { entry ->
			allTabDestinations.any { tab -> tab::class == entry::class }
		} ?: backStack.lastOrNull()

		PermanentDrawerSheet(
			modifier = modifier.fillMaxHeight().width(240.dp),
			drawerContainerColor = containerColor,
			windowInsets = windowInsets
		) {
			Column(
				modifier = Modifier
					.fillMaxHeight()
					.padding(horizontal = 12.dp)
			) {
				Spacer(Modifier.padding(top = 16.dp))
				Text(
					text = stringResource(Res.string.app_name),
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.padding(horizontal = 16.dp)
				)

				Spacer(Modifier.padding(top = 16.dp))
				HorizontalDivider(Modifier.padding(horizontal = 16.dp))
				Spacer(Modifier.padding(top = 8.dp))

				Column(
					modifier = Modifier
						.weight(1f)
						.verticalScroll(rememberScrollState())
				) {
					Spacer(Modifier.padding(top = 8.dp))
					tabs.forEach { tab ->
						val item = when (tab.id) {
							NavbarTab.Id.LIBRARY -> NavItem.LIBRARY
							NavbarTab.Id.ALBUMS -> NavItem.ALBUMS
							NavbarTab.Id.PLAYLISTS -> NavItem.PLAYLISTS
							NavbarTab.Id.ARTISTS -> NavItem.ARTISTS
							NavbarTab.Id.SEARCH -> NavItem.SEARCH
							NavbarTab.Id.GENRES -> NavItem.GENRES
							NavbarTab.Id.SONGS -> NavItem.SONGS
							NavbarTab.Id.RADIOS -> NavItem.RADIOS
						}
						val selected = currentActiveTab?.let { it::class == item.destination::class } ?: false

						NavigationDrawerItem(
							label = { Text(stringResource(item.label)) },
							selected = selected,
							onClick = dropUnlessResumed {
								onTabSelected(item.destination)
							},
							icon = {
								if (selected) {
									val painter = animatedTabIconPainter(item.destination)
									if (painter != null) {
										Icon(painter = painter, contentDescription = null)
									} else {
										Icon(item.icon, contentDescription = null)
									}
								} else {
									Icon(item.iconUnselected, contentDescription = null)
								}
							},
							modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
						)
					}
				}

				SidebarMiniPlayer()
			}
		}
	}
}
