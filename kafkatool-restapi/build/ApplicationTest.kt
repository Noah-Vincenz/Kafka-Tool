//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import com.fasterxml.jackson.module.kotlin.readValue
//import com.typesafe.config.ConfigFactory
//import io.ktor.application.*
//import io.ktor.config.*
//import io.ktor.http.*
//import io.ktor.server.engine.*
//import io.ktor.server.testing.*
//import models.Event
//import mu.KotlinLogging
//import io.kotest.matchers.shouldBe
//import io.kotest.matchers.string.shouldNotContain
//import org.junit.BeforeClass
//import org.junit.Test
//import org.slf4j.LoggerFactory
//import kotlin.test.assertEquals
//
//private val producer.logger = KotlinLogging.producer.logger {}
//
//class ApplicationTest {
//
//    companion object {
//        val engine = TestApplicationEngine(createCustomTestEnvironment())
//
//        @BeforeClass
//        @JvmStatic
//        fun setup(){
//            producer.logger.debug("Starting application with config ....")
//            engine.start(wait = false)
//            engine.application.module(true)
//        }
//    }
//
//    @Test
//    fun testGetTopics() {
//        with(engine) {
//            handleRequest(HttpMethod.Get, "/topics").apply {
//                response.content shouldBe "{ }"
//                response.content shouldNotContain "id"
//                response.status() shouldBe HttpStatusCode.OK
//            }
//        }
//    }
//
//    @Test
//    fun testGetNonExistingTopicEvents() {
//        with(engine) {
//            handleRequest(HttpMethod.Get, "/topics/SharedKafka2_600000190_accountingentry").apply {
//                response.content shouldBe null
//                response.status() shouldBe HttpStatusCode.NotFound
//            }
//        }
//    }
//}
//
//fun createCustomTestEnvironment(
//    configure: ApplicationEngineEnvironmentBuilder.() -> Unit = {}
//): ApplicationEngineEnvironment = applicationEngineEnvironment {
//    config = MapApplicationConfig().apply {
//        put("ktor.deployment.environment", "test")
//        put("ktor.kafka.consumer.bootstrap.servers", listOf("localhost:9092"))
//        put("ktor.kafka.consumer.bootstrap.servers.size", "1")
//        put("ktor.kafka.consumer.APIGEE_CLIENT_ID", "test")
//        put("ktor.kafka.consumer.APIGEE_CLIENT_SECRET", "test")
//        put("ktor.kafka.consumer.APIGEE_ENDPOINT", "test")
//        put("ktor.kafka.consumer.topic_encryption_enabled", "true")
//        put("ktor.kafka.consumer.crypto_key_deserializer", "test")
//        put("ktor.kafka.consumer.crypto_value_deserializer", "test")
//        put("ktor.kafka.consumer.security_encryption_enabled", "true")
//        put("ktor.kafka.consumer.key_deserializer_class_config", "org.apache.kafka.common.serialization.ByteArrayDeserializer")
//        put("ktor.kafka.consumer.value_deserializer_class_config", "org.apache.kafka.common.serialization.ByteArrayDeserializer")
//        put("ktor.kafka.consumer.group.id", "test")
//        put("ktor.kafka.consumer.client.id", "test")
//        put("ktor.kafka.consumer.auto_offset_reset_config", "none")
//        put("ktor.kafka.consumer.enable_auto_commit_config", "true")
//        put("ktor.kafka.consumer.auto_commit_interval_ms_config", "500")
//        put("ktor.kafka.consumer.session_timeout_ms_config", "30000")
//    }
//    log = LoggerFactory.getLogger("ktor.test")
//    configure()
//}