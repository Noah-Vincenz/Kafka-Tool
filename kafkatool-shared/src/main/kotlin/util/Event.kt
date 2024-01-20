package models

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

data class Event(
    val id: String,
    val correlationId: String,
    val originatorId: String,
    val name: String,
    val domain: String,
    val service: String,
    val timestamp: Long,
    val topicName: String
) {
    @JsonSerialize(using = ToStringSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    fun getTimestampAsLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC)
}
