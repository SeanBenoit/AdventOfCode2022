package day1

fun solvePuzzle1(input: List<String>) {
    val elves = separateElves(input)

    val calorieCounts = elves.map { it.sum() }

    val maxCalories = calorieCounts.max()

    println(maxCalories)
}

fun solvePuzzle2(input: List<String>) {
    val elves = separateElves(input)

    val calorieCounts = elves.map { it.sum() }

    val topThreeSum = calorieCounts
            .asSequence()
            .sortedDescending()
            .take(3)
            .sum()

    println(topThreeSum)
}

fun separateElves(input: List<String>): List<List<Int>> {
    val output: MutableList<MutableList<Int>> = mutableListOf()

    var nextElf = mutableListOf<Int>()
    for (inputString in input) {
        if (inputString.isEmpty()) {
            output.add(nextElf)
            nextElf = mutableListOf()
            continue
        }

        nextElf.add(inputString.toInt())
    }

    if (nextElf.isNotEmpty()) {
        output.add(nextElf)
    }

    return output
}
