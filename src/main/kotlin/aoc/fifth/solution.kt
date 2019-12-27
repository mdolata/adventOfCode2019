package aoc.fifth

import java.lang.RuntimeException

data class Instruction(val opCode: OpCode, val in1: Int, val in2: Int, val out: Int)
data class OpCode(val opCode: Int, val modeParam1: Int = 0, val modeParam2: Int = 0) {
    fun isJump(): Boolean {
        return opCode == 5 || opCode == 6
    }
}

fun solve1(data: String): String {
    val list = data.split(",").toMutableList()
    var instructionPointer = 0
    do {
        val instruction = takeNextInstruction(list, instructionPointer)

        instructionPointer = calcNextPointerPosition(instructionPointer, instruction, list)

        apply(list, instruction)

    } while (instruction.opCode.opCode != 99)
    return list.joinToString(",")
}

private fun calcNextPointerPosition(
    actualInstructionPointer: Int,
    instruction: Instruction,
    list: MutableList<String>
): Int {
    if (instruction.opCode.isJump() and itJump(list, instruction)) {
        return jump(list, instruction)
    }

    return actualInstructionPointer + pointerIncrementer(instruction.opCode.opCode)
}

fun jump(list: MutableList<String>, instruction: Instruction): Int {
    return withMode(list, instruction.opCode.modeParam2, instruction.in2)
}

fun itJump(list: MutableList<String>, instruction: Instruction): Boolean {
    val value = withMode(list, instruction.opCode.modeParam1, instruction.in1)

    return when (instruction.opCode.opCode){
        5 -> value != 0
        6 -> value == 0
        else -> false
    }
}

private fun pointerIncrementer(opCode: Int): Int {
    return when (opCode) {
        1, 2, 7, 8 -> 4
        3, 4 -> 2
        5, 6 -> 3
        else -> 0
    }
}

private fun apply(list: MutableList<String>, instruction: Instruction) {
    when (instruction.opCode.opCode) {
        1 -> list[instruction.out] = calc(list, instruction) { i1: Int, i2: Int -> i1 + i2 }
        2 -> list[instruction.out] = calc(list, instruction) { i1: Int, i2: Int -> i1 * i2 }
        3 -> list[instruction.out] = read()
        4 -> println("output -> ${list[instruction.out]}")
        7 -> list[instruction.out] = calc(list, instruction) { i1: Int, i2: Int -> if (i1 < i2) 1 else 0 }
        8 -> list[instruction.out] = calc(list, instruction) { i1: Int, i2: Int -> if (i1 == i2) 1 else 0 }
        99 -> {
        }
    }
}

fun read(): String {
    println("Provide number")
//    return readLine()!!
    return "5" // just for tests
}

fun withMode(list: MutableList<String>, modeParam: Int, input: Int): Int {
    return when (modeParam) {
        0 -> getInt(list, input)
        1 -> input
        else -> throw RuntimeException("unsupported mode")
    }
}

private fun calc(list: MutableList<String>, instruction: Instruction, function: (Int, Int) -> Int) =
    function(
        withMode(
            list, instruction.opCode.modeParam1, instruction.in1
        ),
        withMode(
            list, instruction.opCode.modeParam2, instruction.in2
        )
    ).toString()

private fun takeNextInstruction(data: MutableList<String>, instructionPointer: Int): Instruction {
    val richOpCode = getRichOpCode(data[instructionPointer])
    return when (richOpCode.opCode) {
        1, 2, 7, 8 -> createInstruction2(richOpCode, data, instructionPointer)
        3, 4 -> createInstruction0(richOpCode, data, instructionPointer)
        5, 6 -> createInstruction1(richOpCode, data, instructionPointer)
        99 -> halt()
        else -> throw RuntimeException(richOpCode.toString())
    }
}

fun createInstruction1(opCode: OpCode, data: List<String>, instructionPointer: Int): Instruction {
    return Instruction(
        opCode,
        getInt(data, instructionPointer + 1),
        getInt(data, instructionPointer + 2),
        0
    )
}

fun createInstruction0(opCode: OpCode, data: List<String>, instructionPointer: Int): Instruction {
    return Instruction(
        opCode,
        0,
        0,
        getInt(data, instructionPointer + 1)
    )
}

private fun halt() = Instruction(OpCode(99), 0, 0, 0)


private fun getRichOpCode(opCode: String): OpCode {

    return when (opCode.length) {
        1, 2 -> OpCode(Integer.parseInt(opCode), 0, 0)
        3 -> OpCode(Integer.parseInt(opCode.substring(1)), Integer.parseInt(opCode.substring(0, 1)), 0)
        4 -> OpCode(
            Integer.parseInt(opCode.substring(2)),
            Integer.parseInt(opCode.substring(1, 2)),
            Integer.parseInt(opCode.substring(0, 1))
        )
        else -> throw RuntimeException("too long opcode")
    }
}

private fun createInstruction2(opCode: OpCode, split: List<String>, instructionPointer: Int) =
    Instruction(
        opCode,
        getInt(split, instructionPointer + 1),
        getInt(split, instructionPointer + 2),
        getInt(split, instructionPointer + 3)
    )

private fun getInt(data: List<String>, i: Int) = Integer.parseInt(data[i])

