package day11

import java.math.BigInteger
import java.util.*

class Monkey(
        startingItems: List<BigInteger>,
        private val worryReductionFactor: BigInteger,
        private val operation: (BigInteger) -> BigInteger,
        private val test: (BigInteger) -> Boolean,
        private val targetTrue: Int,
        private val targetFalse: Int,
) {
    private val items: Queue<BigInteger>

    init {
        items = LinkedList(startingItems)
    }

    var itemsInspected = 0

    fun processItems(): List<Pair<Int, BigInteger>> {
        val itemsSent = mutableListOf<Pair<Int, BigInteger>>()
        while (items.isNotEmpty()) {
            var item = items.remove()
            itemsInspected++

            item = operation(item)

            item /= worryReductionFactor

            if (test(item)) {
                itemsSent.add(Pair(targetTrue, item))
            } else {
                itemsSent.add(Pair(targetFalse, item))
            }
        }

        return itemsSent
    }

    fun addItem(item: BigInteger) {
        items.add(item)
    }

    companion object {
        fun fromString(input: List<String>, worryReductionFactor: BigInteger): Monkey {
            val startingItems = input[1].removePrefix("Starting items: ")
                    .split(", ")
                    .map { it.toBigInteger() }

            val operationParts = input[2].removePrefix("Operation: new = old ")
                    .split(" ")
            val operationValue = operationParts[1].toBigIntegerOrNull()
            val operation: (BigInteger) -> BigInteger = when (operationParts[0]) {
                "+" -> { value: BigInteger -> value + (operationValue ?: value) }
                "*" -> { value: BigInteger -> value * (operationValue ?: value) }
                else -> throw UnsupportedOperationException("Unrecognized operation: ${operationParts[0]}")
            }

            val testValue = input[3].removePrefix("Test: divisible by ").toBigInteger()
            val test = { value: BigInteger -> value % testValue == 0.toBigInteger() }

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

fun engageInMonkeyBusiness(input: List<String>, worryReductionFactor: BigInteger, rounds: Int): Int {
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
    val result = engageInMonkeyBusiness(input, 3.toBigInteger(), 20)

    println(result)
}

fun solvePuzzle2(input: List<String>) {
    val result = engageInMonkeyBusiness(input, 1.toBigInteger(), 10_000)

    println(result)
}
