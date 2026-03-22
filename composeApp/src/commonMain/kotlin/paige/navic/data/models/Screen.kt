package paige.navic.data.models

import androidx.navigation3.runtime.NavKey
import dev.zt64.subsonic.api.model.AlbumListType
import dev.zt64.subsonic.api.model.Song
import dev.zt64.subsonic.api.model.SongCollection
import kotlinx.serialization.Serializable
import paige.navic.data.database.SongEntity

@Serializable
sealed interface Screen : NavKey {

	// tabs
	@Serializable
	data class Library(
		val nested: Boolean = false
	) : Screen
	@Serializable
	data class PlaylistList(
		val nested: Boolean = false
	) : Screen
	@Serializable
	data class ArtistList(
		val nested: Boolean = false
	) : Screen
	@Serializable
	data class AlbumList(
		val nested: Boolean = false,
		val listType: AlbumListType? = null
	) : Screen
	@Serializable
	data class GenreList(
		val nested: Boolean = false
	) : Screen

	// misc
	@Serializable data object NowPlaying : Screen
	@Serializable data object Lyrics : Screen
	@Serializable data object Queue : Screen
	@Serializable data class TrackList(val partialCollection: TrackCollectionUiModel, val tab: String) : Screen
	@Serializable data class TrackDetail(val track: SongEntity) : Screen
	@Serializable data class Search(
		val nested: Boolean = false
	) : Screen
	@Serializable data object ShareList : Screen
	@Serializable data class ArtistDetail(val artist: String) : Screen

	// settings
	@Serializable
	sealed interface Settings : Screen {
		@Serializable data object Root : Settings
		@Serializable data object Appearance : Settings
		@Serializable data object Playback : Settings
		@Serializable data object Developer : Settings
		@Serializable data object BottomAppBar : Settings
		@Serializable data object NowPlaying : Settings
		@Serializable data object About : Settings
		@Serializable data object Acknowledgements : Settings
		@Serializable data object Fonts : Settings
	}
}