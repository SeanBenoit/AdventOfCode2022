package day6

fun firstUniqueString(input: String, length: Int): Int {
    val lengthIndex = length - 1
    require(input.length > lengthIndex) { "input is too short!" }

    for (i in lengthIndex until input.length) {
        val lastNChars = input.substring(i - lengthIndex, i + 1)
        val uniqueChars = lastNChars.toSet()
        if (uniqueChars.size == lastNChars.length) {
            return i + 1
        }
    }

    throw IllegalArgumentException("input does not contain start of packet marker")
}

fun findStartOfPacketIndex(input: String): Int {
    return firstUniqueString(input, 4)
}

fun findStartOfMessageIndex(input: String): Int {
    return firstUniqueString(input, 14)
}

fun solvePuzzle1(input: List<String>) {
    val result = findStartOfPacketIndex(input[0])

    println(result)
}

fun solvePuzzle2(input: List<String>) {
    val result = findStartOfMessageIndex(input[0])

    println(result)
}
