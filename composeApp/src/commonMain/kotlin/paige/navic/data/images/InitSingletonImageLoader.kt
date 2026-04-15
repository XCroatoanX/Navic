package paige.navic.data.images

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade

fun initializeSingletonImageLoader(context: PlatformContext): ImageLoader {
	return ImageLoader.Builder(context)
		.components {
			add(KtorNetworkFetcherFactory())
		}
		.memoryCache {
			MemoryCache.Builder()
				.maxSizePercent(context, 0.25)
				.build()
		}
		.diskCache {
			DiskCache.Builder()
				.maxSizePercent(0.25)//maybe too much idk
				.build()
		}
		.crossfade(true)
		.build()
}
