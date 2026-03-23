package paige.navic.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
enum class DomainExplicitStatus {
	@SerialName("explicit")
	EXPLICIT,

	@SerialName("clean")
	CLEAN,

	@SerialName("")
	UNKNOWN
}