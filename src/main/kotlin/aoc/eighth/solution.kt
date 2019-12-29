package aoc.eighth

data class Layer(val rows: List<String>)

fun solve1(data: String, wide: Int, tall: Int): Int {

    val layers = parseToLayers(data, wide, tall)

    val layerWithFewestZeros = findLayerWithFewestZeros(layers)

    return multiplyNumberOfOnesWithNumberOfTwos(layerWithFewestZeros)
}

fun solve2(data: String, wide: Int, tall: Int): List<String> {

    val layers = parseToLayers(data, wide, tall).map { it.rows.reduce { acc, s -> acc + s } }
    var tmpLayer = layers.first()

    for ( layer in layers) {
        for ( i in 0 until layer.length) {
            if (tmpLayer[i] == '2' && layer[i] != '2') {
                tmpLayer = replace(tmpLayer, layer, i)
            }
        }
    }

    return takeNextLayer(tmpLayer, wide, tall).first.rows
}

fun replace(tmpLayer: String, layer: String, i: Int): String {
    return tmpLayer.substring(0,i) + layer[i] + tmpLayer.substring(i + 1)
}

fun multiplyNumberOfOnesWithNumberOfTwos(layer: Layer): Int {
    return numberOfSymbol(layer, '1') * numberOfSymbol(layer, '2')
}

fun numberOfSymbol(layer: Layer, symbol: Char): Int {
    return layer.rows
        .reduce { acc: String, s: String -> acc + s }
        .count { it == symbol }

}

fun findLayerWithFewestZeros(layers: List<Layer>): Layer {
    return layers.map { Pair(it, it.rows) }
        .map { Pair(it.first, it.second.reduce { acc, s -> acc + s }) }
        .map { Pair(it.first, it.second.count { c -> c == '0' }) }
        .sortedBy { it.second }
        .first()
        .first
}

private fun parseToLayers(
    data: String,
    wide: Int,
    tall: Int
): List<Layer> {
    var tmpData = data
    val layers = mutableListOf<Layer>()

    while (tmpData.isNotEmpty()) {
        val element = takeNextLayer(tmpData, wide, tall)
        layers.add(element.first)
        tmpData = element.second
    }

    return layers
}

fun takeNextLayer(picture: String, wide: Int, tall: Int): Pair<Layer, String> {

    var rawLayer = picture.take(wide * tall)

    val layerList = mutableListOf<String>()

    while (rawLayer.isNotEmpty()) {
        val row = rawLayer.take(wide)
        layerList.add(row)
        rawLayer = rawLayer.removePrefix(row)

    }


    return Pair(Layer(layerList), picture.drop(wide * tall))
}
