package aoc.first


fun solve1(mass: Int): Int {
    val v1 = mass / 3
    return v1 - 2
}

fun solve2(mass: Int, firstCall: Boolean = false): Int {
    return if (mass <= 0) 0
    else (if (firstCall) 0 else mass) + solve2(solve1(mass))
}