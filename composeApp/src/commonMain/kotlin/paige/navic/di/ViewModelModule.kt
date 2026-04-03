package paige.navic.di

import dev.zt64.subsonic.api.model.AlbumListType
import org.koin.dsl.module
import paige.navic.domain.models.DomainSongCollection
import paige.navic.ui.screens.album.viewmodels.AlbumListViewModel
import paige.navic.ui.screens.track.viewmodels.TrackListViewModel

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

}