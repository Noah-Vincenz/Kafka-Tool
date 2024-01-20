package main.kotlin

import models.Event

interface TopicService {
    suspend fun getEvents(topicName: String, fromDate: String?, fromTime: String?, toDate: String?, toTime: String?): List<Event>
    suspend fun getLogs(topicName: String, fromDate: String?, fromTime: String?, toDate: String?, toTime: String?, filterParam: String?): String
    fun setApiEndpoint(apiEndpointUrl: String)
}