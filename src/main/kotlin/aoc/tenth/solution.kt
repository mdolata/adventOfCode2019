package aoc.tenth

import java.lang.RuntimeException
import kotlin.math.abs

data class Point(val x: Int, val y: Int)
data class PlaceContent(val content: Content)

enum class Content {
    EMPTY, ASTEROID
}

fun solve1(data: String): Pair<Int, Pair<Int, Int>> {

    val board = data.split("\n")

    val places = parseRawBoard(board)

    val placesWithAsteroids = filterPlacesWithAsteroids(places)

    val map = placesWithAsteroids.map {
        Pair(it,
            findVisibleAsteroidsFromPlace(places,
                placesWithAsteroids.filter { p -> p.key != it.key }.map { p -> p.key },
                it.key
            )
        )
    }.map {
        Pair(it.first, it.second.filter { p -> p.value.content == Content.ASTEROID })
    }
//(33, (9, 5)) ==> (33, (5, 8))   => false
    customPrint2(map[0], board.first().length, board.size)

    val map2 = map.map {
        Pair(it.first, it.second.size)
    }.map {
        Pair(it.first.key, it.second)
    }

    customPrint(map2, board.first().length, board.size)

    val result = map2.map {
        Pair(Pair(it.first.x, it.first.y), it.second)
    }.map {
        Pair(it.second, it.first)
    }.sortedByDescending { it.first }
        .first()

    return result
}

fun customPrint2(
    asteroids: Pair<Map.Entry<Point, PlaceContent>, Map<Point, PlaceContent>>,
    length: Int,
    size: Int
) {
    for (i in 0 until length) {
        for (j in 0 until size) {
            val point = Point(j, i)
            val map = asteroids.second
            if (asteroids.first.key == point) {
                print("O")
            } else if (map.containsKey(point)) {
                print("#")
                // print(asteroids.find { it.first == point }!!.second)
            } else {
                print(".")
            }
        }
        println()
    }
}

fun customPrint(
    asteroids: List<Pair<Point, Int>>,
    length: Int,
    size: Int
) {
    for (i in 0 until length) {
        for (j in 0 until size) {
            val point = Point(j, i)
            if (asteroids.map { it.first }.contains(point)) {
                //     print("#")
                print(asteroids.find { it.first == point }!!.second)
            } else {
                print(".")
            }
        }
        println()
    }
}

fun findVisibleAsteroidsFromPlace(
    point: Map<Point, PlaceContent>,
    placesWithAsteroids: List<Point>,
    thisPlace: Point
): Map<Point, PlaceContent> {

    val visitedPlaces = mutableMapOf<Point, PlaceContent>()

    for (asteroid in placesWithAsteroids) {
        if (thatAsteroidWasVisited(asteroid, visitedPlaces)) {
            continue
        }

        visitedPlaces[asteroid] = PlaceContent(Content.ASTEROID)

        val pathToAsteroid = calcPath(thisPlace, asteroid)

        for (i in 0..1000) {
            val nextPlace = getNextPlace(asteroid, pathToAsteroid, i)

            if (point.containsKey(nextPlace)) {
                visitedPlaces[nextPlace] = PlaceContent(Content.EMPTY)
            } else {
                break
            }
        }
    }

    return visitedPlaces.toMap()
}

fun getNextPlace(asteroid: Point, path: Pair<Int, Int>, counter: Int): Point {
    var nextPlace = asteroid

    for (i in 0..counter) {
        nextPlace = next(nextPlace, path)
    }

    return nextPlace
}

fun next(asteroid: Point, path: Pair<Int, Int>): Point {
    return Point(asteroid.x + path.first, asteroid.y + path.second)
}

fun calcPath(thisPlace: Point, asteroid: Point): Pair<Int, Int> {
    val x = asteroid.x - thisPlace.x
    val y = asteroid.y - thisPlace.y

    val gcd = gcd(abs(x), abs(y))

    return if (gcd > 1) Pair(x / gcd, y / gcd)
    else if (x == 0) Pair(0, y/ (abs(y)))
    else if (y == 0) Pair(x/ (abs(x)), 0)
    else Pair(x, y)
}

fun gcd(n1: Int, n2: Int): Int {
    return if (n2 != 0)
        gcd(n2, n1 % n2)
    else
        n1
}

fun thatAsteroidWasVisited(asteroid: Point, visitedPlaces: Map<Point, PlaceContent>): Boolean {
    return visitedPlaces.containsKey(asteroid)
}

private fun filterPlacesWithAsteroids(places: Map<Point, PlaceContent>) =
    places.filter { it.value.content == Content.ASTEROID }

private fun parseRawBoard(board: List<String>): Map<Point, PlaceContent> {
    val places = mutableMapOf<Point, PlaceContent>()

    for (rowIndex in 0 until board.size) {
        for (placeIndex in 0 until board[rowIndex].length) {
            val point = Point(placeIndex, rowIndex)
            val content = when (board[rowIndex][placeIndex]) {
                '.' -> Content.EMPTY
                '#' -> Content.ASTEROID
                else -> throw RuntimeException("Not supported content type")
            }
            places[point] = PlaceContent(content)
        }
    }
    return places
}

fun solve2(data: String): String {
    return ""
}
