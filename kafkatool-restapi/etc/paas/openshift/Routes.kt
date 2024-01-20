import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import models.Event
import models.Log
import service.EventService
import service.LogService
import util.topicConfigs

fun Routing.restApi(eventService: EventService, logService: LogService) {
    route("/topics/{topicName}") {
        println("GET REQUEST HAS ARRIVED IN REST API")
        get {
            val topicName = call.parameters["topicName"]
            if (topicName == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            if (topicName !in topicConfigs.map { it.name }) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            val fromDate = call.parameters["fromDate"]
            val toDate = call.parameters["toDate"]
            val fromTime = call.parameters["fromTime"]
            val toTime = call.parameters["toTime"]
            if (fromDate == null && toDate == null) call.respond(status = HttpStatusCode.OK, message = emptyList<Event>())
            else call.respond(status = HttpStatusCode.OK, message = eventService.getEvents(topicName, fromDate!!, fromTime!!, toDate!!, toTime!!))
        }
        post {
            val topicName = call.parameters["topicName"]
            if (topicName == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            if (topicName !in topicConfigs.map { it.name }) {
                call.respond(HttpStatusCode.NotFound)
                return@post
            }
            val event = call.receive<Event>()
            eventService.addEvent(event)
            call.respond(HttpStatusCode.OK)
        }
        route("/logs") {
            get {
                val topicName = call.parameters["topicName"]
                if (topicName == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                if (topicName !in topicConfigs.map { it.name }) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                val fromDate = call.parameters["fromDate"]
                val toDate = call.parameters["toDate"]
                val fromTime = call.parameters["fromTime"]
                val toTime = call.parameters["toTime"]
                if (fromDate == null && toDate == null) call.respond(status = HttpStatusCode.OK, message = emptyList<Log>())
                else call.respond(status = HttpStatusCode.OK, message = logService.getLogs(topicName, fromDate!!, fromTime!!, toDate!!, toTime!!))
            }
            post {
                val topicName = call.parameters["topicName"]
                if (topicName == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if (topicName !in topicConfigs.map { it.name }) {
                    call.respond(HttpStatusCode.NotFound)
                    return@post
                }
                val log = call.receive<Log>()
                logService.addLog(log)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}