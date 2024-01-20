package models

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

data class Log(
    val eventId: String,
    val topicName: String,
    val timestamp: Long,
    val content: String
) {
//    @JsonSerialize(using = ToStringSerializer::class)
//    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
//    fun getTimestampAsLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp.toLong()), TimeZone.getDefault().toZoneId())
}