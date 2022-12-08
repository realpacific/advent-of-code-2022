/**
 * [Source](https://adventofcode.com/2022/day/8)
 */
fun main() {
    fun part1(input: List<String>): Int {
        fun checkVisibilityFromLeft(row: Int, col: Int, seen: MutableSet<Pair<Int, Int>>): Boolean {
            if (seen.contains(row to col)) return false
            val selectedTree = input[row][col]
            for (currentCol in (col - 1) downTo 0) {
                if (selectedTree <= input[row][currentCol]) {
                    return false
                }
            }
            seen.add(row to col)
            return true
        }

        fun checkVisibilityFromRight(row: Int, col: Int, seen: MutableSet<Pair<Int, Int>>): Boolean {
            if (seen.contains(row to col)) return false
            val selectedTree = input[row][col]
            for (currentCol in (col + 1)..input.first().lastIndex) {
                if (selectedTree <= input[row][currentCol]) {
                    return false
                }
            }
            seen.add(row to col)
            return true
        }

        fun checkVisibilityFromTop(row: Int, col: Int, seen: MutableSet<Pair<Int, Int>>): Boolean {
            if (seen.contains(row to col)) return false
            val selectedTree = input[row][col]
            for (currentRow in 0 until row) {
                if (selectedTree <= input[currentRow][col]) {
                    return false
                }
            }
            seen.add(row to col)
            return true
        }

        fun checkVisibilityFromBottom(row: Int, col: Int, seen: MutableSet<Pair<Int, Int>>): Boolean {
            if (seen.contains(row to col)) return false
            val selectedTree = input[row][col]
            for (currentRow in (row + 1)..input.lastIndex) {
                if (selectedTree <= input[currentRow][col]) {
                    return false
                }
            }
            seen.add(row to col)
            return true
        }

        val visibleTrees = mutableSetOf<Pair<Int, Int>>()

        for (row in input.indices) {
            for (col in input.first().indices) {
                checkVisibilityFromLeft(row, col, visibleTrees)
                    .or(checkVisibilityFromBottom(row, col, visibleTrees))
                    .or(checkVisibilityFromRight(row, col, visibleTrees))
                    .or(checkVisibilityFromTop(row, col, visibleTrees))
            }
        }
        return visibleTrees.size
    }

    fun part2(input: List<String>): Int {

        fun calculateScenicScoreTop(row: Int, col: Int): Int {
            var score = 0
            val selectedTree = input[row][col]
            for (currentRow in (row - 1) downTo 0) {
                if (selectedTree > input[currentRow][col]) {
                    score++
                } else {
                    score++
                    return score
                }
            }
            return score
        }

        fun calculateScenicScoreBottom(row: Int, col: Int): Int {
            var score = 0
            val selectedTree = input[row][col]
            for (currentRow in (row + 1)..input.lastIndex) {
                if (selectedTree > input[currentRow][col]) {
                    score++
                } else {
                    score++
                    return score
                }
            }
            return score
        }

        fun calculateScenicScoreLeft(row: Int, col: Int): Int {
            var score = 0
            val tree = input[row][col]
            for (currentCol in (col - 1) downTo 0) {
                if (tree > input[row][currentCol]) {
                    score++
                } else {
                    score++
                    return score
                }
            }
            return score
        }

        fun calculateScenicScoreRight(row: Int, col: Int): Int {
            var score = 0
            val tree = input[row][col]
            for (currentCol in (col + 1)..input[0].lastIndex) {
                if (tree > input[row][currentCol]) {
                    score++
                } else {
                    score++
                    return score
                }
            }
            return score
        }

        var maxScores = Integer.MIN_VALUE
        for (row in 1 until input.lastIndex) {
            for (col in 1 until input.first().lastIndex) {
                val score = calculateScenicScoreLeft(row, col)
                    .times(calculateScenicScoreRight(row, col))
                    .times(calculateScenicScoreBottom(row, col))
                    .times(calculateScenicScoreTop(row, col))
                maxScores = maxOf(maxScores, score)
            }
        }
        return maxScores
    }

    run {
        val input = readInput("Day08_test")
        assert(part1(input) == 21)
        assert(part2(input) == 8)
    }
    run {
        val input = readInput("Day08")
        println(part1(input))
        println(part2(input))
    }
}
