fun main() {
    fun part1(input: List<String>): Int {
        var currentCaloriesSum = 0
        var maxCalories = Integer.MIN_VALUE
        input.forEach {
            if (it.isEmpty()) {
                maxCalories = maxOf(maxCalories, currentCaloriesSum)
                currentCaloriesSum = 0
            } else {
                currentCaloriesSum += it.toInt()
            }
        }
        return maxCalories
    }

    fun part2(input: List<String>): Int {
        val topThreeCalories = IntArray(3) { Integer.MIN_VALUE }
        var currentCaloriesSum = 0
        input.forEach {
            if (it.isEmpty()) {
                topThreeCalories.sort()
                for (i in 0..topThreeCalories.lastIndex) {
                    if (topThreeCalories[i] < currentCaloriesSum) {
                        topThreeCalories[i] = currentCaloriesSum
                        break
                    }
                }

                currentCaloriesSum = 0
            } else {
                currentCaloriesSum += it.toInt()
            }
        }
        return topThreeCalories.sum()
    }

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
