package paige.navic.utils

import paige.navic.data.database.PlaylistEntity
import paige.navic.data.models.settings.enums.PlaylistSortMode

fun List<PlaylistEntity>.sortedByMode(mode: PlaylistSortMode, reversed: Boolean): List<PlaylistEntity> {
	val playlists = when (mode) {
		PlaylistSortMode.Name -> sortedBy { it.name.lowercase() }
		PlaylistSortMode.DateAdded -> sortedBy { it.createdAt }
		PlaylistSortMode.Duration -> sortedBy { it.duration }
	}
	return if (reversed) playlists.reversed() else playlists
}
