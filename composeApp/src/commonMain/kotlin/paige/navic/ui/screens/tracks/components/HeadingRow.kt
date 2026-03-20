package paige.navic.ui.screens.tracks.components

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import dev.zt64.subsonic.api.model.Album
import dev.zt64.subsonic.api.model.Playlist
import dev.zt64.subsonic.api.model.SongCollection
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.info_unknown_genre
import navic.composeapp.generated.resources.info_unknown_year
import navic.composeapp.generated.resources.subtitle_playlist
import org.jetbrains.compose.resources.stringResource
import paige.navic.LocalNavStack
import paige.navic.LocalSharedTransitionScope
import paige.navic.data.models.Screen
import paige.navic.ui.components.common.CoverArt
import paige.navic.ui.theme.defaultFont

@Composable
fun TracksScreenHeadingRow(
	partialTracks: SongCollection,
	tab: String,
	listState: LazyListState,
	sharedTransitionScope: SharedTransitionScope
) {
	val backStack = LocalNavStack.current
	with(LocalSharedTransitionScope.current) {
		CoverArt(
			coverArtId = partialTracks.coverArtId,
			contentDescription = partialTracks.name,
			modifier = Modifier
				.widthIn(0.dp, 420.dp)
				.padding(
					top = 10.dp,
					start = 64.dp,
					end = 64.dp
				)
				.aspectRatio(1f)
				.sharedElement(
					sharedContentState = this@with.rememberSharedContentState("${tab}-${partialTracks.id}-cover"),
					animatedVisibilityScope = LocalNavAnimatedContentScope.current
				),
			crossfadeMs = 0,
			enabled = true
		)
		Spacer(Modifier.height(10.dp))
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			with(sharedTransitionScope) {
				Text(
					partialTracks.name,
					modifier = Modifier.sharedElementWithCallerManagedVisibility(
						sharedContentState = rememberSharedContentState("name"),
						visible = !listState.canScrollBackward
					),
					style = MaterialTheme.typography.headlineSmall,
					textAlign = TextAlign.Center
				)
			}
			val subtitle = when (partialTracks) {
				is Album -> partialTracks.artistName
				is Playlist -> partialTracks.comment
			}
			subtitle?.let { subtitle ->
				Text(
					subtitle,
					color = MaterialTheme.colorScheme.primary,
					modifier = Modifier.clickable(partialTracks is Album) {
						(partialTracks as? Album)?.artistId?.let { id ->
							backStack.add(Screen.Artist(id))
						}
					},
					style = MaterialTheme.typography.bodyMedium,
					fontFamily = defaultFont(grade = 100, round = 100f)
				)
			}
			Text(
				if (partialTracks is Album)
					"${partialTracks.genre ?: stringResource(Res.string.info_unknown_genre)} • ${
						partialTracks.year ?: stringResource(
							Res.string.info_unknown_year
						)
					}"
				else stringResource(Res.string.subtitle_playlist),
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				style = MaterialTheme.typography.bodySmall,
				fontFamily = defaultFont(grade = 100, round = 100f)
			)
		}
	}
}
