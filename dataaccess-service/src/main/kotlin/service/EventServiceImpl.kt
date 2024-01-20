package service

import models.Event
import org.jetbrains.exposed.sql.*
import service.DatabaseFactory.dbQuery
import db.tables.Events
import db.tables.Logs
import mu.KotlinLogging
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

private val logger = KotlinLogging.logger {}

class EventServiceImpl : EventService {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmmss")

    override suspend fun getEvents(topicName: String, fromDate: String, fromTime: String, toDate: String, toTime: String): List<Event> = dbQuery {
        val from = LocalDateTime.parse("$fromDate $fromTime", formatter).toInstant(ZoneOffset.UTC).toEpochMilli()
        val to = LocalDateTime.parse("$toDate $toTime", formatter).toInstant(ZoneOffset.UTC).toEpochMilli()
        Events.select {
            (Events.topicName eq topicName) and (Events.timestamp greaterEq from) and (Events.timestamp lessEq to)
        }
            .orderBy(Events.timestamp to SortOrder.DESC)
            .mapNotNull { it.toEvent() }
    }

    override suspend fun addEvent(event: Event) {
        getEvent(event.id)?.let {
            logger.warn { "Duplicate event found. This will not be saved in the DB." }
        } ?: dbQuery {
            Events.insert {
                it[id] = event.id
                it[correlationId] = event.correlationId
                it[originatorId] = event.originatorId
                it[name] = event.name
                it[domain] = event.domain
                it[service] = event.service
                it[topicName] = event.topicName
                it[timestamp] = event.timestamp
            } get Events.id
        }
    }

    override suspend fun getEvent(id: String): Event? = dbQuery {
        Events.select { Events.id eq id }
            .mapNotNull { it.toEvent() }
            .singleOrNull()
    }

    private fun ResultRow.toEvent(): Event =
        Event(
            id = this[Events.id],
            correlationId = this[Events.correlationId],
            originatorId = this[Events.originatorId],
            name = this[Events.name],
            domain = this[Events.domain],
            service = this[Events.service],
            topicName = this[Events.topicName],
            timestamp = this[Events.timestamp]
        )
}
