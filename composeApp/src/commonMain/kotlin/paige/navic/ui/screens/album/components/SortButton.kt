package paige.navic.ui.screens.album.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.zt64.subsonic.api.model.AlbumListType
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.option_sort_alphabetical_by_artist
import navic.composeapp.generated.resources.option_sort_alphabetical_by_name
import navic.composeapp.generated.resources.option_sort_frequent
import navic.composeapp.generated.resources.option_sort_newest
import navic.composeapp.generated.resources.option_sort_random
import navic.composeapp.generated.resources.option_sort_recent
import navic.composeapp.generated.resources.option_sort_starred
import org.jetbrains.compose.resources.stringResource
import paige.navic.icons.Icons
import paige.navic.icons.outlined.Sort
import paige.navic.ui.components.common.SelectionDropdown
import paige.navic.ui.components.layouts.TopBarButton
import paige.navic.ui.screens.album.viewmodels.AlbumListViewModel

@Composable
fun AlbumListScreenSortButton(
	root: Boolean,
	viewModel: AlbumListViewModel
) {
	val currentListType by viewModel.listType.collectAsState()
	val items = remember {
		listOf(
			AlbumListType.Random,
			AlbumListType.Newest,
			AlbumListType.Frequent,
			AlbumListType.Recent,
			AlbumListType.Starred,
			AlbumListType.AlphabeticalByArtist,
		)
	}
	Box {
		var expanded by remember { mutableStateOf(false) }
		if (root) {
			IconButton(onClick = {
				expanded = true
			}) {
				Icon(
					Icons.Outlined.Sort,
					contentDescription = null
				)
			}
		} else {
			TopBarButton({
				expanded = true
			}) {
				Icon(
					Icons.Outlined.Sort,
					contentDescription = null
				)
			}
		}
		SelectionDropdown(
			items = items,
			label = {
				it.label()
			},
			expanded = expanded,
			onDismissRequest = { expanded = false },
			selection = currentListType,
			onSelect = {
				viewModel.setListType(it)
				viewModel.refreshAlbums()
			}
		)
	}
}

@Composable
private fun AlbumListType.label() =
	when (this) {
		AlbumListType.Random -> stringResource(Res.string.option_sort_random)
		AlbumListType.Newest -> stringResource(Res.string.option_sort_newest)
		AlbumListType.Frequent -> stringResource(Res.string.option_sort_frequent)
		AlbumListType.Recent -> stringResource(Res.string.option_sort_recent)
		AlbumListType.AlphabeticalByName -> stringResource(Res.string.option_sort_alphabetical_by_name)
		AlbumListType.AlphabeticalByArtist -> stringResource(Res.string.option_sort_alphabetical_by_artist)
		AlbumListType.Starred -> stringResource(Res.string.option_sort_starred)
		else -> "$this"
	}
