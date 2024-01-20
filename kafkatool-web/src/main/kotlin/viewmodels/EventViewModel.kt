package viewmodels

import models.Event

data class EventViewModel(private val eventsIn: List<Event>?, private val topicNameIn: String) {
    val events = eventsIn
    val topicName = topicNameIn
}





