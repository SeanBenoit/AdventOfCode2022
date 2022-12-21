package day14

import kotlin.math.max
import kotlin.math.min

class RegolithReservoir {
    enum class Substance {
        AIR,
        ROCK,
        SAND,
    }

    private fun Substance.isSolid(): Boolean {
        return this in listOf(Substance.ROCK, Substance.SAND)
    }

    private val sandSource = Pair(500, 0)

    private var floorRow = 0

    // Coordinates are [column, row]
    private val grid: MutableMap<Int, MutableMap<Int, Substance>> = mutableMapOf()

    private var unitsOfSand = 0

    fun buildRocks(input: List<String>) {
        for (string in input) {
            val vertices = string.split(" -> ")
                .map {
                    val (first, second) = it.split(",")
                    Pair(first.toInt(), second.toInt())
                }

            var lineStart = vertices.first()
            for (vertex in vertices.drop(1)) {
                if (lineStart.first == vertex.first) {
                    val columnStart = min(lineStart.second, vertex.second)
                    val columnEnd = max(lineStart.second, vertex.second)
                    buildColumn(lineStart.first, columnStart, columnEnd)
                    floorRow = max(floorRow, columnEnd)
                } else {
                    val rowStart = min(lineStart.first, vertex.first)
                    val rowEnd = max(lineStart.first, vertex.first)
                    buildRow(lineStart.second, rowStart, rowEnd)
                    floorRow = max(floorRow, lineStart.second)
                }

                lineStart = vertex
            }
        }
        floorRow += 2
    }

    fun fillWithSand() {
        while (true) {
            // There's nothing in the column of the sand source so we're done.
            if (!grid.containsKey(sandSource.first)) return

            // Add a piece of sand
            var dropFrom = sandSource
            dropSand@while (true) {
                // Get top of the column the sand is falling in.
                // If the column is empty, we're done.
                val topOfColumn = getTopOfColumn(dropFrom) ?: return

                val leftColumn = dropFrom.first - 1
                if (grid[leftColumn]?.get(topOfColumn)?.isSolid() != true) {
                    // Empty space down and left so drop there
                    dropFrom = Pair(leftColumn, topOfColumn)
                    continue@dropSand
                }

                val rightColumn = dropFrom.first + 1
                if (grid[rightColumn]?.get(topOfColumn)?.isSolid() != true) {
                    // Empty space down and right so drop there
                    dropFrom = Pair(rightColumn, topOfColumn)
                    continue@dropSand
                }

                // All spaces below are filled already, place sand here.
                val column = grid.getOrPut(dropFrom.first) { mutableMapOf() }
                column[topOfColumn - 1] = Substance.SAND
                break@dropSand
            }
            unitsOfSand++
        }
    }

    fun fillWithSandAndFloor() {
        while (grid[sandSource.first]?.get(sandSource.second)?.isSolid() != true) {
            // Add a piece of sand
            var dropFrom = sandSource
            dropSand@while (true) {
                // Get top of the column the sand is falling in.
                // If the column is empty, it hits the floor at floorRow.
                val topOfColumn = getTopOfColumn(dropFrom) ?: floorRow

                val leftColumn = dropFrom.first - 1
                if (topOfColumn < floorRow
                    && grid[leftColumn]?.get(topOfColumn)?.isSolid() != true
                ) {
                    // Empty space down and left so drop there
                    dropFrom = Pair(leftColumn, topOfColumn)
                    continue@dropSand
                }

                val rightColumn = dropFrom.first + 1
                if (topOfColumn < floorRow
                    && grid[rightColumn]?.get(topOfColumn)?.isSolid() != true
                ) {
                    // Empty space down and right so drop there
                    dropFrom = Pair(rightColumn, topOfColumn)
                    continue@dropSand
                }

                // All spaces below are filled already, place sand here.
                val column = grid.getOrPut(dropFrom.first) { mutableMapOf() }
                column[topOfColumn - 1] = Substance.SAND
                break@dropSand
            }
            unitsOfSand++
        }
    }

    fun countSand(): Int {
        return unitsOfSand
    }

    private fun buildColumn(column: Int, start: Int, end: Int) {
        val columnMap = grid.getOrPut(column) { mutableMapOf() }

        for (index in start..end) {
            columnMap[index] = Substance.ROCK
        }
    }

    private fun buildRow(row: Int, start: Int, end: Int) {
        for (index in start..end) {
            val column = grid.getOrPut(index) { mutableMapOf() }
            column[row] = Substance.ROCK
        }
    }

    // Returns the row number of the highest solid below the specified point
    // or null if it's empty.
    private fun getTopOfColumn(dropFrom: Pair<Int, Int>): Int? {
        return grid[dropFrom.first]?.keys
            // Filter out things above the drop point
            ?.filter { it > dropFrom.second }
            // Return the row number of the top thing.
            ?.min()
    }
}

fun solvePuzzle1(input: List<String>) {
    val reservoir = RegolithReservoir()

    reservoir.buildRocks(input)

    reservoir.fillWithSand()

    val result = reservoir.countSand()

    println(result)
}

fun solvePuzzle2(input: List<String>) {
    val reservoir = RegolithReservoir()

    reservoir.buildRocks(input)

    reservoir.fillWithSandAndFloor()

    val result = reservoir.countSand()

    println(result)
}