package aoc.third

import java.lang.RuntimeException
import java.util.*
import kotlin.math.abs

data class Point(val x: Int, val y: Int, val counter: Int = 0) {
    override fun equals(other: Any?): Boolean {
        return if (other is Point) {
            x == other.x && y == other.y
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return Objects.hashCode(x) + Objects.hashCode(y)
    }
}

data class Step(val direction: String, val value: Int)

fun solve1(data: Pair<String, String>): Int? {
    return getIntersections(data)
        .map { calcManhattanDistance(it) }
        .min()
}

fun getIntersections(data: Pair<String, String>): List<Point> {
    val points1 = toPoints(data.first)
    val points2 = toPoints(data.second)

    return points1.intersect(points2)
        .filter { it != Point(0, 0) }
}

private fun toPoints(data: String) =
    toPoints(data.split(",").map { parse(it) })

fun calcManhattanDistance(point: Point): Int {
    return abs(point.x) + abs(point.y)
}

private fun toPoints(wire1: List<Step>): List<Point> {
    val lst = mutableListOf(Point(0, 0))
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
            Point(point.x + 1, point.y, point.counter + 1)
        }
        "L" -> {
            Point(point.x - 1, point.y, point.counter + 1)
        }
        "U" -> {
            Point(point.x, point.y + 1, point.counter + 1)
        }
        "D" -> {
            Point(point.x, point.y - 1, point.counter + 1)
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

fun solve2(data: Pair<String, String>): Int? {
    val points1 = toPoints(data.first)
    val points2 = toPoints(data.second)

    val intersection = getIntersections(data)

    return intersection.map { point ->
        points1.first { p -> p == point }.counter +
                points2.first { p -> p == point }.counter
    }
        .min()


}