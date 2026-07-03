@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("UNCHECKED_CAST")

package paige.navic.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
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

class NowPlayingScene<T : Any>(
	override val key: Any,
	private val entry: NavEntry<T>,
	override val previousEntries: List<NavEntry<T>>,
	override val overlaidEntries: List<NavEntry<T>>,
	private val maxWidth: Dp,
	private val isTransparent: Boolean,
	private val onBack: () -> Unit
) : OverlayScene<T> {

	override val entries = listOf(entry)

	lateinit var sheetState: SheetState

	override val content = @Composable {
		sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
		val lifecycleOwner = rememberLifecycleOwner()
		val screenCornerRadius = rememberScreenCornerRadius()

		// TODO: see if there's a way to do this w out private api
		@Suppress("INVISIBLE_REFERENCE")
		val expandProgress = sheetState.anchoredDraggableState.progress(
			from = SheetValue.Hidden,
			to = SheetValue.Expanded
		)
		val shape = remember(expandProgress, screenCornerRadius) {
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

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || this::class != other::class) return false

		other as NowPlayingScene<*>

		return key == other.key
			&& entry == other.entry
			&& maxWidth == other.maxWidth
			&& isTransparent == other.isTransparent
	}

	override fun hashCode() = key.hashCode() * 31 +
		entry.hashCode() * 31 +
		maxWidth.hashCode() * 31 +
		isTransparent.hashCode() * 31

	// onRemove is intentionally not used, see the comment
	// on LocalSheetState
}

class NowPlayingSceneStrategy<T : Any> : SceneStrategy<T> {

	override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
		val entry = entries.lastOrNull() ?: return null
		val maxWidth = entry.metadata[MaxWidthKey] ?: return null
		val isTransparent = entry.metadata[IsTransparentKey] ?: return null

		return NowPlayingScene(
			key = entry.contentKey as T,
			entry = entry,
			previousEntries = entries.dropLast(1),
			overlaidEntries = entries.dropLast(1),
			maxWidth = maxWidth,
			isTransparent = isTransparent,
			onBack = onBack
		)
	}

	companion object {
		object MaxWidthKey : NavMetadataKey<Dp>
		object IsTransparentKey : NavMetadataKey<Boolean>

		fun bottomSheet(
			maxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
			isTransparent: Boolean = false
		) = metadata {
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
