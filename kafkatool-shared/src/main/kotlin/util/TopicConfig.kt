package models

data class TopicConfig(
    val name: String,
    val key: String,
    val eventNames: List<String>
)
