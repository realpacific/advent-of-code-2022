import java.math.BigInteger

class Monkey(val id: Int, operation: String, test: String) {
    private val startingItems = mutableListOf<BigInteger>()

    var inspectionCount = 0
        private set

    private lateinit var throwToFn: (Boolean) -> Int

    val divisibleBy by lazy {
        val testRegex = Regex("Test: divisible by (\\d+)").find(test)!!
        testRegex.groupValues[1].toBigInteger()
    }

    fun addItem(item: BigInteger) {
        startingItems.add(item)
    }

    fun removeItem(item: BigInteger) {
        startingItems.remove(item)
    }

    fun currentItems() = ArrayList(startingItems)

    private val operationRegex by lazy {
        Regex("Operation: new = old ([+|-|*|/]) (\\d+)").find(operation)
            ?: Regex("Operation: new = old ([+|-|*|/]) old").find(operation)!!
    }

    fun calculateThrow(item: BigInteger, mapper: (BigInteger) -> BigInteger): Pair<BigInteger, Int> {
        inspectionCount++
        val worryLevel = mapper(calculateWorryLevel(item))
        val throwTo = throwToFn(test(worryLevel))
        return worryLevel to throwTo
    }

    fun setThrowTo(cases: List<String>) {
        val ifTrueRegex = Regex("If true: throw to monkey (\\d+)").find(cases[0])!!
        val ifFalseRegex = Regex("If false: throw to monkey (\\d+)").find(cases[1])!!
        throwToFn = {
            if (it) ifTrueRegex.groupValues[1].toInt()
            else ifFalseRegex.groupValues[1].toInt()
        }
    }

    private fun calculateWorryLevel(value: BigInteger): BigInteger {
        val operator = operationRegex.groupValues[1]
        val operand = operationRegex.groupValues.getOrNull(2)?.toBigIntegerOrNull() ?: value
        return when (operator) {
            "*" -> value.times(operand)
            "+" -> value.plus(operand)
            "-" -> value.minus(operand)
            "/" -> value.div(operand)
            else -> TODO()
        }
    }

    private fun test(value: BigInteger): Boolean {
        return value.mod(divisibleBy) == BigInteger.ZERO
    }

    companion object {
        fun build(input: List<String>): Monkey {
            val regex = Regex("Monkey (\\d):").find(input[0])!!
            val monkeyId = regex.groupValues[1].toInt()
            val monkey = Monkey(monkeyId, operation = input[2], test = input[3])
            monkey.setThrowTo(input.slice(4..5))
            monkey.startingItems.addAll(extractAllDigits(input[1]))
            return monkey
        }

        private fun extractAllDigits(input: String) = input.split(" ")
            .map { it.replace(",", "") }
            .filter { it.toBigIntegerOrNull() != null }
            .map { it.toBigInteger() }.toMutableList()
    }
}

fun main() {


    fun calculateMonkeyBusiness(monkeys: List<Monkey>): Long {
        val topTwoInspectionCount = monkeys.map { it.inspectionCount }.sortedDescending().slice(0..1)
        return (topTwoInspectionCount[0]).toLong().times(topTwoInspectionCount[1].toLong())
    }

    fun buildMonkeys(input: List<String>): List<Monkey> {
        val monkeys = ArrayList<Monkey>(input.size / 7)
        input.chunked(7).forEach { data ->
            val monkey = Monkey.build(data)
            monkeys.add(monkey)
        }
        return monkeys
    }

    fun part1(input: List<String>): Long {
        val monkeys = buildMonkeys(input)
        repeat(20) { round ->
            monkeys.forEach { monkey ->
                val currentItem = monkey.currentItems()
                for (item in currentItem) {
                    val (worryLevel, throwTo) = monkey.calculateThrow(item) {
                        it.divide((3).toBigInteger())
                    }
                    monkeys[throwTo].addItem(worryLevel)
                    monkey.removeItem(item)
                }
            }
        }
        monkeys.forEach { monkey ->
            println("Monkey ${monkey.id} inspected items ${monkey.inspectionCount} times.")
        }
        return calculateMonkeyBusiness(monkeys)
    }


    fun part2(input: List<String>): Long {
        val monkeys = buildMonkeys(input)
        val modulo = monkeys.map(Monkey::divisibleBy).fold(BigInteger.ONE) { acc, current -> acc.multiply(current) }
        repeat(10000) {
            monkeys.forEach { monkey ->
                val currentItem = monkey.currentItems()
                for (item in currentItem) {
                    val (worryLevel, throwTo) = monkey.calculateThrow(item) {
                        it.mod(modulo)
                    }
                    monkeys[throwTo].addItem(worryLevel)
                    monkey.removeItem(item)
                }
            }
        }
        monkeys.forEach { monkey ->
            println("Monkey ${monkey.id} inspected items ${monkey.inspectionCount} times.")
        }
        return calculateMonkeyBusiness(monkeys)
    }

    run {
        val input = readInput("Day11")
        println(part1(input))
        println(part2(input))
    }
}
