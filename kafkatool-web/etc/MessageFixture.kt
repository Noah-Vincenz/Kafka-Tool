package util

// ...avroschemas
import org.apache.avro.specific.SpecificRecordBase
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

object MessageFixture {

    private const val ORIGINATOR_ID = "KafkaToolUI"
    private const val ORIGINATOR_DOMAIN = "KafkaToolUI"
    private const val ORIGINATOR_SERVICE = "KafkaToolUI"

    fun buildMessage(topicName: String, eventName: String) =
        when (topicName) {
            "topicName1" -> buildTopicName1Message(eventName)
            "topicName2" -> buildTopicName2Message(eventName)
            // TODO: add event creation for other topics
            else -> null
        }

    // avro pojo payload example
    fun buildTopicName1Message(eventName: String): SomeAvroClass {
        val paymentId = "paymentId"
        return SomeAvroClass.newBuilder()
            .setX(x)
            .setHeader(buildHeader(eventName))
            .build()
    }

    // xml payload example
    fun buildTopicName2Message(eventName: String): SomeAvroClass2 {
        return SomeAvroClass2.newBuilder()
            .setPayload(xmlMessage())
            .setHeader(buildHeader(eventName)).build()
    }

    fun xmlMessage(): String {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
                "<FNXML Version=\"1.0\" xmlns=\"http://www.fnx.com/2004/fnxml\">" +
                "  <Header>" +
                "     <...> +
                "  </Header>" +
                "</FNXML>"
    }
}
