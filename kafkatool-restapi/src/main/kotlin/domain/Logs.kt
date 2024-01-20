package db.tables

import org.jetbrains.exposed.sql.Table

object Logs : Table() {
    val eventId = varchar("eventId", 36)
    val topicName = varchar("topicName", 100)
    val timestamp = long("timestamp")
    val content = varchar("content", 10000)
    override val primaryKey = PrimaryKey(eventId)
}