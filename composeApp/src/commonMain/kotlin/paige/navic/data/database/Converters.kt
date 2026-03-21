package paige.navic.data.database

import androidx.room.TypeConverter
import paige.navic.data.models.LocalContributor
import paige.navic.data.models.LocalReplayGain
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

class Converters {
	// Duration
	@TypeConverter
	fun fromDuration(duration: Duration?): Long? {
		return duration?.inWholeMilliseconds
	}

	@TypeConverter
	fun toDuration(millis: Long?): Duration? {
		return millis?.milliseconds
	}

	// Instant
	@TypeConverter
	fun fromInstant(instant: Instant?): Long? {
		return instant?.toEpochMilliseconds()
	}

	@TypeConverter
	fun toInstant(millis: Long?): Instant? {
		return millis?.let { Instant.fromEpochMilliseconds(it) }
	}

	// List<String>
	@TypeConverter
	fun fromStringList(list: List<String>?): String? {
		return list?.joinToString(separator = "||")
	}

	@TypeConverter
	fun toStringList(data: String?): List<String>? {
		return data?.split("||")?.filter { it.isNotEmpty() }
	}

	// List<Contributor>
	@TypeConverter
	fun fromContributorList(list: List<LocalContributor>?): String? {
		return list?.joinToString(separator = ";") { c ->
			"${c.role}^${c.subRole ?: ""}^${c.artistId}^${c.artistName}"
		}
	}

	@TypeConverter
	fun toContributorList(data: String?): List<LocalContributor>? {
		if (data == null) return null
		return data.split(";").filter { it.isNotEmpty() }.map { item ->
			val parts = item.split("^")
			LocalContributor(
				role = parts[0],
				subRole = parts[1].ifEmpty { null },
				artistId = parts[2],
				artistName = parts[3]
			)
		}
	}

	// ReplayGain
	@TypeConverter
	fun fromReplayGain(rg: LocalReplayGain?): String? {
		if (rg == null) return null
		return "${rg.albumGain ?: ""},${rg.albumPeak ?: ""},${rg.trackGain ?: ""},${rg.trackPeak ?: ""},${rg.baseGain ?: ""},${rg.fallbackGain ?: ""}"
	}

	@TypeConverter
	fun toReplayGain(data: String?): LocalReplayGain? {
		if (data.isNullOrEmpty()) return null
		val parts = data.split(",")
		if (parts.size < 6) return null

		return LocalReplayGain(
			albumGain = parts[0].toFloatOrNull(),
			albumPeak = parts[1].toFloatOrNull(),
			trackGain = parts[2].toFloatOrNull(),
			trackPeak = parts[3].toFloatOrNull(),
			baseGain = parts[4].toFloatOrNull(),
			fallbackGain = parts[5].toFloatOrNull()
		)
	}
}