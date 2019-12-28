package aoc.sixth

import aoc.util.InputReader


fun main() {
    val values = mapOf(
        "COM)B" to 1,
        "COM)B\nB)C\n" to 3,
        "COM)B\n" +
                "C)D\n" +
                "B)C\n" +
                "D)E\n" +
                "E)F\n" +
                "B)G\n" +
                "G)H\n" +
                "D)I\n" +
                "E)J\n" +
                "J)K\n" +
                "K)L" to 42
    )

    values
        .map { Pair(solve1(it.key.trim()), it.value) }
        .forEach { println("$it   => ${it.first == it.second}") }

    println(solve1(InputReader().testCase(6)))

}