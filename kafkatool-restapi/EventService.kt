package service

import models.Event

interface EventService {
    suspend fun getEvents(topicName: String, fromDate: String, fromTime: String, toDate: String, toTime: String): List<Event>
    suspend fun addEvent(event: Event)
    suspend fun getEvent(id: String): Event?
}