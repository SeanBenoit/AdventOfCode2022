package day13

import java.util.*

class DistressSignal(
    private val number: Int? = null,
    private val list: List<DistressSignal>? = null,
) {
    fun compareTo(other: DistressSignal): Int {
        return if (number != null) {
            if (other.number != null) {
                // Both ints
                number.compareTo(other.number)
            } else {
                // This is a number, other is a list
                listOf(this).compareTo(other.list!!)
            }
        } else if (other.list != null) {
            // Both are lists
            list!!.compareTo(other.list)
        } else {
            // This is a list, other is a number
            list!!.compareTo(listOf(other))
        }
    }

    companion object {
        fun fromString(input: String): DistressSignal {
            val digits = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
            val stack = Stack<MutableList<DistressSignal>>()
            var currentList = mutableListOf<DistressSignal>()

            // We always start with a list, so skip the opening '['
            var index = 1
            // Last element is always ']' so skip it
            while (index < input.lastIndex) {
                when {
                    input[index] == '[' -> {
                        stack.push(currentList)
                        currentList = mutableListOf()
                    }
                    input[index] in digits -> {
                        var currentNumber = 0
                        while (input[index] in digits) {
                            currentNumber *= 10
                            currentNumber += input[index].toString().toInt()
                            index++
                        }
                        currentList.add(DistressSignal(number = currentNumber))

                        index-- // rewind to the non-digit character again
                    }
                    input[index] == ']' -> {
                        // End current list
                        val tempList = DistressSignal(list = currentList)
                        currentList = stack.pop()
                        currentList.add(tempList)
                    }
                    input[index] == ',' -> {
                        // nothing to do here, just carry on with life.
                    }
                    else -> {
                        throw IllegalArgumentException(
                            "unrecognized character: ${input[index]}"
                        )
                    }
                }
                index++
            }

            require(stack.isEmpty()) {
                "current stack is not empty"
            }

            return DistressSignal(list = currentList)
        }
    }
}

fun List<DistressSignal>.compareTo(other: List<DistressSignal>): Int {
    var index = 0
    while (index < size) {
        if (index >= other.size) return 1

        val elementComparison = this[index].compareTo(other[index])
        if (elementComparison != 0) return elementComparison

        // Elements are the same so we continue
        index++
    }
    // Return 0 if lists are equal length
    if (size == other.size) return 0

    return -1
}

fun solvePuzzle1(input: List<String>) {
    val pairs = input.chunked(3)
        .mapIndexed { index, signalPair ->
            val first = DistressSignal.fromString(signalPair[0])
            val second = DistressSignal.fromString(signalPair[1])

            Pair(index + 1, first.compareTo(second))
        }

    val result = pairs.filter { it.second <= 0 }
        .sumBy { it.first }

    println(result)
}

fun solvePuzzle2(input: List<String>) {
    val allPackets = input.asSequence()
        .filter { it.isNotBlank() }
        .map { DistressSignal.fromString(it) }
        .toList()

    val firstDivider = DistressSignal.fromString("[[2]]")
    val secondDivider = DistressSignal.fromString("[[6]]")

    val packetsBeforeFirstDivider = allPackets.partition { it.compareTo(firstDivider) < 0 }
        .first
        .size

    // Add 1 because we're using 1-indexing
    val indexOfFirstDivider = packetsBeforeFirstDivider + 1

    val packetsBeforeSecondDivider = allPackets.partition { it.compareTo(secondDivider) < 0 }
        .first
        .size

    // Add 1 to account for first divider packet and a second 1 because 1-indexing
    val indexOfSecondDivider = packetsBeforeSecondDivider + 2

    val result = indexOfFirstDivider * indexOfSecondDivider

    println(result)
}