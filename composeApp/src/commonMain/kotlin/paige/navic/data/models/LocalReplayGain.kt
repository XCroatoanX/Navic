package paige.navic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LocalReplayGain(
	val albumGain: Float?,
	val albumPeak: Float?,
	val trackGain: Float?,
	val trackPeak: Float?,
	val baseGain: Float?,
	val fallbackGain: Float?
)