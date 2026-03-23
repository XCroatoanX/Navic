package paige.navic.data.database.relations

import androidx.room.Embedded
import paige.navic.data.database.entities.SongEntity
import paige.navic.domain.models.DomainContributor
import paige.navic.domain.models.DomainExplicitStatus
import paige.navic.domain.models.DomainReplayGain

data class SongWithExtras(
	@Embedded val song: SongEntity,
	// TODO
	val contributors: List<DomainContributor> = emptyList(),
	val replayGain: DomainReplayGain? = null,
	val explicitStatus: DomainExplicitStatus? = null
)
