package service

import models.Log
import org.jetbrains.exposed.sql.*
import service.DatabaseFactory.dbQuery
import db.tables.Logs
import mu.KotlinLogging
import java.time.Instant.ofEpochMilli
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.TimeZone.getDefault

private val logger = KotlinLogging.logger {}

class LogServiceImpl : LogService {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmmss")

    override suspend fun getLogs(topicName: String, fromDate: String, fromTime: String, toDate: String, toTime: String): List<Log> = dbQuery {
        val from = LocalDateTime.parse("$fromDate $fromTime", formatter).toInstant(ZoneOffset.UTC).toEpochMilli()
        val to = LocalDateTime.parse("$toDate $toTime", formatter).toInstant(ZoneOffset.UTC).toEpochMilli()
        Logs.select {
            (Logs.topicName eq topicName) and (Logs.timestamp greaterEq from) and (Logs.timestamp lessEq to)
        }
            .orderBy(Logs.timestamp to SortOrder.DESC)
            .mapNotNull { it.toLog() }
    }

    override suspend fun addLog(log: Log) {
        getLog(log.eventId)?.let {
            logger.warn { "Duplicate event found. This will not be saved in the DB." }
        } ?: dbQuery {
            Logs.insert {
                it[eventId] = log.eventId
                it[topicName] = log.topicName
                it[timestamp] = log.timestamp
                it[content] = log.content
            } get Logs.eventId
        }
    }

    override suspend fun getLog(id: String): Log? = dbQuery {
        Logs.select { Logs.eventId eq id }
            .mapNotNull { it.toLog() }
            .singleOrNull()
    }

    private fun ResultRow.toLog(): Log =
        Log(
            eventId = this[Logs.eventId],
            topicName = this[Logs.topicName],
            timestamp = this[Logs.timestamp],
            content = this[Logs.content],
        )
}
