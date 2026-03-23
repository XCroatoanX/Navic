package paige.navic.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class DomainContributor(
	val role: String,
	val subRole: String?,
	val artistId: String,
	val artistName: String
)