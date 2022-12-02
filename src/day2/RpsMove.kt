package day2

import day2.RpsMove.*
import day2.RpsResult.*

enum class RpsMove {
    ROCK,
    PAPER,
    SCISSORS,
    ;

    fun beats(opponentMove: RpsMove): Boolean {
        return (this == ROCK && opponentMove == SCISSORS) ||
                (this == PAPER && opponentMove == ROCK) ||
                (this == SCISSORS && opponentMove == PAPER)
    }

    fun getScore(): Int {
        return when (this) {
            ROCK -> 1
            PAPER -> 2
            SCISSORS -> 3
        }
    }
}

fun String.toRpsMove(): RpsMove {
    return when (this) {
        "A", "X" -> ROCK
        "B", "Y" -> PAPER
        "C", "Z" -> SCISSORS
        else -> throw IllegalArgumentException("Invalid input character: $this")
    }
}

data class RpsGame(
        val opponentMove: RpsMove,
        val playerMove: RpsMove,
) {
    fun getScore(): Int {
        val resultScore = when {
            opponentMove == playerMove -> 3 // draw case
            playerMove.beats(opponentMove) -> 6 // winning case
            else -> 0 // loss case
        }

        return playerMove.getScore() + resultScore
    }

    companion object {
        fun fromString(input: String): RpsGame {
            val (oppMove, playMove) = input.split(" ")

            return RpsGame(oppMove.toRpsMove(), playMove.toRpsMove())
        }
    }
}

fun solvePuzzle1(input: List<String>) {
    val games = input.map { RpsGame.fromString(it) }

    val score = games.sumBy { it.getScore() }

    println(score)
}

enum class RpsResult {
    LOSS,
    DRAW,
    WIN,
    ;

    fun getScore(): Int {
        return when (this) {
            LOSS -> 0
            DRAW -> 3
            WIN -> 6
        }
    }
}

fun String.toRpsResult(): RpsResult {
    return when (this) {
        "X" -> LOSS
        "Y" -> DRAW
        "Z" -> WIN
        else -> throw IllegalArgumentException("")
    }
}

data class RpsGameWithResult(
        val opponentMove: RpsMove,
        val result: RpsResult,
) {
    fun getScore(): Int {
        val playerMove = when (result) {
            DRAW -> opponentMove
            WIN -> {
                when (opponentMove) {
                    ROCK -> PAPER
                    PAPER -> SCISSORS
                    SCISSORS -> ROCK
                }
            }
            LOSS -> {
                when (opponentMove) {
                    ROCK -> SCISSORS
                    PAPER -> ROCK
                    SCISSORS -> PAPER
                }
            }
        }

        return result.getScore() + playerMove.getScore()
    }

    companion object {
        fun fromString(input: String): RpsGameWithResult {
            val inputParts = input.split(" ")

            val opponentMove = inputParts[0].toRpsMove()
            val result = inputParts[1].toRpsResult()

            return RpsGameWithResult(opponentMove, result)
        }
    }
}

fun solvePuzzle2(input: List<String>) {
    val games = input.map { RpsGameWithResult.fromString(it) }

    val score = games.sumBy { it.getScore() }

    println(score)
}
