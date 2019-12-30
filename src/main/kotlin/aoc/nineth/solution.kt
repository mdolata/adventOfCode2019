package aoc.nineth

import aoc.util.Amplifier


fun solve1(data: String): String {
    val list = data.split(",").toMutableList()

    val amplifier = Amplifier(list, "1")

    amplifier.provideInputs(listOf("1", "1", "1"))
    val result = amplifier.runCode()


    return "$result"
}

fun solve2(data: String): String {
    val list = data.split(",").toMutableList()

    var max = Integer.MIN_VALUE

    var sequenceInput1 = listOf("5", "5", "5", "5", "5")
    var maxSequence = listOf<String>()

    for (i in 1..(5 * 5 * 5 * 5 * 5)) {
        sequenceInput1 = nextInputSequence(sequenceInput1, 5, 9)
        val output = runWithFeedbackLoop(list, sequenceInput1)

        val parsedInt = Integer.parseInt(output)

        if (parsedInt > max) {
            max = parsedInt
            maxSequence = sequenceInput1
        }
    }

    println("$maxSequence $max")
    return "$max"
}

fun runWithFeedbackLoop(list: MutableList<String>, sequenceInput1: List<String>): String {
    val sequenceInput2 = mutableListOf("0")

    val amplifiers = sequenceInput1.map { Amplifier(list, it) }

    amplifiers.forEach { it.provideInputs(listOf(it.phaseSetting)) }

    while (amplifiers.all { it.isNotHalted() }) {
        for (amplifier in amplifiers) {

            amplifier.provideInputs(listOf(sequenceInput2.last()))
            val output = amplifier.runCode()

            sequenceInput2.add(output.last())

        }
    }

    return sequenceInput2.last()
}

private fun nextInputSequence(sequenceInput1: List<String>, min: Int, max: Int): List<String> {
    val tmp = sequenceInput1.map { Integer.parseInt(it) }.reversed().toMutableList()

    tmp[0] += 1
    var isOverload = false
    for (i in 0 until tmp.size) {
        tmp[i] += if (isOverload) 1 else 0

        if (tmp[i] > max) {
            tmp[i] = min
            isOverload = true
        } else {
            isOverload = false
        }
    }

    val result = tmp.map { "$it" }.reversed()

    if (tmp.toSet().size != tmp.size) {
        return nextInputSequence(result, min, max)
    }

    return result
}

