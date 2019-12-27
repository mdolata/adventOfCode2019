package aoc.fifth

import java.lang.RuntimeException

data class Instruction(val opCode: Int, val in1: Int, val in2: Int, val out: Int)

fun solve1(data: String): String {
    val list = data.split(",").toMutableList()
    var instructionPointer = 0
    do {
        val instruction = takeNextInstruction(list, instructionPointer)

        instructionPointer += pointerIncrementer(instruction.opCode)

        apply(list, instruction)

    } while (instruction.opCode != 99)
    return list.joinToString(",")
}

private fun pointerIncrementer(opCode: Int): Int {
    return when (opCode) {
        1, 2 -> 4
        3, 4 -> 2
        else -> 0
    }
}

private fun apply(list: MutableList<String>, instruction: Instruction) {
    when (instruction.opCode) {
        1 -> list[instruction.out] = calc(list, instruction) { i1: Int, i2: Int -> i1 + i2 }
        2 -> list[instruction.out] = calc(list, instruction) { i1: Int, i2: Int -> i1 * i2 }
        3 -> list[instruction.out] = read()
        4 -> println("output -> ${list[instruction.out]}")
        99 -> {
        }
    }
}

fun read(): String {
    println("Provide number")
    return readLine()!!
}


private fun calc(list: MutableList<String>, instruction: Instruction, function: (Int, Int) -> Int) =
    function(getInt(list, instruction.in1), getInt(list, instruction.in2)).toString()


private fun takeNextInstruction(data: MutableList<String>, instructionPointer: Int): Instruction =
    when (getInt(data, instructionPointer)) {
        1 -> addition(data, instructionPointer)
        2 -> multiplication(data, instructionPointer)
        3 -> sysIn(data, instructionPointer)
        4 -> sysOut(data, instructionPointer)
        99 -> halt()
        else -> throw RuntimeException()
    }

private fun halt() = Instruction(99, 0, 0, 0)
private fun addition(data: List<String>, instructionPointer: Int) = createFour(1, data, instructionPointer)
private fun multiplication(data: List<String>, instructionPointer: Int) = createFour(2, data, instructionPointer)
private fun sysIn(data: MutableList<String>, instructionPointer: Int) = Instruction(3,0,0, getInt(data, instructionPointer + 1))
private fun sysOut(data: MutableList<String>, instructionPointer: Int) = Instruction(4, 0,0, getInt(data,instructionPointer + 1))


private fun createFour(opCode1: Int, split: List<String>, instructionPointer: Int) =
    Instruction(
        opCode1,
        getInt(split, instructionPointer + 1),
        getInt(split, instructionPointer + 2),
        getInt(split, instructionPointer + 3)
    )


private fun getInt(data: List<String>, i: Int) = Integer.parseInt(data[i])

fun solve2(data: String): Int {
    return Integer.parseInt(solve1(data).split(",")[0])
}