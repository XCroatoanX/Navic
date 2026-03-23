package paige.navic.data.database.mappers

import dev.zt64.subsonic.api.model.Song as ApiSong
import paige.navic.data.database.entities.SongEntity
import paige.navic.data.models.LocalContributor
import paige.navic.data.models.LocalReplayGain
import kotlin.time.Clock

fun ApiSong.toEntity(playlistId: String = "__library__"): SongEntity {
	return SongEntity(
		id = "${this.id}_$playlistId",
		navidromeId = this.id,
		playlistId = playlistId,
		title = this.title,
		artistName = this.artistName,
		artistId = this.artistId,
		album = this.albumTitle,
		albumId = this.albumId,
		coverArtId = this.coverArtId,
		duration = this.duration,
		trackNumber = this.trackNumber,
		discNumber = this.discNumber,
		year = this.year,
		genre = this.genre,
		bitRate = this.bitRate,
		mimeType = this.mimeType,
		suffix = this.fileExtension,
		path = this.filePath,
		starredAt = this.starredAt,
		dateCached = Clock.System.now().toEpochMilliseconds(),
		parentId = this.parentId,
		genres = this.genres,
		moods = this.moods,
		isrc = this.isrc,
		bpm = this.bpm,
		comment = this.comment,
		contributors = this.contributors.map {
			LocalContributor(
				role = it.role,
				subRole = it.subRole,
				artistId = it.artist.id,
				artistName = it.artist.name
			)
		},
		playCount = this.playCount,
		userRating = this.userRating,
		averageRating = this.averageRating,
		bitDepth = this.bitDepth,
		sampleRate = this.sampleRate,
		audioChannelCount = this.audioChannelCount,
		fileSize = this.fileSize,
		replayGain = this.replayGain?.let {
			LocalReplayGain(
				albumGain = it.albumGain,
				albumPeak = it.albumPeak,
				trackGain = it.trackGain,
				trackPeak = it.trackPeak,
				baseGain = it.baseGain,
				fallbackGain = it.fallbackGain
			)
		},
		explicitStatus = this.explicitStatus
	)
}
