package paige.navic.ui.screens.tracks.components

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import dev.zt64.subsonic.api.model.AlbumInfo
import dev.zt64.subsonic.api.model.SongCollection
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.action_add_all_to_playlist
import navic.composeapp.generated.resources.action_more
import navic.composeapp.generated.resources.action_share
import navic.composeapp.generated.resources.action_view_on_lastfm
import navic.composeapp.generated.resources.action_view_on_musicbrainz
import org.jetbrains.compose.resources.stringResource
import paige.navic.LocalNavStack
import paige.navic.data.models.Screen
import paige.navic.icons.Icons
import paige.navic.icons.brand.Lastfm
import paige.navic.icons.brand.Musicbrainz
import paige.navic.icons.outlined.MoreVert
import paige.navic.icons.outlined.PlaylistAdd
import paige.navic.icons.outlined.Share
import paige.navic.ui.components.common.Dropdown
import paige.navic.ui.components.common.DropdownItem
import paige.navic.ui.components.layouts.NestedTopBar
import paige.navic.ui.components.layouts.TopBarButton
import paige.navic.utils.UiState
import kotlin.collections.orEmpty

@Composable
fun TracksScreenTopBar(
	tracks: UiState<SongCollection>,
	albumInfoState: UiState<AlbumInfo>,
	onSetShareId: (shareId: String?) -> Unit,
	sharedTransitionScope: SharedTransitionScope,
	listState: LazyListState
) {
	val backStack = LocalNavStack.current
	val uriHandler = LocalUriHandler.current
	NestedTopBar(
		title = {
			with(sharedTransitionScope) {
				Text(
					(tracks as? UiState.Success)?.data?.name ?: "",
					modifier = Modifier.sharedElementWithCallerManagedVisibility(
						sharedContentState = rememberSharedContentState("name"),
						visible = listState.canScrollBackward
					)
				)
			}
		},
		actions = {
			Box {
				var expanded by remember { mutableStateOf(false) }
				TopBarButton({
					expanded = true
				}) {
					Icon(
						Icons.Outlined.MoreVert,
						stringResource(Res.string.action_more)
					)
				}
				Dropdown(
					expanded = expanded,
					onDismissRequest = { expanded = false }
				) {
					val info = (albumInfoState as? UiState.Success)?.data
					DropdownItem(
						text = { Text(stringResource(Res.string.action_view_on_lastfm)) },
						leadingIcon = { Icon(Icons.Brand.Lastfm, null) },
						enabled = albumInfoState is UiState.Success
							&& info?.lastFmUrl != null,
						onClick = {
							expanded = false
							info?.lastFmUrl?.let { url ->
								uriHandler.openUri(url)
							}
						}
					)
					DropdownItem(
						text = { Text(stringResource(Res.string.action_view_on_musicbrainz)) },
						leadingIcon = { Icon(Icons.Brand.Musicbrainz, null) },
						enabled = albumInfoState is UiState.Success
							&& info?.musicBrainzId != null,
						onClick = {
							expanded = false
							info?.musicBrainzId?.let { id ->
								uriHandler.openUri(
									"https://musicbrainz.org/release/$id"
								)
							}
						}
					)
					DropdownItem(
						text = { Text(stringResource(Res.string.action_share)) },
						leadingIcon = { Icon(Icons.Outlined.Share, null) },
						containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
						enabled = tracks is UiState.Success,
						onClick = {
							expanded = false
							onSetShareId((tracks as? UiState.Success)?.data?.id)
						},
					)
					DropdownItem(
						text = { Text(stringResource(Res.string.action_add_all_to_playlist)) },
						leadingIcon = { Icon(Icons.Outlined.PlaylistAdd, null) },
						containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
						enabled = tracks is UiState.Success,
						onClick = {
							expanded = false
							if (backStack.lastOrNull() !is Screen.AddToPlaylist) {
								backStack.add(
									Screen.AddToPlaylist(
										(tracks as? UiState.Success)?.data?.songs.orEmpty()
									)
								)
							}
						},
					)
				}
			}
		}
	)
}
