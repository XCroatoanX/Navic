package paige.navic.ui.screens.tracks.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.zt64.subsonic.api.model.SongCollection
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.count_songs
import org.jetbrains.compose.resources.pluralStringResource
import paige.navic.ui.theme.defaultFont

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TracksScreenFooterRow(
	partialTracks: SongCollection
) {
	Text(
		buildString {
			append(pluralStringResource(
				Res.plurals.count_songs,
				partialTracks.songCount,
				partialTracks.songCount
			))
			append(" • ")
			append(partialTracks.duration.toString())
		},
		style = MaterialTheme.typography.titleSmall,
		fontFamily = defaultFont(round = 100f),
		color = MaterialTheme.colorScheme.onSurfaceVariant,
		modifier = Modifier.fillMaxWidth().padding(
			horizontal = 16.dp,
			vertical = 8.dp
		)
	)
}