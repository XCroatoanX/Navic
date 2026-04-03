package paige.navic.managers

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

expect class ShareManager {
	suspend fun shareImage(bitmap: ImageBitmap, fileName: String)
	suspend fun shareString(string: String)
}

@Composable
expect fun rememberShareManager(): ShareManager
