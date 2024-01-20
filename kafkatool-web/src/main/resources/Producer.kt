package producer

import mu.KotlinLogging
import org.apache.avro.generic.GenericRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.MessageBuilder
import util.SerializationUtil.serialise

private val logger = KotlinLogging.logger {}
const val MESSAGE_HEADER_CORRELATION_ID = "cbp_correlationId"
const val MESSAGE_HEADER_ORIGINATOR_ID = "cbp_originatorId"
const val MESSAGE_HEADER_EVENT_NAME = "cbp_eventName"
const val MESSAGE_HEADER_EVENT_ID = "cbp_eventId"

class Producer<K, V>(private val kafkaTemplate: KafkaTemplate<String, ByteArray>) {

    fun produce(record: GenericRecord, topicName: String, id: String, originatorId: String, correlationId: String, eventName: String) {

        val message = MessageBuilder.withPayload(serialise(record, eventName))
            .setHeader(KafkaHeaders.TOPIC, topicName)
            .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
            .setHeader(MESSAGE_HEADER_CORRELATION_ID, correlationId)
            .setHeader(MESSAGE_HEADER_ORIGINATOR_ID, originatorId)
            .setHeader(MESSAGE_HEADER_EVENT_NAME, eventName)
            .setHeader(MESSAGE_HEADER_EVENT_ID, id)
            .build()

        val listenableFuture = kafkaTemplate.send(message)
        val sendResultMetaData = listenableFuture.get().recordMetadata

        logger.info { "Produced message to topic = $topicName, partition = ${sendResultMetaData.partition()}, " +
                "offset = ${sendResultMetaData.offset()}, value = $record"
        }
    }
}
