package day15

import utils.manhattanDistance
import java.util.*
import kotlin.math.abs

class BeaconExclusionZone(
    private val sensorLocation: Pair<Int, Int>,
    private val beaconLocation: Pair<Int, Int>,
) {
    private val range = sensorLocation.manhattanDistance(beaconLocation)

    fun blockedPositionsInRow(y: Int): Set<Int> {
        val distanceToY = abs(y - sensorLocation.second)

        // Row of interest is out of range of this sensor
        if (distanceToY > range) return setOf()

        val x = sensorLocation.first
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
    println(Date())

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

    println(Date())
}

fun solvePuzzle2(input: List<String>) {
    val beaconExclusionZones = input.map { BeaconExclusionZone.fromString(it) }

    println(Date())

    val minCoordinate = 0
    val maxCoordinate = 4_000_000
    for (x in minCoordinate..maxCoordinate) {
        for (y in minCoordinate..maxCoordinate) {
            if (beaconExclusionZones.none { it.isInRangeOf(x, y) }) {
                val result = x.toLong() * 4_000_000L + y
                println(Date())
                println(result)
                return
            }
        }
    }

    println("Didn't find an open position in range!")
    println(Date())
}