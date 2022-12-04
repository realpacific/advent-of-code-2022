fun main() {
    fun convertToRange(text: String): IntRange {
        val range = text.split("-").map(String::toInt)
        return range.first()..range.last()
    }

    fun splitInput(text: String): Pair<IntRange, IntRange> {
        val input = text.split(",")
        val firstElf = convertToRange(input.first())
        val secondElf = convertToRange(input.last())
        return firstElf to secondElf
    }

    fun IntRange.completelyOverlap(other: IntRange): Boolean =
        (first in other && last in other) || (other.first in this && other.last in this)

    fun IntRange.hasAnyOverlap(other: IntRange): Boolean =
        first in other || last in other || other.first in this || other.last in this

    fun part1(input: List<String>): Int {
        var overlapsCount = 0
        input.forEach { line ->
            val (first, second) = splitInput(line)
            if (first.completelyOverlap(second)) {
                overlapsCount += 1
            }
        }
        return overlapsCount
    }

    fun part2(input: List<String>): Int {
        var overlapsCount = 0
        input.forEach { line ->
            val (first, second) = splitInput(line)
            if (first.hasAnyOverlap(second)) {
                overlapsCount += 1
            }
        }
        return overlapsCount
    }

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
