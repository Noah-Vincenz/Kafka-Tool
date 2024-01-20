package util

import models.TopicConfig

val topic1 = TopicConfig(
    "sometopicname",
    "sometopickey",
    listOf("") // event names
)

val topicConfigs = listOf(
    topic1
)
