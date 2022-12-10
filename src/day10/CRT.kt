package day10

class CPU(val crt: CRT?) {
    private var x = 1
    private var cycle = 0
    private val interestingSignalStrengths = mutableListOf<Int>()

    fun executeCommand(command: String) {
        if (command.startsWith("noop")) {
            nextCycle()
        } else {
            nextCycle()
            nextCycle()
            x += command.removePrefix("addx ").toInt()
        }
    }

    fun sumInterestingSignals(): Int {
        return interestingSignalStrengths.sum()
    }

    private fun nextCycle() {
        crt?.update(cycle, x)
        cycle++
        if (isInterestingCycle()) {
            interestingSignalStrengths.add(cycle * x)
        }
    }

    private fun isInterestingCycle(): Boolean {
        return cycle % 40 == 20
    }
}

class CRT {
    private val pixels = mutableListOf<String>()

    init {
        repeat(HEIGHT) {
            pixels.add(".".repeat(WIDTH))
        }
    }

    fun print() {
        for (line in pixels) {
            println(line)
        }
    }

    fun update(cycle: Int, x: Int) {
        val column = cycle % WIDTH
        if (column in (x - 1)..(x + 1)) {
            val row = cycle / WIDTH
            val line = pixels[row]
            pixels[row] = line.substring(0 until column) + "#" + line.substring(column + 1)
        }
    }

    companion object {
        private const val WIDTH = 40
        private const val HEIGHT = 6
    }
}

fun solvePuzzle1(input: List<String>) {
    val cpu = CPU(null)

    for (command in input) {
        cpu.executeCommand(command)
    }

    val result = cpu.sumInterestingSignals()

    println(result)
}

fun solvePuzzle2(input: List<String>) {
    val cpu = CPU(CRT())

    for (command in input) {
        cpu.executeCommand(command)
    }

    cpu.crt?.print() ?: throw IllegalStateException("how did we even get here?")
}
