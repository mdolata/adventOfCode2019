package aoc.fifth

import java.lang.RuntimeException

data class Four(val opCode: Int, val in1: Int, val in2: Int, val out: Int)

fun solve1(data: String): String {
    val list = data.split(",").toMutableList()
    var i = 0
    do {
        val four = takeNextFour(list, i++)

        if (four.opCode != 99) {
            apply(list, four)
        }
    } while (four.opCode != 99)
    return list.joinToString(",")
}

private fun apply(list: MutableList<String>, four: Four) {
    when (four.opCode) {
        1 -> {
            list[four.out] = calc(list, four) { i1: Int, i2: Int -> i1 + i2 }
        }
        2 ->
            list[four.out] = calc(list, four) { i1: Int, i2: Int -> i1 * i2 }
    }
}


private fun calc(list: MutableList<String>, four: Four, function: (Int, Int) -> Int) =
    function(getInt(list, four.in1), getInt(list, four.in2)).toString()


private fun takeNextFour(data: MutableList<String>, i: Int): Four =
    when (getInt(data, i * 4)) {
        1 -> {
            createFour(1, data, i)
        }
        2 -> {
            createFour(2, data, i)
        }
        99 -> {
            createFour(99)
        }
        else -> {
            throw RuntimeException()
        }
    }

private fun createFour(opCode1: Int) =
    Four(opCode1, 0, 0, 0)


private fun createFour(opCode1: Int, split: List<String>, i: Int) =
    Four(opCode1, getInt(split, i * 4 + 1), getInt(split, i * 4 + 2), getInt(split, i * 4 + 3))


private fun getInt(split: List<String>, i: Int) =
    Integer.parseInt(split[i])

fun solve2(data: String): Int {
    return Integer.parseInt(solve1(data).split(",")[0])
}