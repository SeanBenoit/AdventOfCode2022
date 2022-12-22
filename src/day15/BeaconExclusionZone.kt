package day15

import utils.manhattanDistance
import kotlin.math.abs

class BeaconExclusionZone(
    val sensorLocation: Pair<Int, Int>,
    private val beaconLocation: Pair<Int, Int>,
) {
    val range = sensorLocation.manhattanDistance(beaconLocation)

    fun blockedPositionsInRow(y: Int): Set<Int> {
        if (!isInRangeOfRow(y)) return setOf()

        val x = sensorLocation.first
        val distanceToY = abs(y - sensorLocation.second)
        val delta = range - distanceToY

        return (x - delta..x + delta).toSet()
    }

    // Returns the x position of the beacon if it's in row [y]. Otherwise returns null.
    fun beaconXOrNull(y: Int): Int? {
        return if (beaconLocation.second == y) {
            beaconLocation.first
        } else {
            null
        }
    }

    fun isInRangeOf(x: Int, y: Int): Boolean {
        return sensorLocation.manhattanDistance(x to y) <= range
    }

    fun isInRangeOfRow(y: Int): Boolean {
        val distanceToY = abs(y - sensorLocation.second)
        return distanceToY <= range
    }

    companion object {
        private val inputRegex = Regex(
            "Sensor at x=(?<sensorX>-?\\d+), y=(?<sensorY>-?\\d+): " +
                    "closest beacon is at x=(?<beaconX>-?\\d+), y=(?<beaconY>-?\\d+)"
        )

        fun fromString(input: String): BeaconExclusionZone {
            val parsedInput = inputRegex.matchEntire(input)!!.groups

            val sensorLocation = Pair(
                parsedInput["sensorX"]!!.value.toInt(),
                parsedInput["sensorY"]!!.value.toInt()
            )

            val beaconLocation = Pair(
                parsedInput["beaconX"]!!.value.toInt(),
                parsedInput["beaconY"]!!.value.toInt()
            )

            return BeaconExclusionZone(sensorLocation, beaconLocation)
        }
    }
}

fun solvePuzzle1(input: List<String>) {
    val importantRow = 2_000_000

    val beaconExclusionZones = input.map { BeaconExclusionZone.fromString(it) }

    val blockedPositions = beaconExclusionZones.fold(
        setOf<Int>()
    ) { acc, beaconExclusionZone ->
        acc.union(beaconExclusionZone.blockedPositionsInRow(importantRow))
    }

    val beaconPositions = beaconExclusionZones.asSequence()
        .mapNotNull { it.beaconXOrNull(importantRow) }
        .toSet()

    val result = (blockedPositions - beaconPositions).size

    println(result)
}

fun solvePuzzle2(input: List<String>) {
    val beaconExclusionZones = input.map { BeaconExclusionZone.fromString(it) }
    val sensorsByXCoordinate = beaconExclusionZones.sortedBy {
        it.sensorLocation.first
    }

    val minCoordinate = 0
    val maxCoordinate = 4_000_000
    var y = minCoordinate
    while (y  <= maxCoordinate) {
        val sensorsInRangeOfRow = sensorsByXCoordinate.filter {
            it.isInRangeOfRow(y)
        }

        var x = minCoordinate
        var sensorIndex = 0
        while (x <= maxCoordinate) {
            // Skip to the next sensor we're in range of
            while (!sensorsInRangeOfRow[sensorIndex].isInRangeOf(x, y)) {
                sensorIndex++
                if (sensorIndex > sensorsInRangeOfRow.lastIndex) {
                    // Found a spot that's out of range of all sensors
                    val result = 4_000_000L * x + y
                    println(result)
                    return
                }
            }

            // Skip to the other side of this sensor's range if it's farther
            val sensorLocation = sensorsInRangeOfRow[sensorIndex].sensorLocation
            val yDelta = abs(sensorLocation.second - y)
            val leftoverRange = sensorsInRangeOfRow[sensorIndex].range - yDelta
            x = sensorLocation.first + leftoverRange + 1
        }
        y++
    }

    println("Didn't find an open position in range!")
}