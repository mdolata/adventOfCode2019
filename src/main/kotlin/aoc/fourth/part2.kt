package aoc.fourth

fun main() {
    val solver1 = Solve1(testCase())
    val solver2 = Solver2()

    val count = testCase()
        .filter { solver1.verify(it) }
        .filter { solver2.verify(it) }
        .count()

    println(count)

}

private fun testCase() = 138241..674034