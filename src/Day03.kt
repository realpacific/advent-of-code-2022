fun main() {
    fun calculatePriority(item: Char) =
        if (item.isUpperCase()) item.code - 65 + 27 else item.code - 97 + 1

    fun part1(input: List<String>): Int {
        var prioritySum = 0
        input.forEach { rucksack ->
            val itemsCountInCompartment = rucksack.lastIndex / 2

            val firstCompartment = rucksack.substring(0, itemsCountInCompartment + 1)
            val firstCompartmentItemCountMap = firstCompartment.toHashSet()

            val secondCompartment = rucksack.substring(itemsCountInCompartment + 1)
            val secondCompartmentItemCountMap = secondCompartment.toHashSet()

            val commonItem = firstCompartmentItemCountMap.intersect(secondCompartmentItemCountMap).first()

            prioritySum += calculatePriority(commonItem)

        }
        return prioritySum
    }

    fun part2(input: List<String>): Int {
        var prioritySum = 0
        input.chunked(3).forEach { group ->
            val (first, second, third) = group.map(String::toHashSet)
            val badge = first.intersect(second).intersect(third).first()
            prioritySum += calculatePriority(badge)
        }
        return prioritySum
    }

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
