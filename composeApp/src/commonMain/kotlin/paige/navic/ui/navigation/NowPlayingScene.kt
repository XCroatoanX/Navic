package paige.navic.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.rememberLifecycleOwner
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import coil3.SingletonImageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.kmpalette.rememberDominantColorState
import com.kyant.capsule.ContinuousRoundedRectangle
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import org.koin.compose.koinInject
import paige.navic.domain.manager.SessionManager
import paige.navic.shared.MediaPlayerViewModel
import paige.navic.ui.components.sheets.ModalBottomSheet
import paige.navic.ui.theme.NavicTheme
import paige.navic.util.ui.LocalSheetState
import paige.navic.util.ui.rememberScreenCornerRadius
import paige.navic.util.ui.toImageBitmap
import coil3.compose.LocalPlatformContext as LocalCoilPlatformContext

@OptIn(ExperimentalMaterial3Api::class)
class NowPlayingSceneStrategy<T : Any> : SceneStrategy<T> {

	override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
		val entry = entries.lastOrNull() ?: return null
		val properties = entry.metadata[MetadataKey] ?: return null
		val maxWidth = entry.metadata[MaxWidthKey] ?: return null
		val isTransparent = entry.metadata[IsTransparentKey] ?: return null

		return object : OverlayScene<T> {
			@Suppress("UNCHECKED_CAST")
			override val key = entry.contentKey as T
			override val entries = listOf(entry)
			override val previousEntries = entries.dropLast(1)
			override val overlaidEntries = entries.dropLast(1)

			override val content = @Composable {
				val lifecycleOwner = rememberLifecycleOwner()
				val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
				val screenCornerRadius = rememberScreenCornerRadius()
				@Suppress("INVISIBLE_REFERENCE")
				val expandProgress = sheetState.anchoredDraggableState.progress(
					from = SheetValue.Hidden,
					to = SheetValue.Expanded
				)
				val shape = remember(
					expandProgress,
					screenCornerRadius
				) {
					if (expandProgress == 1f) {
						RectangleShape
					} else {
						ContinuousRoundedRectangle(
							topStart = screenCornerRadius,
							topEnd = screenCornerRadius
						)
					}
				}
				NavicTheme(colorSchemeForCurrentSong()) {
					ModalBottomSheet(
						containerColor = if (isTransparent) {
							Color.Transparent
						} else {
							MaterialTheme.colorScheme.surface
						},
						onDismissRequest = onBack,
						properties = properties,
						sheetState = sheetState,
						sheetMaxWidth = maxWidth,
						contentWindowInsets = { WindowInsets() },
						dragHandle = null,
						shape = shape
					) {
						CompositionLocalProvider(
							LocalLifecycleOwner provides lifecycleOwner,
							LocalSheetState provides sheetState
						) {
							Box(Modifier.fillMaxSize()) {
								entry.Content()
							}
						}
					}
				}
			}

			// could use onRemove for sheet animations but that will
			// make sheets close if you layer them on top of each other
		}
	}

	companion object {
		object MetadataKey : NavMetadataKey<ModalBottomSheetProperties>
		object MaxWidthKey : NavMetadataKey<Dp>
		object IsTransparentKey : NavMetadataKey<Boolean>

		fun bottomSheet(
			sheetProperties: ModalBottomSheetProperties = ModalBottomSheetProperties(),
			maxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
			isTransparent: Boolean = false
		) = metadata {
			put(MetadataKey, sheetProperties)
			put(MaxWidthKey, maxWidth)
			put(IsTransparentKey, isTransparent)
		}
	}
}

@Composable
private fun colorSchemeForCurrentSong(): ColorScheme {
	val player = koinInject<MediaPlayerViewModel>()
	val sessionManager = koinInject<SessionManager>()

	val playerState by player.uiState.collectAsState()
	val song = playerState.currentSong
	val coverId = song?.coverArtId
	val coverUri = remember(coverId) {
		coverId?.let { sessionManager.getCoverArtUrl(it) }
	}

	val coilPlatformContext = LocalCoilPlatformContext.current
	val loader = SingletonImageLoader.get(coilPlatformContext)
	val model = remember(coverUri) {
		ImageRequest.Builder(coilPlatformContext)
			.data(coverUri)
			.memoryCacheKey(coverId)
			.diskCacheKey(coverId)
			.diskCachePolicy(CachePolicy.ENABLED)
			.memoryCachePolicy(CachePolicy.ENABLED)
			.build()
	}
	val dominantColorState = rememberDominantColorState()

	LaunchedEffect(model) {
		val result = loader.execute(model)
		result.image?.toImageBitmap()?.let { imageBitmap ->
			dominantColorState.updateFrom(imageBitmap)
		}
	}

	val scheme = rememberDynamicColorScheme(
		seedColor = dominantColorState.color,
		isDark = true,
		style = if (coverUri != null) PaletteStyle.Content else PaletteStyle.Monochrome,
		specVersion = ColorSpec.SpecVersion.SPEC_2021,
	)

	return scheme
}
