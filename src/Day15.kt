import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

private fun solveForX(coordinate1: Coordinate, coordinate2: Coordinate): (Int) -> Int {
    return { y: Int ->
        val slope = (coordinate2.second - coordinate1.second) / (coordinate2.first - coordinate1.first)
        // y - y1 = m(x - x1)
        (y - coordinate1.second) / slope + coordinate1.first
    }
}

data class SensorBeacon(val sensor: Coordinate, val beacon: Coordinate) {
    val distance: Int = abs(sensor.first - beacon.first) + abs(sensor.second - beacon.second)

    // the Manhattan Geometry forms a square
    val leftCorner: Coordinate = sensor.first - distance to sensor.second
    val rightCorner: Coordinate = sensor.first + distance to sensor.second
    val topCorner: Coordinate = sensor.first to sensor.second + distance
    val bottomCorner: Coordinate = sensor.first to sensor.second - distance


    fun findBottomHalfIntersection(y: Int): IntRange {
        return solveForX(leftCorner, bottomCorner)(y)..solveForX(rightCorner, bottomCorner)(y)
    }

    fun findTopHalfIntersection(y: Int): IntRange {
        return solveForX(leftCorner, topCorner)(y)..solveForX(rightCorner, topCorner)(y)
    }

    fun isHorizontalLinePassesThroughSquare(y: Int): Boolean = y in topCorner.second downTo bottomCorner.second
    fun isHorizontalLineCutsAtTopHalf(y: Int): Boolean = y in topCorner.second downTo leftCorner.second
    fun isHorizontalLineCutsAtBottomHalf(y: Int): Boolean = y in leftCorner.second downTo bottomCorner.second
}

private fun IntRange.keepRangeInBetween(lowValue: Int, atMostValue: Int): IntRange {
    val start: Int = if (first < lowValue) lowValue else first
    val end: Int = if (last < atMostValue) last else atMostValue - 1
    return start..end
}


private fun List<IntRange>.merge(): List<IntRange> {
    val result = mutableListOf<IntRange>()
    val sorted = this.sortedBy { it.first }
    sorted.forEach { current ->
        if (result.isEmpty()) {
            result.add(current)
            return@forEach
        } else {
            val prev = result.last()
            if (current.first in prev && current.last in prev) {
                return@forEach
            }
            if (current.last !in prev) {
                result.removeLast()
                result.add(prev.first..current.last)
            }
        }
    }
    return result
}

fun main() {

    fun extractCoordinates(input: List<String>): List<SensorBeacon> {
        val regex = "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()
        return input.map {
            val (sensorX, sensorY, beaconX, beaconY) = regex.matchEntire(it)!!.groupValues
                .slice(1..4)
                .map(String::toInt)

            // the question uses opposite index (y-coordinate increases as you go down the y-axis)
            SensorBeacon(Coordinate(sensorX, -sensorY), Coordinate(beaconX, -beaconY))
        }
    }

    fun part1(input: List<String>, yCoordinate: Int): Int {
        // the question uses opposite index (y-coordinate increases as you go down the y-axis)
        val y = -yCoordinate
        val squares = extractCoordinates(input)
        val xCoordinateOfBeaconAtY = squares.map { it.beacon }.filter { it.second == y }.map { it.first }
        val results = mutableListOf<IntRange>()

        squares.forEach { square ->
            if (square.isHorizontalLineCutsAtBottomHalf(y)) {
                val range = square.findBottomHalfIntersection(y)
                results.add(range)
            } else if (square.isHorizontalLineCutsAtTopHalf(y)) {
                val range = square.findTopHalfIntersection(y)
                results.add(range)
            }
        }

        val seen = mutableSetOf<Int>()
        for (range in results) {
            for (xCoordinate in range) {
                if (xCoordinate !in xCoordinateOfBeaconAtY)
                    seen.add(xCoordinate)
            }
        }
        return seen.size
    }

    fun part2(input: List<String>, atMostValue: Int): Int {
        val squares = extractCoordinates(input)
        val foundAnswer = AtomicInteger()

        val executor = Executors.newFixedThreadPool(10)
        for (i in 0 until atMostValue) {
            executor.submit {
                println("Executing $i")
                val y = -i
                var results = mutableListOf<IntRange>()

                squares.forEach { square ->
                    if (square.isHorizontalLineCutsAtBottomHalf(y)) {
                        val range = square.findBottomHalfIntersection(y)
                        results.add(range)
                    } else if (square.isHorizontalLineCutsAtTopHalf(y)) {
                        val range = square.findTopHalfIntersection(y)
                        results.add(range)
                    }
                }
                results = results.map { it.keepRangeInBetween(0, atMostValue) }.merge().toMutableList()
                if (results.size == 1 && results.first().first == 0 && results.first().last == atMostValue - 1) {
                    return@submit
                }

                val missingSignalCoordinate = (results.first().first - 1) * 4000000 + -1 * results.first().last
                foundAnswer.set(missingSignalCoordinate)
                println()
            }
        }
        executor.awaitTermination(100, TimeUnit.MINUTES)
        return foundAnswer.get()
    }

//    run {
//        val input = readInput("Day15_test")
//        println(part1(input, 10))
//        println(part2(input, 20))
//    }
    run {
        val input = readInput("Day15")
//        println(part1(input, 2000000))
        println(part2(input, 4_000_000))
    }
}
