package consumer

import io.ktor.application.*
import models.TopicConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import util.getConsumerConfigs

fun <K, V> buildConsumer(
    environment: ApplicationEnvironment,
    topicConfigs: List<TopicConfig>
): Consumer<K, V> = Consumer(KafkaConsumer(getConsumerConfigs(environment, topicConfigs)), topicConfigs.map { topic -> topic.name })
