package paige.navic.utils

import dev.zt64.subsonic.api.model.Playlist
import paige.navic.data.models.settings.enums.PlaylistSortMode

fun List<Playlist>.sortedByMode(mode: PlaylistSortMode, reversed: Boolean): List<Playlist> {
	val playlists = when (mode) {
		PlaylistSortMode.Name -> sortedBy { it.name.lowercase() }
		PlaylistSortMode.DateAdded -> sortedBy { it.createdAt }
		PlaylistSortMode.Duration -> sortedBy { it.duration }
	}
	if (reversed) return playlists.reversed()
	return playlists
}
