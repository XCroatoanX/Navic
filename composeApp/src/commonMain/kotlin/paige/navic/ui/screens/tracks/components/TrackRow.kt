package paige.navic.ui.screens.tracks.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.zt64.subsonic.api.model.Song
import paige.navic.ui.components.common.MarqueeText
import paige.navic.utils.toHoursMinutesSeconds

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TracksScreenTrackRow(
	track: Song,
	index: Int,
	count: Int,
	onClick: (() -> Unit),
	onLongClick: (() -> Unit)
) {
	SegmentedListItem(
		modifier = Modifier.padding(
			start = 16.dp,
			end = 16.dp,
			bottom = ListItemDefaults.SegmentedGap
		),
		onClick = onClick,
		onLongClick = onLongClick,
		shapes = ListItemDefaults.segmentedShapes(
			index = index,
			count = count
		),
		colors = ListItemDefaults.segmentedColors(
			containerColor = MaterialTheme.colorScheme.surfaceContainer
		),
		leadingContent = {
			Text(
				"${index + 1}",
				modifier = Modifier.width(25.dp),
				style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum"),
				fontWeight = FontWeight(400),
				fontSize = 13.sp,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				maxLines = 1,
				textAlign = TextAlign.Center
			)
		},
		content = {
			Column {
				MarqueeText(track.title)
				Text(
					track.artistName,
					style = MaterialTheme.typography.bodySmall,
					maxLines = 1
				)
			}
		},
		trailingContent = {
			track.duration.toHoursMinutesSeconds().let {
				Text(
					text = it,
					style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum"),
					fontWeight = FontWeight(400),
					fontSize = 13.sp,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					maxLines = 1
				)
			}
		}
	)
}
