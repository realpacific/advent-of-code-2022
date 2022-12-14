private data class Position(val row: Int, val col: Int) {
    fun generatePositionTo(end: Position): List<Position> {
        val positions = mutableListOf<Position>()
        val (startRow, startCol) = this
        val (endRow, endCol) = end
        if (startRow == endRow) {
            val from = minOf(startCol, endCol)
            val to = maxOf(startCol, endCol)
            for (col in from..to) {
                positions.add(Position(startRow, col))
            }
        } else {
            val from = minOf(endRow, startRow)
            val to = maxOf(endRow, startRow)
            for (row in from..to) {
                positions.add(Position(row, endCol))
            }
        }
        return positions
    }

    companion object {
        val POSITION_ABYSS = Position(-1, -1)
    }
}

private class RegolithReservoir(val coordinates: List<String>, val extraHorizontalWidth: Int = 0, calculateFloor: ((Int) -> Int)? = null) {

    private val maxHeight: Int
    private val maxWidth: Int
    private val minWidth: Int
    private val structure: Array<Array<Char>>

    private val pourPoint: Position

    init {
        val line: List<Pair<Int, Int>> = coordinates
                .map { it.split(" -> ") }
                .flatten()
                .map {
                    val (x, y) = it.split(",")
                    x.toInt() to y.toInt()
                }

        // required to calculate the size of array required to hold the cave structures
        maxHeight = line.maxOf { it.second }
        maxWidth = line.maxOf { it.first }
        minWidth = line.minOf { it.first }

        if (calculateFloor == null) {
            structure = Array(maxHeight + 1) {
                Array(maxWidth - minWidth + 1) { '.' }
            }
        } else {
            val depthOfGroundFloor = calculateFloor.invoke(maxHeight) - maxHeight
            structure = Array(maxHeight + 1 + depthOfGroundFloor) {
                Array(maxWidth - minWidth + 1 + extraHorizontalWidth + extraHorizontalWidth) {
                    '.'
                }
            }

            // create the base
            for (col in 0..structure.first().lastIndex) {
                structure[structure.lastIndex][col] = '#'
            }
        }
        pourPoint = toPosition("500,0")
        insertRocks()
    }


    private fun toPosition(location: String): Position {
        fun findYIndex(position: Int): Int {
            return position - minWidth
        }
        val (col, row) = location.split(",").map { it.toInt() }
        return Position(row, findYIndex(col) + extraHorizontalWidth)
    }

    private fun insertRocks() {
        coordinates
                .map { it.split(" -> ") }
                .forEach { rock ->
                    val rockPath = rock.map(::toPosition)
                    rockPath.forEachIndexed { index, location ->
                        structure[location.row][location.col] = '#'
                        if (index != 0) {
                            val startLocation = rockPath[index - 1]

                            // fill all points from[startLocation] to [location] with rocks
                            startLocation.generatePositionTo(location).forEach {
                                structure[it.row][it.col] = '#'
                            }
                        }
                    }
                }

        // create pour point
        structure[pourPoint.row][pourPoint.col] = '+'
        printBoard()
    }

    fun printBoard() {
        for (row in structure.indices) {
            for (col in structure.first().indices) {
                print(structure[row][col].toString().padStart(3))
            }
            println()
        }
        println()
    }

    fun isAbyss(position: Position): Boolean {
        return position == Position.POSITION_ABYSS || structure.getOrNull(position.row)?.getOrNull(position.col) == null
    }

    fun calculateNextMove(position: Position): Position? {
        val verticalMove = structure.getOrNull(position.row + 1)?.getOrNull(position.col)
        if (verticalMove == '.') {
            return Position(position.row + 1, position.col)
        }
        val diagonalLeftMove = structure.getOrNull(position.row + 1)?.getOrNull(position.col - 1)
        if (diagonalLeftMove == '.') {
            return Position(position.row + 1, position.col - 1)
        }
        val diagonalRightMove = structure.getOrNull(position.row + 1)?.getOrNull(position.col + 1)
        if (diagonalRightMove == '.') {
            return Position(position.row + 1, position.col + 1)
        }
        if (isAbyss(Position(position.row + 1, position.col - 1))) {
            return Position.POSITION_ABYSS
        } else if (isAbyss(Position(position.row + 1, position.col + 1))) {
            return Position.POSITION_ABYSS
        } else if (isAbyss(Position(position.row + 1, position.col))) {
            return Position.POSITION_ABYSS
        }
        return null
    }

    private fun canMove(position: Position): Boolean {
        val verticalMove = structure.getOrNull(position.row + 1)?.getOrNull(position.col)
        if (verticalMove == '.') {
            return true
        }
        val diagonalLeftMove = structure.getOrNull(position.row + 1)?.getOrNull(position.col - 1)
        if (diagonalLeftMove == '.') {
            return true
        }
        val diagonalRightMove = structure.getOrNull(position.row + 1)?.getOrNull(position.col + 1)
        if (diagonalRightMove == '.') {
            return true
        }
        return false
    }

    fun simulateInfiniteAbyss() {
        var simulationCount = 1
        while (true) {
            var currentMove: Position = toPosition("500,0")

            while (canMove(currentMove)) {
                val nextMOve = calculateNextMove(currentMove) ?: break
                currentMove = nextMOve
            }
            calculateNextMove(currentMove)?.let {
                if (isAbyss(it)) {
                    return
                }
            }
            structure[currentMove.row][currentMove.col] = 'o'
            simulationCount++
        }
    }

    fun simulateWithBase() {
        var simulationCount = 1
        while (true) {
            var currentMove: Position = toPosition("500,0")

            while (canMove(currentMove)) {
                val nextMOve = calculateNextMove(currentMove) ?: break
                currentMove = nextMOve
            }
            if (calculateNextMove(currentMove) == null && currentMove == toPosition("500,0")) {
                structure[currentMove.row][currentMove.col] = 'o'
                return
            }
            structure[currentMove.row][currentMove.col] = 'o'
            simulationCount++
        }
    }

    fun countSands(): Int {
        var sum = 0
        structure.forEach {
            sum += it.count { cell -> cell == 'o' }
        }
        return sum
    }

}

fun main() {
    fun part1(input: List<String>): Int {
        val reservoir = RegolithReservoir(input)
        reservoir.simulateInfiniteAbyss()
        reservoir.printBoard()
        return reservoir.countSands()
    }

    fun part2(input: List<String>): Int {
        // arbitrary number-- it depends on the examples used
        val width = 200
        val reservoir = RegolithReservoir(input, width) { heightDiff ->
            heightDiff + 2
        }
        reservoir.simulateWithBase()
        reservoir.printBoard()
        return reservoir.countSands()
    }


    val input = readInput("Day14")
    println(part1(input))
    println(part2(input).also { assert(it == 24166) })
}
