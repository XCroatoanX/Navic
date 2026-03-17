package paige.navic.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import paige.navic.data.session.SessionManager

@Composable
fun rememberTrackPainter(coverArt: String?): Painter {
	val context = LocalPlatformContext.current

	val imageRequest = remember(coverArt) {
		ImageRequest.Builder(context)
			.data(coverArt?.let { SessionManager.api.getCoverArtUrl(it, auth = true) })
			.crossfade(500)
			.memoryCacheKey(coverArt)
			.diskCacheKey(coverArt)
			.diskCachePolicy(CachePolicy.ENABLED)
			.memoryCachePolicy(CachePolicy.ENABLED)
			.build()
	}

	return rememberAsyncImagePainter(imageRequest)
}