package paige.navic.data.database.mappers

import paige.navic.data.database.entities.GenreEntity
import paige.navic.domain.models.DomainGenre
import dev.zt64.subsonic.api.model.Genre as ApiGenre

fun ApiGenre.toEntity() = GenreEntity(
	genreName = name,
	albumCount = albumCount,
	songCount = songCount
)

fun GenreEntity.toDomainModel() = DomainGenre(
	name = genreName,
	albumCount = albumCount,
	songCount = songCount
)

fun DomainGenre.toEntity() = GenreEntity(
	genreName = name,
	albumCount = albumCount,
	songCount = songCount
)
