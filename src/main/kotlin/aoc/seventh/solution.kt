package aoc.seventh

import java.lang.RuntimeException
import java.util.*

data class Instruction(val opCode: OpCode, val in1: Int, val in2: Int, val out: Int)
data class OpCode(val opCode: Int, val modeParam1: Int = 0, val modeParam2: Int = 0) {
    fun isJump(): Boolean {
        return opCode == 5 || opCode == 6
    }
}

fun solve1(data: String): String {
    val list = data.split(",").toMutableList()

    val amplifier = Amplifier(list)

    var max = Integer.MIN_VALUE

    var sequenceInput1 = listOf("0", "0", "0", "0", "0")

    for (i in 1..(5 * 5 * 5 * 5 * 5)) {
        sequenceInput1 = nextInputSequence(sequenceInput1)
        val output = run(amplifier, sequenceInput1)

        max = Integer.max(max, Integer.parseInt(output))

    }

    return "$max"
}

private fun nextInputSequence(sequenceInput1: List<String>): List<String> {
    val tmp = sequenceInput1.map { Integer.parseInt(it) }.reversed().toMutableList()

    tmp[0] += 1
    var isOverload = false
    for (i in 0 until tmp.size) {
        tmp[i] += if (isOverload) 1 else 0

        if (tmp[i] == 5) {
            tmp[i] = 0
            isOverload = true
        } else {
            isOverload = false
        }
    }

    val result = tmp.map { "$it" }.reversed()

    if (tmp.toSet().size != tmp.size) {
        return nextInputSequence(result)
    }

    return result
}

private fun run(amplifier: Amplifier, sequenceInput1: List<String>): String {
    val sequenceInput2 = mutableListOf("0")

    for (i in 0..4) {

        amplifier.provideInputs(listOf(sequenceInput1[i], sequenceInput2[i]))
        val output = amplifier.runCode()

        sequenceInput2.add(output[0])
    }

    return sequenceInput2.last()
}

class Amplifier(private val instructions: List<String>) {

    private var operationInstructions = instructions.toMutableList()
    private var inputs = ArrayDeque<String>()
    private var instructionPointer = 0
    private var outputs = mutableListOf<String>()

    fun runCode(): List<String> {
        resetState()
        do {
            val instruction = takeNextInstruction(operationInstructions, instructionPointer)
            instructionPointer = calcNextPointerPosition(instructionPointer, instruction, operationInstructions)

            apply(operationInstructions, instruction)
        } while (instruction.opCode.opCode != 99)


        return outputs.toList()
    }

    private fun resetState() {
        operationInstructions = instructions.toMutableList()
        instructionPointer = 0
        outputs = mutableListOf()
    }

    public fun provideInputs(inputs: List<String>) {
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

    fun jump(list: MutableList<String>, instruction: Instruction): Int {
        return withMode(list, instruction.opCode.modeParam2, instruction.in2)
    }

    fun itJump(list: MutableList<String>, instruction: Instruction): Boolean {
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


}

