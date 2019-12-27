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

