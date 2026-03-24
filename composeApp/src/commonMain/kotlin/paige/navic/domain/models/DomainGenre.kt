package paige.navic.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class DomainGenre(
	val name: String,
	val albumCount: Int,
	val songCount: Int
)
