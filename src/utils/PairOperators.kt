package utils

import kotlin.math.abs

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(this.first + other.first, this.second + other.second)
}

operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(this.first - other.first, this.second - other.second)
}

fun Pair<Int, Int>.manhattanDistance(other: Pair<Int, Int>): Int {
    return abs(first - other.first) + abs(second - other.second)
}