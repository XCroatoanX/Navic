package paige.navic.ui.screens.lyrics.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.info_no_lyrics
import org.jetbrains.compose.resources.stringResource
import paige.navic.icons.Icons
import paige.navic.icons.outlined.Lyrics
import paige.navic.ui.components.common.ContentUnavailable

@Composable
fun LyricsScreenPlaceholder() {
	ContentUnavailable(
		modifier = Modifier.fillMaxSize(),
		icon = Icons.Outlined.Lyrics,
		label = stringResource(Res.string.info_no_lyrics)
	)
}
