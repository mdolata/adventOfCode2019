package aoc.fourth

class Solve1(private val range: IntRange) {
    fun verify(num: Int): Boolean {
        return isInRange(num)
                && areTwoAdjacentDigitsTheSame(num)
                && isNeverDecrease(num)
    }

    private fun isNeverDecrease(num: Int): Boolean {
        return isLowerThanNext("$num", 0)
    }

    private fun isLowerThanNext(num: String, i: Int): Boolean {
        return when {
            i == num.length - 1 -> true
            num[i] > num[i + 1] -> false
            else -> isLowerThanNext(num, i + 1)
        }
    }

    private fun areTwoAdjacentDigitsTheSame(num: Int): Boolean {
        return areTwoAdjacentDigitsTheSame("$num", 0)
    }

    private fun areTwoAdjacentDigitsTheSame(num: String, pairNo: Int): Boolean {
        return when {
            pairNo == num.length - 1 -> false
            num[pairNo] == num[pairNo + 1] -> true
            else -> areTwoAdjacentDigitsTheSame(num, pairNo + 1)
        }
    }

    private fun isInRange(num: Int): Boolean {
        return num in range
    }
}

class Solver2 {
    fun verify(num: Int): Boolean {
        return isPairPartOfLargerGroup(num)
    }

    private fun isPairPartOfLargerGroup(num: Int): Boolean {
        val lst = mutableListOf<MutablePair<Char>>()

        for (c in "$num") {
            val last = lst.getOrElse(lst.size - 1) { MutablePair(c, 0) }
            when {
                last.second == 0 -> {
                    last.increment()
                    lst.add(last)
                }
                last.first == c -> last.increment()
                else -> lst.add(MutablePair(c, 1))
            }
        }

        return lst.map { it.second }.contains(2)


    }
}



data class MutablePair<T>(val first: T, var second: Int) {
    fun increment() {
        second += 1
    }
}