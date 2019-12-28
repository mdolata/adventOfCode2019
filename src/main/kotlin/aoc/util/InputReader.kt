package aoc.util

class InputReader {
    fun testCase(day: Int): String {
        return InputReader::class.java.getResource("/day$day.input").readText()
    }
}