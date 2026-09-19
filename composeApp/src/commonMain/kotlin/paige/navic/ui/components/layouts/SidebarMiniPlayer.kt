package paige.navic.ui.components.layouts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.action_next_song
import navic.composeapp.generated.resources.action_pause
import navic.composeapp.generated.resources.action_play
import navic.composeapp.generated.resources.action_previous_song
import navic.composeapp.generated.resources.info_not_playing
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import paige.navic.di.LocalNavStack
import paige.navic.domain.manager.PreferenceManager
import paige.navic.domain.manager.SessionManager
import paige.navic.domain.models.settings.MiniPlayerProgressStyle
import paige.navic.icons.Icons
import paige.navic.icons.filled.Note
import paige.navic.icons.filled.Pause
import paige.navic.icons.filled.Play
import paige.navic.icons.filled.SkipNext
import paige.navic.icons.filled.SkipPrevious
import paige.navic.icons.outlined.Radio
import paige.navic.shared.MediaPlayerViewModel
import paige.navic.ui.components.common.MarqueeText
import paige.navic.ui.navigation.Screen
import paige.navic.ui.theme.NavicTheme
import paige.navic.ui.util.playPauseIconPainter
import paige.navic.ui.util.rememberColorSchemeForCurrentSong
import paige.navic.util.toHoursMinutesSeconds
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import coil3.compose.LocalPlatformContext as LocalCoilPlatformContext

