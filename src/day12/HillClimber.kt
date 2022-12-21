package day12

import utils.manhattanDistance
import java.util.*
import kotlin.Int.Companion.MAX_VALUE
import kotlin.math.abs
import kotlin.math.min

private const val START_CHAR = 'S'
private const val GOAL_CHAR = 'E'

class HillClimber(
        gridInput: List<String>,
) {
    class HillNode(
            val height: Int,
            val position: Pair<Int, Int>,
            val isGoal: Boolean,
            var predictedShortestPathLength: Int = MAX_VALUE,
    ) {
        private val neighbours = mutableSetOf<HillNode>()

        /**
         * Mutation needs to happen via [addNeighbour].
         */
        fun getNeighbours(): Set<HillNode> {
            return neighbours
        }

        // Manhattan distance to specified goal position. Used for A* heuristic.
        fun heuristicToGoal(goalPosition: Pair<Int, Int>): Int {
            return position.manhattanDistance(goalPosition)
        }

        // Adds neighbour both ways
        fun addNeighbour(neighbour: HillNode) {
            addNeighbourBack(neighbour)
            neighbour.addNeighbourBack(this)
        }

        // Adds neighbour one-way to avoid looping neighbours infinitely.
        private fun addNeighbourBack(neighbour: HillNode) {
            if (height + 1 < neighbour.height) return // neighbour is too tall to reach
            neighbours.add(neighbour)
        }
    }

    private lateinit var startingHill: HillNode
    private val goalPosition: Pair<Int, Int>

    private val grid = mutableListOf<MutableList<HillNode>>()

    init {
        // Initialize the grid
        var initGoalPosition: Pair<Int, Int>? = null

        for ((rowIndex, row) in gridInput.withIndex()) {
            val newRow = mutableListOf<HillNode>()

            for ((columnIndex, tileChar) in row.withIndex()) {
                // Create new hill
                val newHill = HillNode(
                        height = tileChar.toHeight(),
                        position = Pair(rowIndex, columnIndex),
                        isGoal = tileChar == GOAL_CHAR
                )
                newRow.add(newHill)

                // Initialize starting hill
                if (tileChar == START_CHAR) {
                    startingHill = newHill
                }

                // Initialize goal position
                if (newHill.isGoal) {
                    initGoalPosition = newHill.position
                }

                // Connect neighbours
                if (rowIndex > 0) grid[rowIndex - 1][columnIndex].addNeighbour(newHill)
                if (columnIndex > 0) newRow[columnIndex - 1].addNeighbour(newHill)
            }

            grid.add(newRow)
        }

        checkNotNull(initGoalPosition) { "input doesn't contain goal position" }
        goalPosition = initGoalPosition

        startingHill.predictedShortestPathLength = startingHill.heuristicToGoal(goalPosition)
    }

    fun shortestPathLength(): Int {
        return pathToGoal()!!.size - 1
    }

    private fun pathToGoal(): List<Pair<Int, Int>>? {
        val nodesToExplore = PriorityQueue<HillNode> { a, b ->
            a.predictedShortestPathLength - b.predictedShortestPathLength
        }
        nodesToExplore.add(startingHill)

        val cameFrom = mutableMapOf<HillNode, HillNode>()

        val bestScoreSoFar = mutableMapOf(
                startingHill to 0
        )

        while (nodesToExplore.isNotEmpty()) {
            val currentHill = nodesToExplore.poll()

            // Found the goal, return the path.
            if (currentHill.position == goalPosition) {
                return rebuildPath(cameFrom, currentHill)
            }

            for (neighbour in currentHill.getNeighbours()) {
                val scoreForNeighbour = bestScoreSoFar.getValue(currentHill) + 1

                // Skip if we have a better path to this neighbour
                if (scoreForNeighbour >= bestScoreSoFar.getOrDefault(neighbour, MAX_VALUE)) continue

                // Best path for this neighbour so far, record it.
                cameFrom[neighbour] = currentHill
                bestScoreSoFar[neighbour] = scoreForNeighbour
                neighbour.predictedShortestPathLength = scoreForNeighbour +
                        neighbour.heuristicToGoal(goalPosition)

                if (!nodesToExplore.contains(neighbour)) nodesToExplore.add(neighbour)
            }
        }

        return null
    }

    private fun rebuildPath(
            cameFrom: Map<HillNode, HillNode>,
            endHill: HillNode
    ): List<Pair<Int, Int>> {
        val path = mutableListOf(endHill.position)

        var current = endHill
        while (cameFrom.containsKey(current)) {
            current = cameFrom.getValue(current)
            path.add(current.position)
        }

        return path.reversed()
    }

    fun shortestPathFromAnyStart(): Int {
        var shortestPathSoFar = shortestPathLength()
        for (hill in grid.flatten()) {
            if (hill.height != START_CHAR.toHeight()) continue

            // This is a bit jank.
            startingHill = hill
            val pathFromHill = pathToGoal()?.size ?: continue
            shortestPathSoFar = min(shortestPathSoFar, pathFromHill - 1)
        }

        return shortestPathSoFar
    }
}

fun Char.toHeight(): Int {
    return when (this) {
        START_CHAR -> 'a'.toHeight()
        GOAL_CHAR -> 'z'.toHeight()
        else -> this.toInt()
    }
}

fun solvePuzzle1(input: List<String>) {
    val hillClimber = HillClimber(input)

    val result = hillClimber.shortestPathLength()

    println(result)
}

fun solvePuzzle2(input: List<String>) {
    val hillClimber = HillClimber(input)

    val result = hillClimber.shortestPathFromAnyStart()

    println(result)
}
