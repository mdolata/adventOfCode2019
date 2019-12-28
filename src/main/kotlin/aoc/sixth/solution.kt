package aoc.sixth

import java.util.*

data class Node(val name: String, val value: Int = 0)


fun solve1(data: String): Int {

    val queue: Queue<String> = ArrayDeque(data.split("\n").toMutableList())

    val map = mutableMapOf("COM" to Node("COM"))

    while (queue.isNotEmpty()) {
        val row = queue.poll()
        val objects = row.split(")")

        if (!map.containsKey(objects[0])) {
            queue.offer(row)
            continue
        }

        val node = map.getValue(objects[0])

        val newNode = Node(objects[1], node.value + 1)

        map.put(objects[1], newNode)
    }

    return map.map { it.value.value }.sum()

}

fun solve2(data: String): Int {
    val paths = data.split("\n")
    val yourPath = pathToCenter(paths, "YOU")
    val santaPath = pathToCenter(paths, "SAN")

    val commonPrefix = takeCommonPart(yourPath, santaPath)

    val yourWithoutPrefix = yourPath.removePrefix(commonPrefix)
    val santaWithoutPrefix = santaPath.removePrefix(commonPrefix)

    println(yourWithoutPrefix)
    println(santaWithoutPrefix)

    return santaWithoutPrefix.count { it == ')' } +
            yourWithoutPrefix.count { it == ')' }
}

fun takeCommonPart(path1: String, path2: String): String {
    return if (path1.startsWith(path2[0])) path1[0] + takeCommonPart(path1.substring(1), path2.substring(1))
    else ""
}

fun pathToCenter(data: List<String>, name: String): String {
    return if (name == "COM") "COM"
    else {
        val node = findNode(data, name)

        val split = node.split(")")

        pathToCenter(data, split[0]) + ")${split[1]}"
    }
}

fun findNode(data: List<String>, name: String): String {
    return data.find { it.endsWith(name) }.orEmpty()
}

