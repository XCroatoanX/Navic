package paige.navic.di

import dev.zt64.subsonic.api.model.AlbumListType
import org.koin.dsl.module
import paige.navic.domain.models.DomainSong
import paige.navic.domain.models.DomainSongCollection
import paige.navic.ui.screens.album.viewmodels.AlbumListViewModel
import paige.navic.ui.screens.artist.viewmodels.ArtistDetailViewModel
import paige.navic.ui.screens.artist.viewmodels.ArtistListViewModel
import paige.navic.ui.screens.genre.viewmodels.GenreListViewModel
import paige.navic.ui.screens.lyrics.viewmodels.LyricsScreenViewModel
import paige.navic.ui.screens.playlist.viewmodels.PlaylistCreateDialogViewModel
import paige.navic.ui.screens.playlist.viewmodels.PlaylistListViewModel
import paige.navic.ui.screens.search.viewmodels.SearchViewModel
import paige.navic.ui.screens.track.viewmodels.TrackListViewModel
import paige.navic.ui.viewmodels.LoginViewModel

val viewModelModule = module {
	factory { (initialListType: AlbumListType?) ->
		AlbumListViewModel(
			initialListType = initialListType,
			repository = get()
		)
	}

	factory { (collection: DomainSongCollection) ->
		TrackListViewModel(
			partialCollection = collection,
			repository = get()
		)
	}

	factory { (songs: List<DomainSong>) ->
		PlaylistCreateDialogViewModel(
			tracks = songs,
			playlistDao = get()
		)
	}

	factory {
		ArtistListViewModel(
			repository = get()
		)
	}

	factory { (id: String) ->
		ArtistDetailViewModel(
			artistId = id,
			repository = get(),
			artistDao = get(),
			albumDao = get()
		)
	}

	factory {
		LoginViewModel(
			repository = get()
		)
	}

	factory {
		PlaylistListViewModel(
			repository = get()
		)
	}

	factory { (track: DomainSong?) ->
		LyricsScreenViewModel(
			track,
			repository = get()
		)
	}

	factory {
		SearchViewModel(
			repository = get()
		)
	}

	factory {
		GenreListViewModel(
			repository = get()
		)
	}
}