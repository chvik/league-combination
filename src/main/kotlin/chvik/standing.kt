import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

typealias Standing = List<StandingRow>;
data class StandingRow(val team: String, val points: Int)
typealias StandingJson = List<List<Any>>

fun loadStanding(path: String): Standing {
    val data = {}::class.java.getResource(path).readText();
    val mapper = jacksonObjectMapper()
    val standingJson = mapper.readValue<StandingJson>(data)
    val standing = standingJson.map { StandingRow(it[0] as String, it[1] as Int) }
    return standing
}
