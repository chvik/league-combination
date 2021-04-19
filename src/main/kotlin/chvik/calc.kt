import kotlin.math.pow
import kotlinx.coroutines.channels.*

data class Result(val match: Match, val outcome: Outcome)

typealias ResultSet = List<Result>

enum class Outcome {
    HOME,
    DRAW,
    VISITOR
}

suspend fun iterateOverPossibleResults(
        schedule: Schedule,
        outChannel: Channel<ResultSet>,
        filteredTeams: Set<String>
) {
    val filteredSchedule =
            schedule.filter { it.home in filteredTeams || it.visitor in filteredTeams }
    val numberOfResultSets = 3.0.pow(filteredSchedule.size)
    println("Matches of interest: ${filteredSchedule.size}")
    println("Result sets to be processed: $numberOfResultSets")
    val results = filteredSchedule.map { Result(it, Outcome.HOME) }
    iterateOverPossibleResultsRecursively(0, results, outChannel)
    outChannel.close()
    println("iterateOverPossibleResults terminated")
}

suspend fun iterateOverPossibleResultsRecursively(
        index: Int,
        results: ResultSet,
        outChannel: Channel<ResultSet>
) {
    for (outcome in Outcome.values()) {
        val updatedResults =
                results.mapIndexed { i, origResult ->
                    if (i == index) Result(origResult.match, outcome) else origResult
                }
        if (index < results.size - 1) {
            iterateOverPossibleResultsRecursively(index + 1, updatedResults, outChannel)
        } else {
            outChannel.send(updatedResults)
        }
    }
}

suspend fun calculateFinalStandings(
        initialStanding: Standing,
        inChannel: Channel<ResultSet>,
        filteredTeams: Set<String>,
        filterStanding: (Standing) -> Boolean
) {
    val start = System.nanoTime()
    var processedResultSet = 0
    inChannel.consumeEach {
        resultSet ->
        val finalStanding = calculateStanding(initialStanding, resultSet, filteredTeams)
        if (filterStanding(finalStanding)) {
            println(finalStanding)
            println(resultSet)
        }
        ++processedResultSet
        if (processedResultSet % 1_000_000 == 0) {
            val secPerMillionSets =
                    (System.nanoTime() - start).toDouble() /
                            (1000.0 * 1_000_000.0) /
                            (processedResultSet / 1_000_000.0)
            println("speed $secPerMillionSets")
        }
    }
    println("calculateFinalStandings terminated")
}

fun calculateStanding(
        initialStanding: Standing,
        results: ResultSet,
        filteredTeams: Set<String>
): Standing {
    val pointsByTeam = mutableMapOf<String, Int>()
    initialStanding.map { Pair(it.team, it.points) }.toMap(pointsByTeam)
    results.forEach({ result ->
        when (result.outcome) {
            Outcome.HOME ->
                    pointsByTeam[result.match.home] =
                            pointsByTeam.getOrDefault(result.match.home, 0) + 3
            Outcome.VISITOR ->
                    pointsByTeam[result.match.visitor] =
                            pointsByTeam.getOrDefault(result.match.visitor, 0) + 3
            Outcome.DRAW -> {
                pointsByTeam[result.match.home] =
                        pointsByTeam.getOrDefault(result.match.home, 0) + 1
                pointsByTeam[result.match.visitor] =
                        pointsByTeam.getOrDefault(result.match.visitor, 0) + 1
            }
        }
    })

    val standing =
            pointsByTeam
                    .entries
                    .filter { it.key in filteredTeams }
                    .sortedByDescending { it.value }
                    .map { StandingRow(it.key, it.value) }
    return standing
}
