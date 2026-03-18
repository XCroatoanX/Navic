package paige.navic.ui.screens.tracks

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.zt64.subsonic.api.model.Album
import dev.zt64.subsonic.api.model.SongCollection
import paige.navic.LocalMediaPlayer
import paige.navic.data.models.settings.Settings
import paige.navic.ui.components.common.ErrorBox
import paige.navic.ui.components.dialogs.ShareDialog
import paige.navic.ui.components.layouts.RootBottomBar
import paige.navic.ui.screens.tracks.components.TrackRowDropdown
import paige.navic.ui.screens.tracks.components.TracksScreenFooterRow
import paige.navic.ui.screens.tracks.components.TracksScreenHeadingRow
import paige.navic.ui.screens.tracks.components.TracksScreenHeadingRowButtons
import paige.navic.ui.screens.tracks.components.TracksScreenTopBar
import paige.navic.ui.screens.tracks.components.TracksScreenTrackRow
import paige.navic.ui.screens.tracks.components.tracksScreenMoreByArtistRow
import paige.navic.ui.screens.tracks.components.tracksScreenTrackRowPlaceholder
import paige.navic.ui.viewmodels.TracksViewModel
import paige.navic.utils.UiState
import paige.navic.utils.fadeFromTop
import paige.navic.utils.withoutTop
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TracksScreen(
	partialTracks: SongCollection,
	tab: String,
	viewModel: TracksViewModel = viewModel(key = partialTracks.toString()) {
		TracksViewModel(partialTracks)
	}
) {
	val player = LocalMediaPlayer.current

	val tracks by viewModel.tracksState.collectAsState()
	val selection by viewModel.selectedTrack.collectAsState()
	val selectedIndex by viewModel.selectedIndex.collectAsState()

	var shareId by remember { mutableStateOf<String?>(null) }
	var shareExpiry by remember { mutableStateOf<Duration?>(null) }

	val albumInfoState by viewModel.albumInfoState.collectAsState()
	val starredState by viewModel.starredState.collectAsState()
	val artistState by viewModel.artistState.collectAsState()

	SharedTransitionLayout {
		Scaffold(
			topBar = {
				TracksScreenTopBar(
					albumInfoState = albumInfoState,
					tracks = tracks,
					sharedTransitionScope = this@SharedTransitionLayout,
					listState = viewModel.listState,
					onSetShareId = { shareId = it }
				)
			},
			bottomBar = {
				if (Settings.shared.showBarsOnAllScreens) {
					RootBottomBar(scrolled = viewModel.listState.lastScrolledForward)
				}
			}
		) { contentPadding ->
			PullToRefreshBox(
				modifier = Modifier
					.padding(top = contentPadding.calculateTopPadding())
					.background(MaterialTheme.colorScheme.surface),
				isRefreshing = tracks is UiState.Loading
					|| (artistState is UiState.Loading && partialTracks is Album),
				onRefresh = {
					viewModel.refreshTracks()
					viewModel.refreshArtist()
				}
			) {
				LazyColumn(
					modifier = Modifier
						.background(MaterialTheme.colorScheme.surface)
						.fillMaxSize()
						.fadeFromTop(),
					horizontalAlignment = Alignment.CenterHorizontally,
					contentPadding = contentPadding.withoutTop() + PaddingValues(
						top = 16.dp
					),
					state = viewModel.listState
				) {
					item {
						TracksScreenHeadingRow(
							partialTracks = partialTracks,
							tab = tab,
							listState = viewModel.listState,
							sharedTransitionScope = this@SharedTransitionLayout
						)
					}

					val error = (tracks as? UiState.Error)
					val tracks = (tracks as? UiState.Success)?.data
					if (error != null) {
						item { ErrorBox(error) }
						return@LazyColumn
					}
					if (tracks == null) {
						tracksScreenTrackRowPlaceholder(partialTracks.songCount)
						return@LazyColumn
					}

					item { TracksScreenHeadingRowButtons(tracks) }

					itemsIndexed(tracks.songs) { index, track ->
						Box {
							TracksScreenTrackRow(
								track = track,
								index = index,
								count = tracks.songs.count(),
								onClick = {
									player.clearQueue()
									player.addToQueue(tracks)
									player.playAt(index)
								},
								onLongClick = {
									viewModel.selectTrack(track, index)
								}
							)
							TrackRowDropdown(
								expanded = selection == track && selectedIndex == index,
								onDismissRequest = { viewModel.clearSelection() },
								onRemoveStar = { viewModel.unstarSelectedTrack() },
								onAddStar = { viewModel.starSelectedTrack() },
								onShare = { shareId = track.id },
								tracks = tracks,
								track = track,
								onRemoveFromPlaylist = { viewModel.removeFromPlaylist() },
								starredState = starredState
							)
						}
					}

					item { TracksScreenFooterRow(partialTracks) }

					tracksScreenMoreByArtistRow(
						partialTracks = partialTracks,
						artistState = artistState,
						tab = tab
					)
				}
			}
		}
	}

	@Suppress("AssignedValueIsNeverRead")
	ShareDialog(
		id = shareId,
		onIdClear = { shareId = null; viewModel.clearSelection() },
		expiry = shareExpiry,
		onExpiryChange = { shareExpiry = it }
	)
}
