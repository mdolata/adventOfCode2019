package aoc.util

import java.lang.RuntimeException
import java.util.*


data class Instruction(val opCode: OpCode, val in1: Long, val in2: Long, val out: Int) {
    fun isReadOperation(): Boolean {
        return opCode.opCode == 3
    }
}

data class OpCode(val opCode: Int, val modeParam1: Int = 0, val modeParam2: Int = 0, val modeOutput: Int = 0) {
    fun isJump(): Boolean {
        return opCode == 5 || opCode == 6
    }
}

class Amplifier(instructions: List<String>, val phaseSetting: String) {

    private var operationInstructions = instructions.toMutableList()
    private var inputs = ArrayDeque<String>()
    private var instructionPointer = 0
    private var outputs = mutableListOf<String>()
    private var lastInstructionCode = -1
    private var relativeBase = 0

    fun runCode(): List<String> {
        do {
            val instruction = takeNextInstruction(operationInstructions, instructionPointer)
            lastInstructionCode = instruction.opCode.opCode

            if (breakWhenExpectedInputIsMissing(instruction)) break

            expandMemoryIfNeeded(operationInstructions, instruction)


            instructionPointer = calcNextPointerPosition(instructionPointer, instruction, operationInstructions)

            apply(operationInstructions, instruction)
        } while (instruction.opCode.opCode != 99)

        return outputs.toList()
    }

    private fun expandMemoryIfNeeded(operationInstructions: MutableList<String>, instruction: Instruction) {
        checkAndExpandMemory(instruction.opCode.modeParam1, instruction.in1, operationInstructions)
        checkAndExpandMemory(instruction.opCode.modeParam2, instruction.in2, operationInstructions)
        checkAndExpandMemory(0, instruction.out.toLong(), operationInstructions)
    }

    private fun checkAndExpandMemory(
        modeParam: Int,
        parameter: Long,
        operationInstructions: MutableList<String>
    ) {
        if (modeParam != 1) {
            val addressInUse = when (modeParam) {
                0 -> parameter
                2 -> parameter + relativeBase
                else -> 0
            }

            if (addressInUse >= operationInstructions.size) {
                expand(operationInstructions, addressInUse)
            }
        }
    }

    private fun expand(operationInstructions: MutableList<String>, addressInUse: Long) {
        for (i in 0..addressInUse - operationInstructions.size + 1) {
            operationInstructions.add("0")
        }
    }

    private fun breakWhenExpectedInputIsMissing(instruction: Instruction): Boolean {
        return instruction.isReadOperation() && inputs.isEmpty()
    }

    fun provideInputs(inputs: List<String>) {
        this.inputs.addAll(inputs)
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
        return withMode(list, instruction.opCode.modeParam2, instruction.in2).toInt()
    }

    private fun itJump(list: MutableList<String>, instruction: Instruction): Boolean {
        val value = withMode(list, instruction.opCode.modeParam1, instruction.in1).toInt()

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
            9 -> 2
            else -> 0
        }
    }

    private fun apply(list: MutableList<String>, instruction: Instruction) {
        when (instruction.opCode.opCode) {
            1 -> list[addressWithMode(instruction.opCode.modeOutput, instruction.out)] = calc(list, instruction) { i1: Long, i2: Long -> i1 + i2 }
            2 -> list[addressWithMode(instruction.opCode.modeOutput, instruction.out)] = calc(list, instruction) { i1: Long, i2: Long -> i1 * i2 }
            3 -> list[addressWithMode(instruction.opCode.modeParam1, instruction.in1.toInt())] = read()
            4 -> setOutput(withMode(list, instruction.opCode.modeParam1, instruction.in1).toString())
            7 -> list[addressWithMode(instruction.opCode.modeOutput, instruction.out)] = calc(list, instruction) { i1: Long, i2: Long -> if (i1 < i2) 1 else 0 }
            8 -> list[addressWithMode(instruction.opCode.modeOutput, instruction.out)] = calc(list, instruction) { i1: Long, i2: Long -> if (i1 == i2) 1 else 0 }
            9 -> relativeBase += withMode(list, instruction.opCode.modeParam1, instruction.in1).toInt()
            99 -> {
            }
        }
    }

    private fun addressWithMode(modeParam: Int, input: Int): Int {
        return when (modeParam) {
            0 -> input
            1 -> input
            2 -> input + relativeBase
            else -> throw RuntimeException("unsupported mode")
        }.toInt()
    }

    private fun setOutput(output: String) {
        outputs.add(output)
    }

    private fun read(): String {
        return inputs.poll()
    }

    private fun withMode(list: MutableList<String>, modeParam: Int, input: Long): Long {
        return when (modeParam) {
            0 -> getValue(list, input.toInt())
            1 -> input
            2 -> getValue(list, input.toInt() + relativeBase)
            else -> throw RuntimeException("unsupported mode")
        }
    }

    private fun calc(list: MutableList<String>, instruction: Instruction, function: (Long, Long) -> Long) =
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
            5, 6 -> createInstruction1(richOpCode, data, instructionPointer)
            3, 4, 9 -> createInstruction1Param(richOpCode, data, instructionPointer)
            99 -> halt()
            else -> throw RuntimeException(richOpCode.toString())
        }
    }

    private fun createInstruction1Param(
        opCode: OpCode,
        data: List<String>,
        instructionPointer: Int
    ): Instruction {
        return Instruction(
            opCode,
            getValue(data, instructionPointer + 1),
            0,
            0
        )
    }

    private fun createInstruction1(opCode: OpCode, data: List<String>, instructionPointer: Int): Instruction {
        return Instruction(
            opCode,
            getValue(data, instructionPointer + 1),
            getValue(data, instructionPointer + 2),
            0
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
            5 -> OpCode(
                Integer.parseInt(opCode.substring(3)),
                Integer.parseInt(opCode.substring(2, 3)),
                Integer.parseInt(opCode.substring(1, 2)),
                Integer.parseInt(opCode.substring(0, 1))
            )
            else -> throw RuntimeException("too long opcode")
        }
    }

    private fun createInstruction2(opCode: OpCode, split: List<String>, instructionPointer: Int) =
        Instruction(
            opCode,
            getValue(split, instructionPointer + 1),
            getValue(split, instructionPointer + 2),
            getValue(split, instructionPointer + 3).toInt()
        )

    private fun getValue(data: List<String>, i: Int) = data[i].toLong()
    fun isNotHalted() = lastInstructionCode != 99
}