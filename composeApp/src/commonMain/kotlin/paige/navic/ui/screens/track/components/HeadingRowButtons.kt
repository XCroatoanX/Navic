package paige.navic.ui.screens.track.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.action_play
import navic.composeapp.generated.resources.action_shuffle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import paige.navic.data.database.entities.DownloadStatus
import paige.navic.domain.models.DomainSongCollection
import paige.navic.icons.Icons
import paige.navic.icons.filled.Play
import paige.navic.icons.filled.Stop
import paige.navic.icons.outlined.Download
import paige.navic.icons.outlined.Shuffle
import paige.navic.shared.MediaPlayerViewModel

@Composable
fun TracksScreenHeadingRowButtons(
	tracks: DomainSongCollection,
	onDownloadAll: () -> Unit,
	onCancelDownloadAll: () -> Unit,
	downloadStatus: DownloadStatus
) {
	val player = koinViewModel<MediaPlayerViewModel>()
	Row(
		modifier = Modifier.padding(horizontal = 31.dp, vertical = 10.dp),
		verticalAlignment = Alignment.CenterVertically,
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

		val isDownloading = downloadStatus == DownloadStatus.DOWNLOADING
		val isDownloaded = downloadStatus == DownloadStatus.DOWNLOADED
		val enabled = !isDownloaded

		val backgroundColor by animateColorAsState(
			if (isDownloading)
				MaterialTheme.colorScheme.primary
			else Color.Transparent
		)
		val contentColor by animateColorAsState(
			when {
				isDownloading -> MaterialTheme.colorScheme.onPrimary
				enabled -> MaterialTheme.colorScheme.outline
				else -> MaterialTheme.colorScheme.outlineVariant
			}
		)

		Box(
			modifier = Modifier
				.size(40.dp)
				.background(backgroundColor, shape)
				.border(
					border = if (isDownloading)
						BorderStroke(0.dp, MaterialTheme.colorScheme.primary)
					else ButtonDefaults.outlinedButtonBorder(
						enabled = enabled
					),
					shape = shape
				)
				.clip(shape)
				.clickable(enabled = enabled) {
					if (!isDownloading) {
						onDownloadAll()
					} else {
						onCancelDownloadAll()
					}
				},
			contentAlignment = Alignment.Center
		) {
			CompositionLocalProvider(
				LocalContentColor provides contentColor
			) {
				AnimatedContent(
					isDownloading,
					transitionSpec = {
						(fadeIn() + scaleIn()) togetherWith (fadeOut() + scaleOut())
					}
				) { isDownloading ->
					if (isDownloading) {
						Icon(Icons.Filled.Stop, null)
					} else {
						Icon(Icons.Outlined.Download, null)
					}
				}
			}
		}
	}
}