@Composable
fun SidebarMiniPlayer(
	modifier: Modifier = Modifier
) {
	val player = koinInject<MediaPlayerViewModel>()
	val preferenceManager = koinInject<PreferenceManager>()
	val playerState by player.uiState.collectAsState()
	val song = playerState.currentSong

	val hasSong = song != null
	val isRadio = song?.id?.startsWith("radio_") == true
	val haptics = LocalHapticFeedback.current

	val coilPlatformContext = LocalCoilPlatformContext.current
	val imageLoader = koinInject<ImageLoader>()
	val sessionManager = koinInject<SessionManager>()

	val model = remember(song?.coverArtId) {
		ImageRequest.Builder(coilPlatformContext)
			.data(song?.coverArtId?.let { sessionManager.getCoverArtUrl(it) })
			.memoryCacheKey(song?.coverArtId)
			.diskCacheKey(song?.coverArtId)
			.diskCachePolicy(CachePolicy.ENABLED)
			.memoryCachePolicy(CachePolicy.ENABLED)
			.build()
	}

	val backStack = LocalNavStack.current
	val onClick = dropUnlessResumed {
		if (!backStack.contains(Screen.NowPlaying)) {
			backStack.add(Screen.NowPlaying)
		}
	}

	AnimatedVisibility(
		visible = hasSong || !preferenceManager.hideIfIdle,
		modifier = modifier
	) {
		val dynamicColorScheme = if (preferenceManager.dynamicTheming) {
			rememberColorSchemeForCurrentSong(forceDark = false)
		} else {
			null
		}


		NavicTheme(colorScheme = dynamicColorScheme) {
			Surface(
				modifier = Modifier
					.padding(horizontal = 8.dp, vertical = 12.dp)
					.fillMaxWidth(),
				shape = RoundedCornerShape(28.dp),
				color = MaterialTheme.colorScheme.surfaceContainerHigh,
				contentColor = MaterialTheme.colorScheme.onSurface,
				tonalElevation = 2.dp,
				onClick = onClick
			) {
				Column(
					modifier = Modifier.padding(14.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Box(
						modifier = Modifier
							.aspectRatio(1f)
							.fillMaxWidth()
							.clip(RoundedCornerShape(22.dp))
							.background(MaterialTheme.colorScheme.surfaceContainerLowest),
						contentAlignment = Alignment.Center
					) {
						AsyncImage(
							model = model,
							imageLoader = imageLoader,
							contentDescription = null,
							contentScale = ContentScale.Crop,
							modifier = Modifier.matchParentSize()
						)

						if (song?.coverArtId.isNullOrEmpty()) {
							Icon(
								imageVector = if (isRadio) Icons.Outlined.Radio else Icons.Filled.Note,
								contentDescription = null,
								tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
								modifier = Modifier.size(52.dp)
							)
						}
					}

					Spacer(Modifier.height(14.dp))

					Column(Modifier.fillMaxWidth()) {
						MarqueeText(
							text = song?.title ?: stringResource(Res.string.info_not_playing),
							style = MaterialTheme.typography.titleMedium.copy(
								color = MaterialTheme.colorScheme.onSurface,
								fontWeight = FontWeight.Medium
							),
							modifier = Modifier.height(24.dp)
						)
						MarqueeText(
							text = song?.artistName ?: "",
							style = MaterialTheme.typography.bodySmall.copy(
								color = MaterialTheme.colorScheme.onSurfaceVariant,
								fontWeight = FontWeight.Medium
							),
							modifier = Modifier.height(18.dp)
						)
					}

					Spacer(Modifier.height(10.dp))

					Box(
						modifier = Modifier
							.fillMaxWidth()
							.height(42.dp),
						contentAlignment = Alignment.TopCenter
					) {
						if (preferenceManager.miniPlayerProgressStyle == MiniPlayerProgressStyle.Visible ||
							preferenceManager.miniPlayerProgressStyle == MiniPlayerProgressStyle.Seekable
						) {
							var dragging by remember { mutableStateOf(false) }
							var isHolding by remember { mutableStateOf(false) }

							val animatedProgress by animateFloatAsState(
								targetValue = if (hasSong) playerState.progress.coerceIn(0f, 1f) else 0f,
								animationSpec = spring(
									dampingRatio = Spring.DampingRatioLowBouncy,
									stiffness = Spring.StiffnessMediumLow
								),
								label = "expressive_progress"
							)

							val progressTrackHeight by animateDpAsState(
								targetValue = if (dragging || isHolding) 12.dp else 4.dp,
								animationSpec = spring(
									dampingRatio = Spring.DampingRatioMediumBouncy,
									stiffness = Spring.StiffnessLow
								),
								label = "expressive_track_height"
							)

							val duration = song?.duration

							Column(modifier = Modifier.fillMaxWidth()) {
								Box(
									modifier = Modifier.fillMaxWidth(),
									contentAlignment = Alignment.Center
								) {
									LinearProgressIndicator(
										progress = { animatedProgress },
										modifier = Modifier
											.fillMaxWidth()
											.height(progressTrackHeight)
											.clip(RoundedCornerShape(50)),
										strokeCap = StrokeCap.Round,
										color = MaterialTheme.colorScheme.primary,
										trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
									)

									if (hasSong && preferenceManager.miniPlayerProgressStyle == MiniPlayerProgressStyle.Seekable) {
										Box(
											modifier = Modifier
												.fillMaxWidth()
												.height(28.dp)
												.pointerInput(Unit) {
													detectTapGestures(
														onPress = { offset ->
															isHolding = true
															val pressTime = Clock.System.now().toEpochMilliseconds()
															val released = tryAwaitRelease()

															val pressDuration = Clock.System.now().toEpochMilliseconds() - pressTime
															val seekRatio = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)

															if (released && pressDuration < 500L) {
																player.seek(seekRatio)
																haptics.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
															}
															isHolding = false
														},
														onLongPress = { offset ->
															isHolding = true
															val seekRatio = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
															player.seek(seekRatio)
															haptics.performHapticFeedback(HapticFeedbackType.LongPress)
														},
														onTap = { offset ->
															val seekRatio = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
															player.seek(seekRatio)
															haptics.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
														}
													)
												}
												.pointerInput(Unit) {
													detectDragGestures(
														onDragStart = { offset ->
															dragging = true
															val seekRatio = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
															player.seek(seekRatio)
															haptics.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
														},
														onDragEnd = {
															dragging = false
															isHolding = false
															haptics.performHapticFeedback(HapticFeedbackType.GestureEnd)
														},
														onDragCancel = {
															dragging = false
															isHolding = false
														}
													) { change, _ ->
														val seekRatio = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
														player.seek(seekRatio)
														change.consume()
													}
												}
										)
									}
								}

								Column(
									modifier = Modifier
										.fillMaxWidth()
										.height(18.dp)
										.padding(top = 2.dp)
								) {
									AnimatedVisibility(
										visible = (isHolding || dragging) && hasSong,
										enter = fadeIn(),
										exit = fadeOut()
									) {
										Row(
											modifier = Modifier.fillMaxWidth(),
											horizontalArrangement = Arrangement.SpaceBetween,
											verticalAlignment = Alignment.CenterVertically
										) {
											when {
												duration == kotlin.time.Duration.ZERO -> {
													Text(
														text = "LIVE",
														style = MaterialTheme.typography.labelSmall,
														color = MaterialTheme.colorScheme.primary,
														fontWeight = FontWeight.Bold
													)
													Text(
														text = "∞",
														style = MaterialTheme.typography.labelSmall,
														color = MaterialTheme.colorScheme.onSurfaceVariant
													)
												}

												duration != null -> {
													val elapsed = ((duration.inWholeSeconds * playerState.progress).toDouble().seconds).toHoursMinutesSeconds()
													Text(
														text = elapsed,
														style = MaterialTheme.typography.labelSmall,
														color = MaterialTheme.colorScheme.primary,
														fontWeight = FontWeight.Bold
													)
													Text(
														text = duration.toHoursMinutesSeconds(),
														style = MaterialTheme.typography.labelSmall,
														color = MaterialTheme.colorScheme.onSurfaceVariant
													)
												}

												else -> {
													Text(
														text = "--:--",
														style = MaterialTheme.typography.labelSmall,
														color = MaterialTheme.colorScheme.primary,
														fontWeight = FontWeight.Bold
													)
													Text(
														text = "--:--",
														style = MaterialTheme.typography.labelSmall,
														color = MaterialTheme.colorScheme.onSurfaceVariant
													)
												}
											}
										}
									}
								}
							}
						}
					}

					Spacer(Modifier.height(6.dp))

					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceEvenly,
						verticalAlignment = Alignment.CenterVertically
					) {
						IconButton(
							onClick = { player.previous() },
							enabled = hasSong,
							colors = IconButtonDefaults.iconButtonColors(
								contentColor = MaterialTheme.colorScheme.onSurface
							)
						) {
							Icon(
								imageVector = Icons.Filled.SkipPrevious,
								contentDescription = stringResource(Res.string.action_previous_song),
								modifier = Modifier.size(26.dp)
							)
						}

						val playScale by animateFloatAsState(
							targetValue = if (playerState.isPaused) 0.95f else 1.05f,
							animationSpec = spring(
								dampingRatio = Spring.DampingRatioMediumBouncy,
								stiffness = Spring.StiffnessMedium
							),
							label = "expressive_play_scale"
						)

						IconButton(
							onClick = {
								if (playerState.isPaused) player.resume() else player.pause()
							},
							enabled = hasSong,
							modifier = Modifier
								.size(52.dp)
								.graphicsLayer {
									scaleX = playScale
									scaleY = playScale
								},
							colors = IconButtonDefaults.filledIconButtonColors(
								containerColor = MaterialTheme.colorScheme.primary,
								contentColor = MaterialTheme.colorScheme.onPrimary
							)
						) {
							val painter = playPauseIconPainter(playerState.isPaused)
							val description = stringResource(
								if (playerState.isPaused) Res.string.action_play else Res.string.action_pause
							)
							if (painter != null) {
								Icon(painter, description, modifier = Modifier.size(28.dp))
							} else {
								Icon(
									imageVector = if (playerState.isPaused) Icons.Filled.Play else Icons.Filled.Pause,
									contentDescription = description,
									modifier = Modifier.size(28.dp)
								)
							}
						}

						IconButton(
							onClick = { player.next() },
							enabled = hasSong,
							colors = IconButtonDefaults.iconButtonColors(
								contentColor = MaterialTheme.colorScheme.onSurface
							)
						) {
							Icon(
								imageVector = Icons.Filled.SkipNext,
								contentDescription = stringResource(Res.string.action_next_song),
								modifier = Modifier.size(26.dp)
							)
						}
					}
				}
			}
		}
	}
}
