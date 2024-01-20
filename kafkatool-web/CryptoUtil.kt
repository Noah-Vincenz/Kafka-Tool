package util

import io.ktor.application.*
import models.TopicConfig
import mu.KotlinLogging
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SslConfigs
import java.util.*

private val logger = KotlinLogging.logger {}

fun getProducerConfigs(environment: ApplicationEnvironment, topics: List<TopicConfig>): Map<String, Any> {
    val producerConfig = environment.config.config("ktor.kafka.producer")
    val env = environment.config.property("ktor.environment").getString()

    val props: MutableMap<String, Any> = HashMap()
    topics.forEach { topic ->
        props["${topic.name}_KEY_IDENTIFIER"] = topic.key
        props["${topic.name}_APIGEE_CLIENT_ID"] = producerConfig.property("APIGEE_CLIENT_ID").getString()
        props["${topic.name}_APIGEE_CLIENT_SECRET"] = producerConfig.property("APIGEE_CLIENT_SECRET").getString()
        props["${topic.name}_APIGEE_ENDPOINT"] = producerConfig.property("APIGEE_ENDPOINT").getString()
        props["${topic.name}_ENCRYPTION_ENABLED"] = producerConfig.property("topic_encryption_enabled").getString() //
    }
    props[SecurityConstants.ENVIRONMENT] = env
    props[ProducerConfig.CLIENT_ID_CONFIG] = producerConfig.property("client.id").getString()
    props[SecurityConstants.CRYPTO_KEY_SERIALIZER] = producerConfig.property("crypto_key_serializer").getString()
    props[SecurityConstants.CRYPTO_VALUE_SERIALIZER] = producerConfig.property("crypto_value_serializer").getString()
    props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = producerConfig.property("key_serializer_class_config").getString()
    props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = producerConfig.property("value_serializer_class_config").getString()
    props[ProducerConfig.ACKS_CONFIG] = producerConfig.property("acks_config").getString()
    if (env == "E1") {
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = producerConfig.property("kafka_server_e1").getList()
        props[CommonClientConfigs.SECURITY_PROTOCOL_CONFIG] = producerConfig.property("security_protocol_config").getString()
        props[SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG] = producerConfig.property("ssl_truststore_location_config").getString()
        props[SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG] = producerConfig.property("ssl_truststore_password_config").getString()
        props[SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG] = producerConfig.property("ssl_keystore_location_config").getString()
        props[SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG] = producerConfig.property("ssl_keystore_password_config").getString()
        props[SslConfigs.SSL_KEY_PASSWORD_CONFIG] = producerConfig.property("ssl_key_password_config").getString()
    } else {
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = producerConfig.property("bootstrap.servers").getList()
    }
    return props
}

fun getConsumerConfigs(environment: ApplicationEnvironment, topics: List<TopicConfig>): Properties {
    val consumerConfig = environment.config.config("ktor.kafka.consumer")
    val env = environment.config.property("ktor.environment").getString()

    return Properties().apply {
        topics.forEach { topic ->
            this["${topic.name}_KEY_IDENTIFIER"] = topic.key
            this["${topic.name}_APIGEE_CLIENT_ID"] = consumerConfig.property("APIGEE_CLIENT_ID").getString()
            this["${topic.name}_APIGEE_CLIENT_SECRET"] = consumerConfig.property("APIGEE_CLIENT_SECRET").getString()
            this["${topic.name}_APIGEE_ENDPOINT"] = consumerConfig.property("APIGEE_ENDPOINT").getString()
            this["${topic.name}_ENCRYPTION_ENABLED"] = consumerConfig.property("topic_encryption_enabled").getString() //
        }
        this[SecurityConstants.CRYPTO_KEY_DESERIALIZER] = consumerConfig.property("crypto_key_deserializer").getString()
        this[SecurityConstants.CRYPTO_VALUE_DESERIALIZER] = consumerConfig.property("crypto_value_deserializer").getString()
        this[SecurityConstants.ENCRYPTION_ENABLED] = consumerConfig.property("security_encryption_enabled").getString() //
        this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = consumerConfig.property("key_deserializer_class_config").getString()
        this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = consumerConfig.property("value_deserializer_class_config").getString()
        this[ConsumerConfig.GROUP_ID_CONFIG] = consumerConfig.property("group.id").getString()
        this[ConsumerConfig.CLIENT_ID_CONFIG] = consumerConfig.property("client.id").getString()
        this[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = consumerConfig.property("auto_offset_reset_config").getString()
        this[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = consumerConfig.property("enable_auto_commit_config").getString()
        this[ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG] = consumerConfig.property("session_timeout_ms_config").getString()
        // this[ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG] = consumerConfig.property("auto_commit_interval_ms_config").getString()
        this[ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG] = "100000"
        this[ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG] = "100000"
        this[ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG] = "100000"
        this[ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG] = "100000"
        if (env == "E1") {
            this[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = consumerConfig.property("kafka_server_e1").getList()
            this[CommonClientConfigs.SECURITY_PROTOCOL_CONFIG] = consumerConfig.property("security_protocol_config").getString()
            this[SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG] = consumerConfig.property("ssl_truststore_location_config").getString()
            this[SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG] = consumerConfig.property("ssl_truststore_password_config").getString()
            this[SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG] = consumerConfig.property("ssl_keystore_location_config").getString()
            this[SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG] = consumerConfig.property("ssl_keystore_password_config").getString()
            this[SslConfigs.SSL_KEY_PASSWORD_CONFIG] = consumerConfig.property("ssl_key_password_config").getString()
        } else {
            this[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = consumerConfig.property("bootstrap.servers").getList()
        }
    }
}
