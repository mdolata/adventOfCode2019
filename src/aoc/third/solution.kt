package aoc.third

import java.lang.RuntimeException
import kotlin.math.abs

data class Point(val x: Int, val y: Int)
data class Step(val direction: String, val value: Int)

fun solve1(data: Pair<String, String>): Int? {
    val wire1 = data.first.split(",").map { parse(it) }
    val wire2 = data.second.split(",").map { parse(it) }

    val points1 = toPoints(wire1)
    val points2 = toPoints(wire2)


    return points1.intersect(points2)
        .filter { it != Point(0,0) }
        .map { calcManhattanDistance(it) }
        .min()

}

fun calcManhattanDistance(point: Point): Int {
    return abs(point.x) + abs(point.y)
}

private fun toPoints(wire1: List<Step>): List<Point> {
    val lst = mutableListOf(Point(0,0))
    for (step in wire1) {
        val direction = step.direction
        for (i in IntRange(1, step.value)) {
            val last = lst.last()
            lst.add(moveOne(last, direction))
        }
    }
    return lst.toList()
}

fun moveOne(point: Point, direction: String): Point {
    return when (direction) {
        "R" -> {
            Point(point.x + 1, point.y)
        }
        "L" -> {
            Point(point.x - 1, point.y)
        }
        "U" -> {
            Point(point.x, point.y + 1)
        }
        "D" -> {
            Point(point.x, point.y - 1)
        }
        else -> throw RuntimeException()
    }
}

fun parse(step: String): Step {
    return Step(
        step[0].toString(),
        Integer
            .parseInt(
                step.subSequence(1, step.length)
                    .toString()
            )
    )
}

fun solve2(data: String): Int {
    return 1
}