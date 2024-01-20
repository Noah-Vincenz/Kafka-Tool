package db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime

object Logs : Table() {
    val eventId = varchar("eventId", 36)
    val topicName = varchar("topicName", 100)
    val timestamp = long("timestamp") // TODO: change to long
    val content = varchar("content", 10000) // TODO: is this acceptable?
    override val primaryKey = PrimaryKey(eventId)
}