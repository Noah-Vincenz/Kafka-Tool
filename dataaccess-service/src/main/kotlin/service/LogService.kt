package service

import models.Log

interface LogService {
    suspend fun getLogs(topicName: String, fromDate: String, fromTime: String, toDate: String, toTime: String): List<Log>
    suspend fun addLog(log: Log)
    suspend fun getLog(id: String): Log?
}