package day8

fun parseToHeightGrid(input: List<String>): List<List<Int>> {
    return input.map { rawRow ->
        rawRow.chunked(1)
                .map { it.toInt() }
    }
}

fun countVisible(heightGrid: List<List<Int>>): Int {
    val visibleTrees = mutableSetOf<Pair<Int, Int>>()

    // Check visibility for rows
    for ((index, row) in heightGrid.withIndex()) {
        val indexedRow = row.withIndex()

        val visibleFromLeft = getVisibleInLine(indexedRow)
                .map { index to it }
        visibleTrees.addAll(visibleFromLeft)

        val visibleFromRight = getVisibleInLine(indexedRow.reversed())
                .map { index to it }
        visibleTrees.addAll(visibleFromRight)
    }

    // Check visibility for columns
    for (index in heightGrid[0].indices) {
        val indexedColumn = heightGrid.getColumn(index).withIndex()

        val visibleFromTop = getVisibleInLine(indexedColumn)
                .map { it to index }
        visibleTrees.addAll(visibleFromTop)

        val visibleFromBottom = getVisibleInLine(indexedColumn.reversed())
                .map { it to index }
        visibleTrees.addAll(visibleFromBottom)
    }

    return visibleTrees.size
}

fun getVisibleInLine(line: Iterable<IndexedValue<Int>>): List<Int> {
    val visibleTrees = mutableListOf<Int>()

    var tallestSoFar = -1
    for ((index, tree) in line) {
        if (tree > tallestSoFar) {
            visibleTrees.add(index)
            tallestSoFar = tree
        }
    }
    return visibleTrees
}

fun <T> List<List<T>>.getColumn(i: Int): List<T> {
    return map { it[i] }
}

fun getScenicScore(heightGrid: List<List<Int>>, location: Pair<Int, Int>): Int {
    val maxX = heightGrid[0].lastIndex
    val maxY = heightGrid.lastIndex
    val locationIsOnEdge = location.first == 0 ||
            location.first == maxX ||
            location.second == 0 ||
            location.second == maxY
    if (locationIsOnEdge) return 0

    val row = heightGrid[location.second]
    val column = heightGrid.getColumn(location.first)

    val height = heightGrid[location.second][location.first]

    val lineLeft = row.slice(location.first - 1 downTo 0)
    val lineRight = row.slice(location.first + 1..maxX)
    val lineUp = column.slice(location.second - 1 downTo 0)
    val lineDown = column.slice(location.second + 1..maxY)

    val viewingDistanceLeft = getViewingDistance(height, lineLeft)
    val viewingDistanceRight = getViewingDistance(height, lineRight)
    val viewingDistanceUp = getViewingDistance(height, lineUp)
    val viewingDistanceDown = getViewingDistance(height, lineDown)

    return viewingDistanceLeft * viewingDistanceRight * viewingDistanceUp * viewingDistanceDown
}

fun getViewingDistance(height: Int, line: List<Int>): Int {
    val distance = line.indexOfFirst { it >= height }
    return if (distance == -1) {
        line.size
    } else {
        distance + 1
    }
}

fun solvePuzzle1(input: List<String>) {
    val heightGrid = parseToHeightGrid(input)

    val result = countVisible(heightGrid)

    println(result)
}

fun solvePuzzle2(input: List<String>) {
    val heightGrid = parseToHeightGrid(input)

    var highestScoreSoFar = 0
    for (y in heightGrid.indices) {
        for (x in heightGrid[y].indices) {
            val scenicScore = getScenicScore(heightGrid, Pair(x, y))
            highestScoreSoFar = maxOf(highestScoreSoFar, scenicScore)
        }
    }

    println(highestScoreSoFar)
}
