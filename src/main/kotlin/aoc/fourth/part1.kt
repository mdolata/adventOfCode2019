package aoc.fourth

fun main() {
    val solver1 = Solve1(testCase())

    val count = testCase().filter { solver1.verify(it) }.count()

    println(count)

}

private fun testCase() = 138241..674034