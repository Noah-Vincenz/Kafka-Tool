package producer

import io.ktor.application.*
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import util.getProducerConfigs
import util.topicConfigs

fun <K, V> buildProducer(
    environment: ApplicationEnvironment
): Producer<K, V> = Producer(KafkaTemplate(DefaultKafkaProducerFactory(getProducerConfigs(environment, topicConfigs))))
