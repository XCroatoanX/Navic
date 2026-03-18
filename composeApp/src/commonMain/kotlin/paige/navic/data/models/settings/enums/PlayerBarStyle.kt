package paige.navic.data.models.settings.enums

import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.option_player_bar_style_detached
import navic.composeapp.generated.resources.option_player_bar_style_unified
import org.jetbrains.compose.resources.StringResource

enum class PlayerBarStyle(val displayName: StringResource) {
	Unified(Res.string.option_player_bar_style_unified),
	Detached(Res.string.option_player_bar_style_detached)
}
