package paige.navic.data.database

import androidx.room.TypeConverter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

class Converters {
	@TypeConverter
	fun fromDuration(duration: Duration?): Long? {
		return duration?.inWholeMilliseconds
	}

	@TypeConverter
	fun toDuration(millis: Long?): Duration? {
		return millis?.milliseconds
	}

	@TypeConverter
	fun fromInstant(instant: Instant?): Long? {
		return instant?.toEpochMilliseconds()
	}

	@TypeConverter
	fun toInstant(millis: Long?): Instant? {
		return millis?.let { Instant.fromEpochMilliseconds(it) }
	}
}