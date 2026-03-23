package paige.navic.data.database

import androidx.room.TypeConverter
import paige.navic.domain.models.DomainContributor
import paige.navic.domain.models.DomainReplayGain
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
}