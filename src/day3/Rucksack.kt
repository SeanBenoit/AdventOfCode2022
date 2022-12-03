package day3

class Rucksack private constructor(
        private val firstCompartment: String,
        private val secondCompartment: String,
) {
    fun commonItems(): Set<Char> {
        return firstCompartment.toList().intersect(secondCompartment.toList())
    }

    companion object {
        fun fromString(input: String): Rucksack {
            require(input.length % 2 == 0)

            val halfLength = input.length / 2
            val firstHalf = input.take(halfLength)
            val secondHalf = input.drop(halfLength)

            return Rucksack(firstHalf, secondHalf)
        }
    }
}

fun Char.priority(): Int {
    val offset = if (isLowerCase()) {
        'a'.toInt() - 1
    } else {
        'A'.toInt() - 27
    }
    return toInt() - offset
}

fun solvePuzzle1(input: List<String>) {
    val rucksacks = input.map { Rucksack.fromString(it) }

    val commonItems = rucksacks.map { it.commonItems() }

    val priorities = commonItems.map { it.single().priority() }

    val result = priorities.sum()

    println(result)
}

class ElfGroup(
        private val firstPack: List<Char>,
        private val secondPack: List<Char>,
        private val thirdPack: List<Char>,
) {
    constructor(packList: List<String>) : this(packList[0].toList(), packList[1].toList(), packList[2].toList())

    fun getBadge(): Char {
        return firstPack.intersect(secondPack)
                .intersect(thirdPack)
                .single()
    }
}

fun solvePuzzle2(input: List<String>) {
    val groups = input.chunked(3)
            .map { ElfGroup(it) }

    val badges = groups.map { it.getBadge() }

    val priorities = badges.map { it.priority() }

    val result = priorities.sum()

    println(result)
}
