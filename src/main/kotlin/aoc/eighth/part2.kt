package aoc.eighth

import aoc.util.InputReader


fun main() {
    val values = mapOf(
        "0222112222120000" to listOf("01", "10")
    )

    values
        .map { Pair(solve2(it.key.trim(), 2, 2), it.value) }
        .forEach { println(it) }

    val message = solve2(InputReader().testCase(8), 25, 6)

    message.map { it.replace('0', ' ') }.forEach { println(it) }

}

