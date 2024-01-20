package consumer

import client
// import avro.Header
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import domain.CloseableJob
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import models.Event
import models.Log
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException
import util.SerializationUtil.deserialise
import util.SerializationUtil.mapper
import java.io.IOException
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

private val logger = KotlinLogging.logger {}
private const val API_ENDPOINT = "http://localhost:8090/topics"

class Consumer<K, V>(
    private val consumer: KafkaConsumer<K, V>,
    topicNames: List<String>
) : CloseableJob {
    private val closed: AtomicBoolean = AtomicBoolean(false)
    private var finished = CountDownLatch(1)

    init {
        consumer.subscribe(topicNames)
    }

    override fun run() {

        try {
            while (!closed.get()) {
                val records: ConsumerRecords<K, V> = consumer.poll(Duration.ofSeconds(1))
                for (record in records) {
                    val headers: MutableMap<String, String> = mutableMapOf()
                    (record as ConsumerRecord).headers().forEach { header -> headers[header.key()] = String(header.value()) }

                    val eventName = headers["cbp_eventName"].orEmpty().replace("\"", "")

                    try {
                        val genericRecord = deserialise(record.value() as ByteArray, eventName)
                        val topic = record.topic()

                        logger.info { "consumed event: topic = $topic, partition = ${record.partition()}, offset = ${record.offset()}, value = $genericRecord" }

                        val customEvent = genericRecord.toCustomEvent(topic)
                        val log = Log(customEvent.id, topic, customEvent.timestamp, genericRecord.toString())

                        // Add log to DB
                        runBlocking {
                            client.post<HttpResponse> { // <> specifies type that's expected as response from server
                                url("$API_ENDPOINT/$topic/logs")
                                contentType(ContentType.Application.Json)
                                accept(ContentType.Application.Json)
                                header(HttpHeaders.ContentType, ContentType.Application.Json)
                                body = log
                            }
                        }

                        // Add event to DB
                        runBlocking {
                            client.post<HttpResponse> { // <> specifies type that's expected as response from server
                                url("$API_ENDPOINT/$topic")
                                contentType(ContentType.Application.Json)
                                accept(ContentType.Application.Json)
                                header(HttpHeaders.ContentType, ContentType.Application.Json)
                                body = customEvent
                            }
                        }
                    } catch (e: IOException) {
                        logger.error { e.printStackTrace() }
                    } catch (e: Exception) {
                        logger.warn("Message could not be parsed - likely using old version of schemas")
                        logger.error { e.printStackTrace() }
                    }
                }
//                if (!records.isEmpty) {
//                    consumer.commitAsync { offsets, exception ->
//                        if (exception != null) {
//                            producer.logger.error(exception) { "Commit failed for offsets $offsets" }
//                        } else {
//                            producer.logger.info { "Offset committed  $offsets" }
//                        }
//                    }
//                }
            }
            logger.info { "Finish consuming" }
        } catch (e: Throwable) {
            when (e) {
                is WakeupException -> logger.info { "Consumer waked up" }
                else -> logger.error(e) { "Polling failed" }
            }
        } finally {
            // logger.info { "Commit offset synchronously" }
            // consumer.commitSync()
            consumer.close()
            client.close()
            finished.countDown()
            logger.info { "Consumer successfully closed" }
        }
    }

    override fun close() {
        logger.info { "Close job..." }
        closed.set(true)
        consumer.wakeup()
        finished.await(3000, TimeUnit.MILLISECONDS)
        logger.info { "Job is successfully closed" }
    }

    private fun GenericRecord.toCustomEvent(topic: String) =
        mapper.readValue(this["header"].toString().attachTopicName(topic), Event::class.java)

    private fun String.attachTopicName(topic: String) = split("}")[0] + ", \"topicName\": \"$topic\"}"
}
