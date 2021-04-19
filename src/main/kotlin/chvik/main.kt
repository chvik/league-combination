import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

fun main(args: Array<String>) {
    val configuration = configurations[args[0]]!!
    println("range ${configuration.rangeStart}..${configuration.rangeEnd}")
    runBlocking {
        val data = loadData()
        val channel = Channel<ResultSet>()
        val filteredTeams =
                (if (configuration.rangeStart != null && configuration.rangeEnd != null)
                                data.standing.subList(configuration.rangeStart - 1, configuration.rangeEnd)
                        else data.standing)
                        .map { it.team }
                        .toSet()
        launch { iterateOverPossibleResults(data.schedule, channel, filteredTeams) }
        launch { calculateFinalStandings(data.standing, channel, filteredTeams, configuration.isStandingInteresting) }
    }
}

val STANDING_PATH = "/standing29.json"

val SCHEDULE_PATH = "/schedule29.json"

data class Data(val standing: Standing, val schedule: Schedule)

fun loadData(): Data {
    val standing = loadStanding(STANDING_PATH)
    println(standing)
    val schedule = loadSchedule(SCHEDULE_PATH)
    println(schedule)
    return Data(standing, schedule)
}
