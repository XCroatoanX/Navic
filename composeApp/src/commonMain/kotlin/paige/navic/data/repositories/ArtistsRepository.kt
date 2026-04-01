package paige.navic.data.repositories

import kotlinx.coroutines.flow.Flow
import paige.navic.data.database.DbContainer
import paige.navic.data.database.dao.ArtistDao
import paige.navic.data.database.entities.ArtistEntity
import paige.navic.data.database.mappers.toEntity
import paige.navic.data.session.SessionManager
import paige.navic.domain.models.DomainArtist
import kotlin.time.Clock

enum class ArtistListType {
	AlphabeticalByName, Random, Starred
}

open class ArtistsRepository(
	private val artistDao: ArtistDao = DbContainer.artistDao
) {

	fun getArtistsFlow(
		offset: Int,
		listType: ArtistListType
	): Flow<List<ArtistEntity>> {
		val totalToLoad = 30 + offset
		return when (listType) {
			ArtistListType.Random -> artistDao.getArtistsRandom(totalToLoad)
			ArtistListType.Starred -> artistDao.getArtistsStarred(totalToLoad)
			else -> artistDao.getArtistsAlphabeticalByName(totalToLoad)
		}
	}

	suspend fun syncArtists() {
		val remoteArtists = SessionManager.api.getArtists().index.flatMap { it.artists }
		artistDao.insertArtists(remoteArtists.map { it.toEntity() })
	}

	suspend fun isArtistStarred(artist: DomainArtist): Boolean {
		return artistDao.isArtistStarred(artist.id)
	}

	suspend fun starArtist(artist: DomainArtist) {
		val starredEntity = artist.toEntity().copy(
			starredAt = Clock.System.now(),
			isPendingSync = true
		)
		artistDao.insertArtist(starredEntity)

		try {
			SessionManager.api.star(artist.id)
			artistDao.insertArtist(starredEntity.copy(isPendingSync = false))
		} catch (e: Exception) {
			// scheduleSyncJob(artist.id, isStarring = true)
		}
	}

	suspend fun unstarArtist(artist: DomainArtist) {
		val unstarredEntity = artist.toEntity().copy(
			starredAt = null,
			isPendingSync = true
		)
		artistDao.insertArtist(unstarredEntity)

		try {
			SessionManager.api.unstar(artist.id)
			artistDao.insertArtist(unstarredEntity.copy(isPendingSync = false))
		} catch (e: Exception) {
			// scheduleSyncJob(artist.id, isStarring = false)
		}
	}
}