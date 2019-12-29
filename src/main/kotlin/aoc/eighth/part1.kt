package aoc.eighth

import aoc.util.InputReader


fun main() {
    val values = mapOf(
        "123456789012" to "notimportant"
    )

    values
        .map { Pair(solve1(it.key.trim(), 3, 2), it.value) }
        .forEach { println(it.first) }

    println(solve1(InputReader().testCase(8), 25, 6))

}
