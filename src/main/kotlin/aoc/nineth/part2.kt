package aoc.nineth

import aoc.util.InputReader


fun main() {
    val values = mapOf(
        "109,1,3,17,3,18,1202,17,10,18,1,18,17,17,4,17,99,1,1" to "[11]",
        "109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99"
                to "[109, 1, 204, -1, 1001, 100, 1, 100, 1008, 100, 16, 101, 1006, 101, 0, 99]",
        "1102,34915192,34915192,7,4,7,99,0" to "[1219070632396864]",
        "104,1125899906842624,99" to "[1125899906842624]"
    )

//    values
//        .map { Pair(solve1(it.key.trim()), it.value) }
//        .forEach { println("$it   => ${it.first == it.second}") }

    println(solve2(InputReader().testCase(9)))

}
