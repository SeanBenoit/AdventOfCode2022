package day5

import day5.Crates.Model.CRATEMOVER_9000
import day5.Crates.Model.CRATEMOVER_9001
import java.util.*

class Crates(private val numberOfStacks: Int, val model: Model) {
    enum class Model {
        CRATEMOVER_9000,
        CRATEMOVER_9001,
    }

    private val stacks: List<Stack<String>>

    init {
        val initStacks = mutableListOf<Stack<String>>()

        repeat(numberOfStacks) {
            initStacks.add(Stack())
        }

        stacks = initStacks
    }

    private fun move(amount: Int, src: Int, dest: Int) {
        when (model) {
            CRATEMOVER_9000 -> {
                repeat(amount) {
                    val item = stacks[src - 1].pop()
                    stacks[dest - 1].push(item)
                }
            }
            CRATEMOVER_9001 -> {
                val tempStack = Stack<String>()
                repeat(amount) {
                    val item = stacks[src - 1].pop()
                    tempStack.push(item)
                }
                repeat(amount) {
                    val item = tempStack.pop()
                    stacks[dest - 1].push(item)
                }
            }
        }
    }

    fun followInstructions(instructions: List<String>) {
        val regex = Regex("move (?<amount>\\d+) from (?<src>\\d+) to (?<dest>\\d+)")

        for (instruction in instructions) {
            val parsed = regex.matchEntire(instruction)?.groups
                    ?: throw IllegalArgumentException("Failed to parse instruction")

            val amount = parsed["amount"]?.value?.toInt()
                    ?: throw IllegalArgumentException("Failed to parse instruction")
            val src = parsed["src"]?.value?.toInt()
                    ?: throw IllegalArgumentException("Failed to parse instruction")
            val dest = parsed["dest"]?.value?.toInt()
                    ?: throw IllegalArgumentException("Failed to parse instruction")

            move(amount, src, dest)
        }
    }

    fun getTopOfStacks(): String {
        return stacks.joinToString(separator = "") { it.peek() }
    }

    companion object {
        fun constructStacks(input: List<String>, model: Model): Crates {
            val numberOfStacks = input.last()
                    .trim()
                    .split("   ")
                    .size

            val crates = Crates(numberOfStacks, model)

            // Parse the stacks of crates from the bottom up because LIFO order
            val crateInput = input.dropLast(1) // Remove the row labelling the stacks
                    .reversed()
            for (row in crateInput) {
                val rawCrates = row.chunked(4) // Crates are 4 chars including the delimiting space
                for ((index, rawCrate) in rawCrates.withIndex()) {
                    if (rawCrate.isBlank()) continue

                    val label = rawCrate.trim('[', ']', ' ')

                    crates.stacks[index].push(label)
                }
            }

            return crates
        }
    }
}

fun solvePuzzle1(input: List<String>) {
    val separatorIndex = input.indexOf("")
    val startingState = input.take(separatorIndex)
    val instructions = input.drop(separatorIndex + 1)

    val crates = Crates.constructStacks(startingState, CRATEMOVER_9000)

    crates.followInstructions(instructions)

    val result = crates.getTopOfStacks()

    println(result)
}

fun solvePuzzle2(input: List<String>) {
    val separatorIndex = input.indexOf("")
    val startingState = input.take(separatorIndex)
    val instructions = input.drop(separatorIndex + 1)

    val crates = Crates.constructStacks(startingState, CRATEMOVER_9001)

    crates.followInstructions(instructions)

    val result = crates.getTopOfStacks()

    println(result)
}
