package paige.navic.data.models.settings.enums

import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.option_sort_playlist_by_name
import navic.composeapp.generated.resources.option_sort_playlist_date_added
import navic.composeapp.generated.resources.option_sort_playlist_duration
import org.jetbrains.compose.resources.StringResource

enum class PlaylistSortMode(val displayName: StringResource) {
	Name(Res.string.option_sort_playlist_by_name),
	DateAdded(Res.string.option_sort_playlist_date_added),
	Duration(Res.string.option_sort_playlist_duration)
}