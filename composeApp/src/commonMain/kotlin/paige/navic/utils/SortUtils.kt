package paige.navic.utils

import dev.zt64.subsonic.api.model.AlbumListType
import paige.navic.data.models.settings.enums.PlaylistSortMode
import paige.navic.domain.models.DomainAlbum
import paige.navic.domain.models.DomainPlaylist

fun List<DomainPlaylist>.sortedByMode(mode: PlaylistSortMode, reversed: Boolean): List<DomainPlaylist> {
	val playlists = when (mode) {
		PlaylistSortMode.Name -> sortedBy { it.name.lowercase() }
		PlaylistSortMode.DateAdded -> sortedBy { it.createdAt }
		PlaylistSortMode.Duration -> sortedBy { it.duration }
	}
	return if (reversed) playlists.reversed() else playlists
}

fun List<DomainAlbum>.sortedByListType(listType: AlbumListType): List<DomainAlbum> {
	return when (listType) {
		AlbumListType.AlphabeticalByArtist -> this.sortedBy { it.artistName.lowercase() }
		AlbumListType.AlphabeticalByName -> this.sortedBy { it.name.lowercase() }
		AlbumListType.Frequent -> this.sortedByDescending { it.playCount }
		AlbumListType.Highest -> this.sortedByDescending { it.userRating }
		AlbumListType.Newest -> this.sortedByDescending { it.createdAt }
		AlbumListType.Random -> this.shuffled()
		AlbumListType.Recent -> this.sortedByDescending { it.lastPlayedAt }
		AlbumListType.Starred -> this.filter { it.starredAt != null }.sortedBy { it.starredAt }
		is AlbumListType.ByGenre -> this.filter { it.genre == listType.genre }
		is AlbumListType.ByYear -> this.filter {
			(it.year ?: 0) >= listType.fromYear
				&& (it.year ?: 0) <= listType.toYear
		}
	}
}
