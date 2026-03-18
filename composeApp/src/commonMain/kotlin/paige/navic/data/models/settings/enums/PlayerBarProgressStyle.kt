package paige.navic.data.models.settings.enums

import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.option_player_bar_progress_style_hidden
import navic.composeapp.generated.resources.option_player_bar_progress_style_seekable
import navic.composeapp.generated.resources.option_player_bar_progress_style_visible
import org.jetbrains.compose.resources.StringResource

enum class PlayerBarProgressStyle(val displayName: StringResource) {
	Hidden(Res.string.option_player_bar_progress_style_hidden),
	Visible(Res.string.option_player_bar_progress_style_visible),
	Seekable(Res.string.option_player_bar_progress_style_seekable)
}
