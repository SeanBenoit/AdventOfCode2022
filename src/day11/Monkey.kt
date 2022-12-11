package day11

import java.lang.Exception
import java.util.*
import kotlin.Long.Companion.MAX_VALUE
import kotlin.math.sqrt

// This is the least common multiple of the numbers each monkey is dividing by.
// Calculating every number mod this doesn't change the checks but keeps things small.
const val modFactor = 9_699_690L

class Monkey(
        startingItems: List<Long>,
        private val worryReductionFactor: Long,
        private val operation: (Long) -> Long,
        private val test: (Long) -> Boolean,
        private val targetTrue: Int,
        private val targetFalse: Int,
) {
    private val items: Queue<Long>

    init {
        items = LinkedList(startingItems)
    }

    var itemsInspected = 0L

    fun processItems(): List<Pair<Int, Long>> {
        val itemsSent = mutableListOf<Pair<Int, Long>>()
        while (items.isNotEmpty()) {
            var item = items.remove()
            itemsInspected++

            if (item > sqrt(MAX_VALUE.toDouble())) {
                println("well, fuck $targetTrue $targetFalse")
                throw Exception()
            }

            item = operation(item) % modFactor

            item /= worryReductionFactor

            if (test(item)) {
                itemsSent.add(Pair(targetTrue, item))
            } else {
                itemsSent.add(Pair(targetFalse, item))
            }
        }

        return itemsSent
    }

    fun addItem(item: Long) {
        items.add(item)
    }

    companion object {
        fun fromString(input: List<String>, worryReductionFactor: Long): Monkey {
            val startingItems = input[1].removePrefix("Starting items: ")
                    .split(", ")
                    .map { it.toLong() }

            val operationParts = input[2].removePrefix("Operation: new = old ")
                    .split(" ")
            val operationValue = operationParts[1].toLongOrNull()
            val operation: (Long) -> Long = when (operationParts[0]) {
                "+" -> { value: Long -> value + (operationValue ?: value) }
                "*" -> { value: Long -> value * (operationValue ?: value) }
                else -> throw UnsupportedOperationException("Unrecognized operation: ${operationParts[0]}")
            }

            val testValue = input[3].removePrefix("Test: divisible by ").toLong()
            val test = { value: Long -> value % testValue == 0.toLong() }

            val targetTrue = input[4].removePrefix("If true: throw to monkey ").toInt()

            val targetFalse = input[5].removePrefix("If false: throw to monkey ").toInt()

            return Monkey(
                    startingItems,
                    worryReductionFactor,
                    operation,
                    test,
                    targetTrue,
                    targetFalse,
            )
        }
    }
}

fun engageInMonkeyBusiness(input: List<String>, worryReductionFactor: Long, rounds: Int): Long {
    val monkeys = input.asSequence()
            .map { it.trim() }
            .chunked(7)
            .map { Monkey.fromString(it, worryReductionFactor) }
            .toList()

    repeat(rounds) {
        for (monkey in monkeys) {
            val itemsThrown = monkey.processItems()

            for (thrownItem in itemsThrown) {
                monkeys[thrownItem.first].addItem(thrownItem.second)
            }
        }
    }

    val mostActiveMonkeys = monkeys.map { it.itemsInspected }
            .sortedDescending()
            .take(2)

    return mostActiveMonkeys[0] * mostActiveMonkeys[1]
}

fun solvePuzzle1(input: List<String>) {
    val result = engageInMonkeyBusiness(input, 3L, 20)

    println(result)
}

fun solvePuzzle2(input: List<String>) {
    val result = engageInMonkeyBusiness(input, 1L, 10_000)

    println(result)
}
