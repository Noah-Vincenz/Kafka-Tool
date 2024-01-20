// import com.avro.Header
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.mustache.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.async
import main.kotlin.TopicService
import mu.KotlinLogging
import org.apache.avro.generic.GenericRecord
import producer.Producer
import util.MessageFixture
import util.SerializationUtil.mapper
import util.SerializationUtil.toString
import util.SerializationUtil.toGenericRecord
import viewmodels.ErrorViewModel
import viewmodels.EventViewModel
import viewmodels.LogViewModel
import viewmodels.SpecificRecordBaseViewModel
import java.io.File
import java.lang.Long.parseLong
import java.time.Instant
import java.util.UUID

private val logger = KotlinLogging.logger {}
private const val API_ENDPOINT = "http://localhost:9080/topics"

fun Routing.eventsUI(topicService: TopicService, producer: Producer<ByteArray, ByteArray>) {

    route("/topics/{topicName}") {
        get {
            val topicName = call.parameters["topicName"]!!
            val fromDate = call.parameters["fromDate"]
            val toDate = call.parameters["toDate"]
            val fromTime = call.parameters["fromTime"]
            val toTime = call.parameters["toTime"]
            topicService.getEvents(topicName, fromDate, fromTime, toDate, toTime).also {
                val eventViewModel = EventViewModel(it, topicName)
                call.respond(MustacheContent("content_events.hbs", mapOf("eventvm" to eventViewModel)))
            }
        }
        post {
            val topicName = call.parameters["topicName"]!!
            val fromDate = call.parameters["fromDate"]
            val toDate = call.parameters["toDate"]
            val fromTime = call.parameters["fromTime"]
            val toTime = call.parameters["toTime"]
            val params = call.receiveParameters()
            val eventName = params["eventName"]!!
            MessageFixture.buildMessage(topicName, eventName)?.let {
                val specificRecordBaseViewModel = SpecificRecordBaseViewModel(it.toString(eventName), topicName, fromDate, fromTime, toDate, toTime, eventName)
                call.respond(
                    MustacheContent(
                        "content_specificrecordbase.hbs",
                        mapOf("specificrecordbasevm" to specificRecordBaseViewModel)
                    )
                )
            } ?: logger.info { "Could not build a message with the given parameters" }
        }
        post("/send") {
            val topicName = call.parameters["topicName"]!!
            val params = call.receiveParameters()
            val record = params["specificrecordbase"]!!
            val fromDate = params["fromDate"]
            val toDate = params["toDate"]
            val fromTime = call.parameters["fromTime"]
            val toTime = call.parameters["toTime"]
            val eventName = params["eventname"]!!
            val genericRecord: GenericRecord
            try {
                genericRecord = record.toGenericRecord(eventName)
            } catch (e: Exception) {
                call.respond(MustacheContent(
                    "content_error.hbs",
                    mapOf("errorvm" to ErrorViewModel("Could not convert input to event of type '$eventName'"))
                ))
                return@post
            }
            val header = mapper.readValue(genericRecord.get("header").toString(), Header::class.java)
            val id = header.get("id") as String
            val correlationId = header.get("correlationId") as String
            val originatorId = header.get("originatorId") as String
            val eventNameFromInput = header.get("name") as String
            if (eventName != eventNameFromInput) {
                call.respond(MustacheContent(
                    "content_error.hbs",
                    mapOf("errorvm" to ErrorViewModel("Event name in header '$eventNameFromInput' does not match expected type '$eventName'"))
                ))
                return@post
            }
            try {
                UUID.fromString(id)
            } catch (e: Exception) {
                call.respond(MustacheContent(
                    "content_error.hbs",
                    mapOf("errorvm" to ErrorViewModel("Event id in header '$id' is not of type UUID"))
                ))
                return@post
            }
            try {
                UUID.fromString(correlationId)
            } catch (e: Exception) {
                call.respond(MustacheContent(
                    "content_error.hbs",
                    mapOf("errorvm" to ErrorViewModel("Event correlationId in header '$correlationId' is not of type UUID"))
                ))
                return@post
            }
            async {
                try {
                    producer.produce(genericRecord, topicName, id, correlationId, originatorId, eventName)
                } catch (e: Exception) { // other errors such as TimeoutException will be caught to display them in the UI
                    logger.error { "Failed to send Kafka message." }
                    call.respond(MustacheContent("content_error.hbs", mapOf("errorvm" to ErrorViewModel(e.message!!))))
                    return@async
                }
            }
            // any avro parse errors are automatically displayed in the UI
            val redirectUrl =
                if (fromDate == null && toDate == null) "$API_ENDPOINT/$topicName"
                else if (fromTime == null && toTime == null) "$API_ENDPOINT/$topicName?fromDate=$fromDate&fromTime=000000&toDate=$toDate&toTime=235959"
                else "$API_ENDPOINT/$topicName?fromDate=$fromDate&fromTime=$fromTime&toDate=$toDate&toTime=$toTime"
            // redirect to main events page
            call.respondRedirect(redirectUrl)
        }
        route("/logs") {
            get {
                val topicName = call.parameters["topicName"]!!
                val fromDate = call.parameters["fromDate"]
                val toDate = call.parameters["toDate"]
                val fromTime = call.parameters["fromTime"]
                val toTime = call.parameters["toTime"]
                val filterParam = call.parameters["filter"]
                if (fromDate == null && toDate == null && filterParam == null) {
                    val logViewModel = LogViewModel("", topicName)
                    call.respond(MustacheContent("content_logs.hbs", mapOf("logvm" to logViewModel)))
                    return@get
                } else {
                    val logViewModel = LogViewModel(topicService.getLogs(topicName, fromDate, fromTime, toDate, toTime, filterParam), topicName)
                    call.respond(MustacheContent("content_logs.hbs", mapOf("logvm" to logViewModel)))
                }
            }
            get("/download") {
                val topicName = call.parameters["topicName"]
                val fromDate = call.parameters["fromDate"]
                val toDate = call.parameters["toDate"]
                val fromTime = call.parameters["fromTime"]
                val toTime = call.parameters["toTime"]
                val filterParam = call.parameters["filter"]
                topicService.getLogs(topicName!!, fromDate, fromTime, toDate, toTime, filterParam).also {
                    val fileName = "logs.txt"
                    with(File.createTempFile(fileName, "")) {
                        writeText(it)
                        call.response.header("Content-Disposition", "attachment; filename=$fileName")
                        call.respondFile(this)
                        delete()
                    }
                }
            }
        }
    }
}
