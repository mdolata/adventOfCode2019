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

