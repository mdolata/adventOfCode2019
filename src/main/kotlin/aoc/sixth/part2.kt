package aoc.sixth

import aoc.util.InputReader


fun main() {
    val values = mapOf(
        "COM)B\n" +
                "B)C\n" +
                "C)D\n" +
                "D)E\n" +
                "E)F\n" +
                "B)G\n" +
                "G)H\n" +
                "D)I\n" +
                "E)J\n" +
                "J)K\n" +
                "K)L\n" +
                "K)YOU\n" +
                "I)SAN" to 4
    )

    values
        .map { Pair(solve2(it.key.trim()), it.value) }
        .forEach { println("$it   => ${it.first == it.second}") }

    println(solve2(InputReader().testCase(6)))

}