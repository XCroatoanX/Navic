package paige.navic.utils

import paige.navic.data.models.LocalReplayGain
import kotlin.math.pow

fun LocalReplayGain.effectiveGain(): Float {
	val gain = trackGain ?: albumGain ?: fallbackGain ?: baseGain ?: 0f
	return (10.0.pow((gain / 20.0)).toFloat()).coerceIn(0f..1f)
}