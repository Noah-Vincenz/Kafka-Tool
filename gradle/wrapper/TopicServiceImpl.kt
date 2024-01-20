
@file:Suppress("PackageDirectoryMismatch")
package main.kotlin

import client
import models.Event
import io.ktor.client.request.*
import models.Log

private const val SEPARATOR = "\n\n"

class TopicServiceImpl : TopicService {

    private var apiEndpoint: String = "http://localhost:8090/topics"

    override fun setApiEndpoint(apiEndpointUrl: String) {
        this.apiEndpoint = apiEndpointUrl
    }

    override suspend fun getEvents(topicName: String, fromDate: String?, fromTime: String?, toDate: String?, toTime: String?) =
        if (fromDate.isNullOrEmpty() && toDate.isNullOrEmpty()) emptyList()
        else client.get<List<Event>>("$apiEndpoint/$topicName?fromDate=$fromDate&fromTime=$fromTime&toDate=$toDate&toTime=$toTime")

    override suspend fun getLogs(topicName: String, fromDate: String?, fromTime: String?, toDate: String?, toTime: String?, filterParam: String?) =
        if (fromDate.isNullOrEmpty() && toDate.isNullOrEmpty()) ""
        else with(client.get<List<Log>>("$apiEndpoint/$topicName/logs?fromDate=$fromDate&fromTime=$fromTime&toDate=$toDate&toTime=$toTime")) {
            filter { log ->
                filterParam?.let { filterParam ->
                    filterParam.split(" ").all { it in log.content }
                } ?: true
            }.joinToString(SEPARATOR) {
                it.timestamp.toString() + " --- " + it.content
            }
        }
}
