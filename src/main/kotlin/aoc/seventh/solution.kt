package aoc.seventh

import java.lang.RuntimeException
import java.util.*

data class Instruction(val opCode: OpCode, val in1: Int, val in2: Int, val out: Int) {
    fun isReadOperation(): Boolean {
        return opCode.opCode == 3
    }
}

data class OpCode(val opCode: Int, val modeParam1: Int = 0, val modeParam2: Int = 0) {
    fun isJump(): Boolean {
        return opCode == 5 || opCode == 6
    }
}

fun solve1(data: String): String {
    val list = data.split(",").toMutableList()

    var max = Integer.MIN_VALUE

    var sequenceInput1 = listOf("0", "0", "0", "0", "0")
    var maxSequence = listOf<String>()

    for (i in 1..(5 * 5 * 5 * 5 * 5)) {
        sequenceInput1 = nextInputSequence(sequenceInput1, 0, 4)
        val output = runWithFeedbackLoop(list, sequenceInput1)

        val parsedInt = Integer.parseInt(output)

        if (parsedInt > max) {
            max = parsedInt
            maxSequence = sequenceInput1
        }
    }

    println ("$maxSequence $max")
    return "$max"
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

    println ("$maxSequence $max")
    return "$max"
}

fun runWithFeedbackLoop(list: MutableList<String>, sequenceInput1: List<String>): String {
    val sequenceInput2 = mutableListOf("0")

    val amplifiers = sequenceInput1.map { Amplifier(list, it) }
    for (amplifier in amplifiers) {

        amplifier.provideInputs(listOf(amplifier.phaseSetting, sequenceInput2.last()))
        val output = amplifier.runCode()

        sequenceInput2.add(output[0])

    }

    while (amplifiers.all { it.isNotHalted() }) {
        for (amplifier in amplifiers) {

            amplifier.provideInputs(listOf(sequenceInput2.last()))
            val output = amplifier.runCode()

            sequenceInput2.add(output.last())

        }
    }

    return sequenceInput2.last()
}

private fun run(
    list: List<String>,
    sequenceInput1: List<String>
): String {

    val sequenceInput2 = mutableListOf("0")

    val amplifiers = sequenceInput1.map { Amplifier(list, it) }
    for (amplifier in amplifiers) {

        amplifier.provideInputs(listOf(amplifier.phaseSetting, sequenceInput2.last()))
        val output = amplifier.runCode()

        sequenceInput2.add(output[0])

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

class Amplifier(instructions: List<String>, val phaseSetting: String) {

    private var operationInstructions = instructions.toMutableList()
    private var inputs = ArrayDeque<String>()
    private var instructionPointer = 0
    private var outputs = mutableListOf<String>()
    private var lastInstructionCode = -1

    fun runCode(): List<String> {
        do {
            val instruction = takeNextInstruction(operationInstructions, instructionPointer)
            lastInstructionCode = instruction.opCode.opCode

            if (instruction.isReadOperation() && inputs.isEmpty()) {
                break
            }

            instructionPointer = calcNextPointerPosition(instructionPointer, instruction, operationInstructions)

            apply(operationInstructions, instruction)
        } while (instruction.opCode.opCode != 99)

        return outputs.toList()
    }

    fun provideInputs(inputs: List<String>) {
        this.inputs = ArrayDeque(inputs)
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

    private fun jump(list: MutableList<String>, instruction: Instruction): Int {
        return withMode(list, instruction.opCode.modeParam2, instruction.in2)
    }

    private fun itJump(list: MutableList<String>, instruction: Instruction): Boolean {
        val value = withMode(list, instruction.opCode.modeParam1, instruction.in1)

        return when (instruction.opCode.opCode) {
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
            4 -> setOutput(list[instruction.out])
            7 -> list[instruction.out] = calc(list, instruction) { i1: Int, i2: Int -> if (i1 < i2) 1 else 0 }
            8 -> list[instruction.out] = calc(list, instruction) { i1: Int, i2: Int -> if (i1 == i2) 1 else 0 }
            99 -> {
            }
        }
    }

    private fun setOutput(output: String) {
        outputs.add(output)
    }

    private fun read(): String {
        return inputs.poll()
    }

    private fun withMode(list: MutableList<String>, modeParam: Int, input: Int): Int {
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

    private fun createInstruction1(opCode: OpCode, data: List<String>, instructionPointer: Int): Instruction {
        return Instruction(
            opCode,
            getInt(data, instructionPointer + 1),
            getInt(data, instructionPointer + 2),
            0
        )
    }

    private fun createInstruction0(opCode: OpCode, data: List<String>, instructionPointer: Int): Instruction {
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
    fun isNotHalted() = lastInstructionCode != 99
}

