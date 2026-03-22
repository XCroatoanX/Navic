package paige.navic.ui.screens.album

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.zt64.subsonic.api.model.AlbumListType
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.info_needs_log_in
import navic.composeapp.generated.resources.info_no_albums
import navic.composeapp.generated.resources.title_albums
import org.jetbrains.compose.resources.stringResource
import paige.navic.data.models.settings.Settings
import paige.navic.data.models.settings.enums.BottomBarVisibilityMode
import paige.navic.data.session.SessionManager
import paige.navic.icons.Icons
import paige.navic.icons.outlined.Album
import paige.navic.ui.components.common.ContentUnavailable
import paige.navic.ui.screens.share.dialogs.ShareDialog
import paige.navic.ui.components.layouts.ArtGrid
import paige.navic.ui.components.layouts.NestedTopBar
import paige.navic.ui.components.layouts.RootBottomBar
import paige.navic.ui.components.layouts.RootTopBar
import paige.navic.ui.components.layouts.artGridError
import paige.navic.ui.components.layouts.artGridPlaceholder
import paige.navic.ui.screens.album.components.AlbumListScreenItem
import paige.navic.ui.screens.album.components.AlbumListScreenSortButton
import paige.navic.ui.screens.album.viewmodels.AlbumListViewModel
import paige.navic.utils.LocalBottomBarScrollManager
import paige.navic.utils.UiState
import paige.navic.utils.withoutTop
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AlbumListScreen(
	nested: Boolean = false,
	listType: AlbumListType? = null,
	viewModel: AlbumListViewModel = viewModel(key = listType.toString()) {
		AlbumListViewModel(listType)
	}
) {
	val albumsState by viewModel.albumsState.collectAsState()
	var shareId by remember { mutableStateOf<String?>(null) }
	var shareExpiry by remember { mutableStateOf<Duration?>(null) }
	val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
	val isRefreshing by viewModel.isRefreshing.collectAsState()
	val isPaginating by viewModel.isPaginating.collectAsState()
	val actions: @Composable RowScope.() -> Unit = {
		if (listType == null) {
			AlbumListScreenSortButton(!nested, viewModel)
		}
	}
	val isLoggedIn by SessionManager.isLoggedIn.collectAsState()

	Scaffold(
		topBar = {
			if (!nested) {
				RootTopBar(
					{ Text(stringResource(Res.string.title_albums)) },
					scrollBehavior,
					actions
				)
			} else {
				NestedTopBar({ Text(stringResource(Res.string.title_albums)) }, actions)
			}
		},
		bottomBar = {
			val scrollManager = LocalBottomBarScrollManager.current
			if (!nested || Settings.shared.bottomBarVisibilityMode == BottomBarVisibilityMode.AllScreens) {
				RootBottomBar(scrolled = scrollManager.isTriggered)
			}
		}
	) { innerPadding ->
		PullToRefreshBox(
			modifier = Modifier
				.padding(top = innerPadding.calculateTopPadding())
				.background(MaterialTheme.colorScheme.surface),
			isRefreshing = isRefreshing || albumsState is UiState.Loading,
			onRefresh = { viewModel.refreshAlbums() }
		) {
			if (!isLoggedIn) {
				Text(
					stringResource(Res.string.info_needs_log_in),
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier.padding(horizontal = 16.dp)
				)
				return@PullToRefreshBox
			}
			Crossfade(albumsState) { state ->
				ArtGrid(
					modifier = if (!nested)
						Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
					else Modifier,
					state = viewModel.gridState,
					contentPadding = innerPadding.withoutTop(),
					verticalArrangement = if ((state as? UiState.Success)?.data?.isEmpty() == true)
						Arrangement.Center
					else Arrangement.spacedBy(12.dp)
				) {
					when (state) {
						is UiState.Loading -> artGridPlaceholder()
						is UiState.Error -> artGridError(state)
						is UiState.Success -> {
							items(state.data, { it.id }) { album ->
								AlbumListScreenItem(
									modifier = Modifier.animateItem(fadeInSpec = null),
									album = album,
									viewModel = viewModel,
									tab = "albums",
									onSetShareId = { newShareId ->
										shareId = newShareId
									}
								)
							}

							if (state.data.isEmpty()) {
								item(span = { GridItemSpan(maxLineSpan) }) {
									ContentUnavailable(
										icon = Icons.Outlined.Album,
										label = stringResource(Res.string.info_no_albums)
									)
								}
							} else {
								item(span = { GridItemSpan(maxLineSpan) }) {
									LaunchedEffect(viewModel.gridState) {
										snapshotFlow { viewModel.gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
											.collect { lastVisible ->
												val totalItems =
													viewModel.gridState.layoutInfo.totalItemsCount
												if (lastVisible != null && lastVisible >= totalItems - 1 && !isPaginating) {
													viewModel.paginate()
												}
											}
									}
									if (isPaginating) {
										Row(horizontalArrangement = Arrangement.Center) {
											ContainedLoadingIndicator(Modifier.size(48.dp))
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Suppress("AssignedValueIsNeverRead")
	ShareDialog(
		id = shareId,
		onIdClear = { shareId = null },
		expiry = shareExpiry,
		onExpiryChange = { shareExpiry = it }
	)
}
