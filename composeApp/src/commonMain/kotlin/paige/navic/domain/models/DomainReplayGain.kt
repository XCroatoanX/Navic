package paige.navic.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class DomainReplayGain(
	val albumGain: Float?,
	val albumPeak: Float?,
	val trackGain: Float?,
	val trackPeak: Float?,
	val baseGain: Float?,
	val fallbackGain: Float?
)