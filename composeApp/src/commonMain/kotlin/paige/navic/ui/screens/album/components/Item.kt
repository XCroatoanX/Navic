package paige.navic.ui.screens.album.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.zt64.subsonic.api.model.Album
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.action_remove_star
import navic.composeapp.generated.resources.action_share
import navic.composeapp.generated.resources.action_star
import org.jetbrains.compose.resources.stringResource
import paige.navic.LocalCtx
import paige.navic.LocalNavStack
import paige.navic.data.models.Screen
import paige.navic.icons.Icons
import paige.navic.icons.filled.Star
import paige.navic.icons.outlined.Share
import paige.navic.icons.outlined.Star
import paige.navic.ui.components.common.Dropdown
import paige.navic.ui.components.common.DropdownItem
import paige.navic.ui.components.layouts.ArtGridItem
import paige.navic.ui.screens.album.viewmodels.AlbumListViewModel
import paige.navic.utils.UiState

@Composable
fun AlbumListScreenItem(
	modifier: Modifier = Modifier,
	album: Album,
	tab: String,
	viewModel: AlbumListViewModel,
	onSetShareId: (String) -> Unit
) {
	val ctx = LocalCtx.current
	val backStack = LocalNavStack.current
	val selection by viewModel.selectedAlbum.collectAsState()
	val starredState by viewModel.starredState.collectAsState()
	Box(modifier) {
		ArtGridItem(
			onClick = {
				ctx.clickSound()
				backStack.add(Screen.TrackList(album, tab))
			},
			onLongClick = { viewModel.selectAlbum(album) },
			coverArtId = album.coverArtId,
			title = album.name,
			subtitle = album.artistName,
			id = album.id,
			tab = tab
		)
		Dropdown(
			expanded = selection == album,
			onDismissRequest = {
				viewModel.selectAlbum(null)
			}
		) {
			DropdownItem(
				text = { Text(stringResource(Res.string.action_share)) },
				leadingIcon = { Icon(Icons.Outlined.Share, null) },
				onClick = {
					viewModel.selectAlbum(null)
					onSetShareId(album.id)
				},
			)
			val starred =
				(starredState as? UiState.Success)?.data
			DropdownItem(
				text = {
					Text(
						stringResource(
							if (starred == true)
								Res.string.action_remove_star
							else Res.string.action_star
						)
					)
				},
				leadingIcon = {
					Icon(if (starred == true) Icons.Filled.Star else Icons.Outlined.Star, null)
				},
				onClick = {
					viewModel.starAlbum(starred != true)
					viewModel.selectAlbum(null)
				},
				enabled = starred != null
			)
		}
	}
}
