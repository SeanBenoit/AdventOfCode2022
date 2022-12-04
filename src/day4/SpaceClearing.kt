package day4

class SpaceAssignment(
        val start: Int,
        val end: Int,
) {
    init {
        require(start <= end)
    }

    fun overlapsFully(other: SpaceAssignment): Boolean {
        return (start <= other.start && end >= other.end) || (other.start <= start && other.end >= end)
    }

    fun overlaps(other: SpaceAssignment): Boolean {
        val thisContainsOther = other.start in start..end || other.end in start..end
        val otherContainsThis = start in other.start..other.end || end in other.start..other.end
        return thisContainsOther || otherContainsThis
    }

    companion object {
        fun fromString(input: String): SpaceAssignment {
            val (start, end) = input.split("-")

            return SpaceAssignment(start.toInt(), end.toInt())
        }
    }
}

fun solvePuzzle1(input: List<String>) {
    val assignments = input.map {
        val foo = it.split(",")
        Pair(
                SpaceAssignment.fromString(foo[0]),
                SpaceAssignment.fromString(foo[1]),
        )
    }

    val fullOverlaps = assignments.count {
        it.first.overlapsFully(it.second)
    }

    println(fullOverlaps)
}

fun solvePuzzle2(input: List<String>) {
    val assignments = input.map {
        val foo = it.split(",")
        Pair(
                SpaceAssignment.fromString(foo[0]),
                SpaceAssignment.fromString(foo[1]),
        )
    }

    val overlaps = assignments.count {
        it.first.overlaps(it.second)
    }

    println(overlaps)
}
