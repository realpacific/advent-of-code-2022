import kotlin.math.pow
import kotlin.math.sqrt

data class Location(val row: Int, val col: Int)

private infix fun Int.to(other: Int): Location {
    return Location(this, other)
}

fun main() {

    class Rope(val startAt: Int, val length: Int) {
        /**
         * Holds position of all the parts of rope
         * The tail is at last index whereas head is at the row index.
         */
        private val bodies = Array(length + 1) { Location(startAt, startAt) }

        /**
         * The bridge as matrix to hold the rope
         *
         * Make the array large enough to handle the movement
         */
        val bridge: Array<Array<String>> = Array(startAt * 2) {
            Array(startAt * 2) { "" }
        }

        init {
            // initialize all head and tail and everything in between in the [startIndex]
            bridge[startAt][startAt] = if (length == 1) "sHT" else "sH" + (1..length).map { it.toString() }
        }

        private fun setPosition(location: Location, value: String) {
            bridge[location.row][location.col] = bridge[location.row][location.col].plus(value)
        }

        private fun removePosition(location: Location, value: String) {
            bridge[location.row][location.col] = bridge[location.row][location.col].replace(value, "")
        }

        fun executeCommand(cmd: String) {
            val (direction, times) = cmd.split(" ")
            repeat(times.toInt()) {
                removePosition(bodies.first(), "H") // remove head from its current position before moving
                var headIndex = bodies.first()
                when (direction) {
                    "R" -> headIndex = headIndex.row to (headIndex.col + 1)
                    "L" -> headIndex = headIndex.row to headIndex.col - 1
                    "U" -> headIndex = headIndex.row - 1 to headIndex.col
                    "D" -> headIndex = headIndex.row + 1 to headIndex.col
                }
                moveTheHead(headIndex)
                if (requiresMoving(1)) { // check if the part next to head requires moving
                    // if it does, then the whole body requires moving
                    for (i in 1..length) {
                        moveBodyPart(i)
                    }
                }
            }
        }

        private fun moveTheHead(headIndex: Location) {
            bodies[0] = headIndex
            setPosition(headIndex, "H")
        }

        /**
         * Movement is requires only if distance between current and previous body part is greater than 1
         */
        private fun requiresMoving(position: Int): Boolean {
            val headIndex = bodies[position - 1]
            val tailIndex = bodies[position]
            val distance = sqrt((headIndex.row - tailIndex.row).toDouble().pow(2) + (headIndex.col - tailIndex.col).toDouble().pow(2))
            if (distance.toInt() <= 1) return false
            return true
        }

        private fun moveBodyPart(tailPosition: Int) {
            val headIndex = bodies[tailPosition - 1]
            var tailIndex = bodies[tailPosition]

            fun isInSamefirst(): Boolean {
                return (headIndex.row == tailIndex.row)
            }

            fun isInSameColumn(): Boolean {
                return (headIndex.col == tailIndex.col)
            }

            if (!requiresMoving(tailPosition)) {
                return
            }
            if (isInSameColumn()) {
                if (headIndex.row > tailIndex.row) { // head is located below tail
                    // move tail down
                    tailIndex = tailIndex.row + 1 to tailIndex.col
                } else if (headIndex.row < tailIndex.row) { // head located above tail
                    // move tail up
                    tailIndex = tailIndex.row - 1 to tailIndex.col
                }
            } else if (isInSamefirst()) { // tail needs to be moved sideways
                if (headIndex.col > tailIndex.col) {
                    tailIndex = tailIndex.row to tailIndex.col + 1
                } else if (headIndex.col < tailIndex.col) {
                    tailIndex = tailIndex.row to tailIndex.col - 1
                }
            } else {
                if (headIndex.row > tailIndex.row) {
                    tailIndex = if (headIndex.col > tailIndex.col) {
                        tailIndex.row + 1 to tailIndex.col + 1
                    } else {
                        tailIndex.row + 1 to tailIndex.col - 1
                    }
                } else if (headIndex.row < tailIndex.row) {
                    tailIndex = if (headIndex.col > tailIndex.col) {
                        tailIndex.row - 1 to tailIndex.col + 1
                    } else {
                        tailIndex.row - 1 to tailIndex.col - 1
                    }
                }
            }
            bodies[tailPosition] = tailIndex
            setPosition(tailIndex, if (length == 1) "T" else tailPosition.toString())
        }

        fun print() {
            for (i in bridge.indices) {
                for (j in bridge.first().indices) {
                    print(bridge[i][j].padStart(length + 2))
                }
                println()
            }
        }

        fun countTailMovements(tailName: String): Int {
            var count = 0
            for (i in bridge.indices) {
                for (j in bridge.first().indices) {
                    if (bridge[i][j].contains(tailName)) count++
                }
            }
            return count
        }
    }


    fun part1(input: List<String>, boardSize: Int): Int {
        val bridge = Rope(boardSize, 1)
        input.forEach { cmd ->
            bridge.executeCommand(cmd)
        }
        // bridge.print()
        return bridge.countTailMovements("T")
    }

    fun part2(input: List<String>, boardSize: Int): Int {
        val bridge = Rope(boardSize, length = 9)
        input.forEach { cmd ->
            bridge.executeCommand(cmd)
        }
        // bridge.print()
        return bridge.countTailMovements("9")
    }

    run {
        val input = readInput("Day09_test")
        assert(part1(input, 10).also(::println) == 13)
        assert(part2(input, 10).also(::println) == 1)
    }
    run {
        val input = readInput("Day09_test_2")
        assert(part2(input, 100).also(::println) == 36)
    }
    run {
        val input = readInput("Day09")
        println(part1(input, 500))
        println(part2(input, 500))
    }
}
