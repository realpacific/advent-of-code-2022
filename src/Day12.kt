import java.util.*

private typealias Position = Pair<Int, Int>

private data class Step(val position: Position) {
    fun generateNextSteps(heightmap: Array<Array<Char>>): List<Step> {
        return arrayOf(
            Step(this.position.first + 1 to this.position.second),
            Step(this.position.first - 1 to this.position.second),
            Step(this.position.first to this.position.second + 1),
            Step(this.position.first to this.position.second - 1),
        ).filter { this.isPossible(heightmap, it) }
    }

    private fun isPossible(heightmap: Array<Array<Char>>, next: Step): Boolean {
        val fromElevation = heightmap[this.position.first][this.position.second]
        val toElevation = heightmap.getOrNull(next.position.first)?.getOrNull(next.position.second) ?: return false
        val srcHeight = calculateHeight(fromElevation)
        val destHeight = calculateHeight(toElevation)
        return srcHeight <= destHeight + 1
    }
}

private fun calculateHeight(elevation: Char): Int {
    if (elevation == 'S') return 'a'.code
    if (elevation == 'E') return 'z'.code
    return elevation.code
}


fun main() {

    fun print(shortestDistanceMatrix: List<MutableList<Int>>, heightmap: Array<Array<Char>>) {
        for (row in shortestDistanceMatrix.indices) {
            for (colIndex in shortestDistanceMatrix.first().indices) {
                val elevation = heightmap[row][colIndex]
                val distance = shortestDistanceMatrix[row][colIndex]
                print(("$distance|$elevation").padEnd(4) + "   ")
            }
            println()
        }
    }

    /**
     * Start from [endPosition], breadth first calculate distance of its neighbours
     */
    fun buildShortestPathMatrix(heightmap: Array<Array<Char>>, endPosition: Position): List<MutableList<Int>> {
        val steps: Queue<MutableList<Step>> = ArrayDeque()
        steps.add(mutableListOf(Step(endPosition)))

        val shortestDistanceMatrix = List(heightmap.size) {
            MutableList(heightmap.first().size) {
                Int.MAX_VALUE
            }
        }

        shortestDistanceMatrix[endPosition.first][endPosition.second] = 0 // distance of E from destination is 0

        val visited = mutableSetOf<Step>()
        visited.add(steps.first().first()) // add E to visited

        while (steps.isNotEmpty()) {
            val currentSteps = steps.remove()
            currentSteps
                .forEach { currentStep ->
                    visited.add(currentStep)
                    val nextMoves = currentStep.generateNextSteps(heightmap)
                        .onEach { next ->
                            shortestDistanceMatrix[next.position.first][next.position.second] =
                                minOf(
                                    shortestDistanceMatrix[currentStep.position.first][currentStep.position.second] + 1,
                                    shortestDistanceMatrix[next.position.first][next.position.second]
                                )
                        }
                        .filter { it !in visited }
                        .toMutableList()
                        .onEach { visited.add(it) }
                    steps.add(nextMoves)
                }
        }
        // print(shortestDistanceMatrix, heightmap)
        return shortestDistanceMatrix
    }


    fun part1(input: List<String>): Int {
        var endPosition = 0 to 0
        var startPosition = 0 to 0
        val heightmap = Array(input.size) { row ->
            Array(input.first().length) { col ->
                val elevation = input[row][col]
                if (elevation == 'E') endPosition = row to col
                if (elevation == 'S') startPosition = row to col
                elevation
            }
        }
        return buildShortestPathMatrix(heightmap, endPosition)[startPosition.first][startPosition.second]
    }

    fun part2(input: List<String>): Int {
        var endPosition = 0 to 0
        val possibleStartLocation = mutableListOf<Position>()
        val heightmap = Array(input.size) { row ->
            Array(input.first().length) { col ->
                val elevation = input[row][col]
                if (elevation == 'E') endPosition = row to col
                if (elevation == 'S' || elevation == 'a') possibleStartLocation.add(row to col)
                elevation
            }
        }

        val path = buildShortestPathMatrix(heightmap, endPosition)
        return possibleStartLocation.minOf { path[it.first][it.second] }
    }


    run {
        val input = readInput("Day12_test")
        println(part1(input))
        println(part2(input))
    }
    run {
        val input = readInput("Day12")
        println(part1(input))
        println(part2(input))
    }
}
