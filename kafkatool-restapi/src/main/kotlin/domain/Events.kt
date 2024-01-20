package db.tables

import org.jetbrains.exposed.sql.Table

object Events : Table() {
    val id = varchar("id", 36)
    val correlationId = varchar("correlationId", 36)
    val originatorId = varchar("originatorId", 36)
    val name = varchar("name", 36)
    val domain = varchar("domain", 36)
    val service = varchar("service", 36)
    val topicName = varchar("topicName", 100)
    val timestamp = long("timestamp") // TODO: change to long
    override val primaryKey = PrimaryKey(id)
}