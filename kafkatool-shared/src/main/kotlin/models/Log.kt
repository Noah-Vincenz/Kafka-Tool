package models

data class Log(
    val eventId: String,
    val topicName: String,
    val timestamp: Long,
    val content: String
)