package paige.navic.ui.screens.tracks.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.zt64.subsonic.api.model.SongCollection
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.action_play
import navic.composeapp.generated.resources.action_shuffle
import org.jetbrains.compose.resources.stringResource
import paige.navic.LocalMediaPlayer
import paige.navic.icons.Icons
import paige.navic.icons.filled.Play
import paige.navic.icons.outlined.Shuffle

@Composable
fun TracksScreenHeadingRowButtons(
	tracks: SongCollection
) {
	val player = LocalMediaPlayer.current
	Row(
		modifier = Modifier.padding(horizontal = 31.dp, vertical = 10.dp),
		horizontalArrangement = Arrangement.spacedBy(
			10.dp,
			alignment = Alignment.CenterHorizontally
		)
	) {
		val shape = MaterialTheme.shapes.medium
		FilledTonalButton(
			modifier = Modifier.weight(1f),
			onClick = {
				player.clearQueue()
				player.addToQueue(tracks)
				player.playAt(0)
			},
			shape = shape
		) {
			Icon(Icons.Filled.Play, null)
			Text(
				stringResource(Res.string.action_play),
				maxLines = 1,
				autoSize = TextAutoSize.StepBased(
					minFontSize = 1.sp,
					maxFontSize = MaterialTheme.typography.labelLarge.fontSize
				)
			)
		}
		OutlinedButton(
			modifier = Modifier.weight(1f),
			onClick = {
				player.shufflePlay(tracks)
			},
			shape = shape
		) {
			Icon(Icons.Outlined.Shuffle, null)
			Text(
				stringResource(Res.string.action_shuffle),
				maxLines = 1,
				autoSize = TextAutoSize.StepBased(
					minFontSize = 1.sp,
					maxFontSize = MaterialTheme.typography.labelLarge.fontSize
				)
			)
		}
	}
}
