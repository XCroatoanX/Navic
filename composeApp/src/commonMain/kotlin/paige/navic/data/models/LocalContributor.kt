package paige.navic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LocalContributor(
	val role: String,
	val subRole: String?,
	val artistId: String,
	val artistName: String
)