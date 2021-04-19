import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

typealias Schedule = List<Match>;
data class Match(val home: String, val visitor: String)
typealias ScheduleJson = List<List<String>>

fun loadSchedule(path: String): Schedule {
    val data = {}::class.java.getResource(path).readText();
    val mapper = jacksonObjectMapper()
    val scheduleJson = mapper.readValue<ScheduleJson>(data)
    val schedule = scheduleJson.map { Match(it.get(0), it.get(1)) }
    return schedule
}
