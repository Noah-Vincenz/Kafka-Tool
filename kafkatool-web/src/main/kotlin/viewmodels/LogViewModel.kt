package viewmodels

data class LogViewModel(private val logsIn: String?, private val topicNameIn: String) {
    val logs = logsIn
    val topicName = topicNameIn
}





