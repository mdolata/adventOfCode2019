package aoc.nineth

import aoc.util.Amplifier


fun solve1(data: String): String {
    val list = data.split(",").toMutableList()

    val amplifier = Amplifier(list, "not used")

    amplifier.provideInputs(listOf("1"))
    val result = amplifier.runCode()


    return "$result"
}

fun solve2(data: String): String {
    val list = data.split(",").toMutableList()

    val amplifier = Amplifier(list, "not used")

    amplifier.provideInputs(listOf("2"))
    val result = amplifier.runCode()

    println(amplifier.isNotHalted())

    return "$result"
}

