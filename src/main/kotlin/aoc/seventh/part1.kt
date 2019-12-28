package aoc.seventh


fun main() {
    val values = mapOf(
        "3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0" to "43210",
        "3,23,3,24,1002,24,10,24,1002,23,-1,23," +
                "101,5,23,23,1,24,23,23,4,23,99,0,0" to "54321",
        "3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33," +
                "1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0" to "65210"
    )

    values
        .map { Pair(solve1(it.key.trim()), it.value) }
        .forEach { println("$it   => ${it.first == it.second}") }

    println(solve1(MySpec().testCase()))

}

class MySpec {

    fun testCase(): String {
        return MySpec::class.java.getResource("/day7.input").readText()
    }
}