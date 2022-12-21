package day9

import utils.minus
import utils.plus
import kotlin.math.abs

class RopeSegment(
        private val next: RopeSegment?
) {
    private var position = Pair(0, 0)
    private val positionsVisited = mutableSetOf(position)

    fun countPositionsVisited(): Int {
        return positionsVisited.size
    }

    fun move(instruction: String) {
        val moveVector = getMoveVector(instruction)
        val distance = instruction.split(" ")[1].toInt()

        repeat(distance) {
            position += moveVector

            next?.update(position)
        }
    }

    private fun update(nextPos: Pair<Int, Int>) {
        val diffVector = nextPos - position
        val needToMove = diffVector.maxComponent() > 1

        if (needToMove) {
            val moveVector = diffVector.normalize()
            position += moveVector
            positionsVisited.add(position)
        }

        next?.update(position)
    }

    private fun getMoveVector(instruction: String): Pair<Int, Int> {
        return when (instruction.first()) {
            'U' -> Pair(0, 1)
            'D' -> Pair(0, -1)
            'L' -> Pair(-1, 0)
            'R' -> Pair(1, 0)
            else -> throw IllegalArgumentException("instruction must start with U, D, L, or R")
        }
    }

    companion object {
        fun buildRope(length: Int): Pair<RopeSegment, RopeSegment> {
            val tail = RopeSegment(null)
            var head = tail
            repeat(length - 1) {
                head = RopeSegment(head)
            }

            return Pair(head, tail)
        }
    }
}

fun Pair<Int, Int>.maxComponent(): Int {
    return maxOf(abs(first), abs(second))
}

fun Pair<Int, Int>.normalize(): Pair<Int, Int> {
    return Pair(first.coerceIn(-1..1), second.coerceIn(-1..1))
}

fun solvePuzzle1(input: List<String>) {
    val (head, tail) = RopeSegment.buildRope(2)

    for (instruction in input) {
        head.move(instruction)
    }

    val result = tail.countPositionsVisited()

    println(result)
}

fun solvePuzzle2(input: List<String>) {
    val (head, tail) = RopeSegment.buildRope(10)

    for (instruction in input) {
        head.move(instruction)
    }

    val result = tail.countPositionsVisited()

    println(result)
}
