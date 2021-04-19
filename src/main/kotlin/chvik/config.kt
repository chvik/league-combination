data class Config(
        val rangeStart: Int?,
        val rangeEnd: Int?,
        val isStandingInteresting: (Standing) -> Boolean
)

val configurations =
        mapOf(
                "puskas-champion" to
                        Config(1, 2, { it[0].team != "FTC" || it[1].points == it[0].points }),
                "ujpest-relegated" to
                        Config(
                                8,
                                12,
                                {
                                    it[3].team == "Újpest" ||
                                            it[4].team == "Újpest" ||
                                            (it[2].team == "Újpest" &&
                                                    it[2].points == it[3].points) ||
                                            (it[1].team == "Újpest" && it[1].points == it[3].points)
                                }),
                "honved-relegated" to
                        Config(
                                9,
                                12,
                                {
                                    it[2].team == "Honvéd" ||
                                            it[3].team == "Honvéd" ||
                                            (it[1].team == "Honvéd" &&
                                                    it[1].points == it[2].points) ||
                                            (it[0].team == "Honvéd" && it[0].points == it[2].points)
                                }))
