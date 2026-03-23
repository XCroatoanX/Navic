package paige.navic.utils

import paige.navic.data.models.settings.enums.PlaylistSortMode
import paige.navic.domain.models.DomainPlaylist

fun List<DomainPlaylist>.sortedByMode(mode: PlaylistSortMode, reversed: Boolean): List<DomainPlaylist> {
	val playlists = when (mode) {
		PlaylistSortMode.Name -> sortedBy { it.name.lowercase() }
		PlaylistSortMode.DateAdded -> sortedBy { it.createdAt }
		PlaylistSortMode.Duration -> sortedBy { it.duration }
	}
	return if (reversed) playlists.reversed() else playlists
}
