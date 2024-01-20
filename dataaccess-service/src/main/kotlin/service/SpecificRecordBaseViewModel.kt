package viewmodels

data class SpecificRecordBaseViewModel(
    private val eventIn: String,
    private val topicNameIn: String,
    private val fromDateIn: String?,
    private val fromTimeIn: String?,
    private val toDateIn: String?,
    private val toTimeIn: String?,
    private val eventNameIn: String
) {
    val specificRecordBase = eventIn
    val topicName = topicNameIn
    val fromDate = fromDateIn
    val fromTime = fromTimeIn
    val toDate = toDateIn
    val toTime = toTimeIn
    val eventName = eventNameIn
}
