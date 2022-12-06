import java.util.*


fun main() {
    val commandRegex = Regex("move (\\d+) from (\\d+) to (\\d+)")

    class CrateHolder {
        private val stack = Stack<String>()

        fun push(item: String) {
            stack.push(item)
        }

        fun pop(): String {
            return stack.pop()!!
        }

        fun peek(): String {
            return stack.peek()
        }
    }

    data class Command(val from: Int, val to: Int, val count: Int)

    class SupplyStacks(count: Int) {
        val crateHolders = Array(count) { CrateHolder() }

        fun load(index: Int, crate: String) {
            if (crate.isBlank()) return
            crateHolders[index].push(crate)
        }

        fun process(command: Command) {
            val fromCrate = crateHolders[command.from - 1]
            val toCrate = crateHolders[command.to - 1]
            repeat(command.count) {
                val popped = fromCrate.pop()
                toCrate.push(popped)
            }
        }

        fun processV2(command: Command) {
            val fromCrate = crateHolders[command.from - 1]
            val toCrate = crateHolders[command.to - 1]
            val collector = mutableListOf<String>()
            repeat(command.count) {
                collector.add(fromCrate.pop())
            }
            collector.reversed().forEach(toCrate::push)
        }
    }


    fun parseCommands(line: String): Command {
        val find = commandRegex.find(line)!!.groupValues
        return Command(from = find[2].toInt(), to = find[3].toInt(), count = find[1].toInt())
    }

    fun findCrateRow(input: List<String>) = input.indexOfFirst { !it.trim().startsWith("[") }

    fun extractCrateName(currentRow: String, column: Int): String {
        return currentRow
                .substring(column..column + 2) //  2 braces + 1 character
                .replace("[", "")
                .replace("]", "")
    }

    fun part1(input: List<String>): String {
        val crateLineIndex = findCrateRow(input)
        val crateNumbers = input[crateLineIndex].trim().split(" ").filter(String::isNotEmpty)
        val stacks = SupplyStacks(crateNumbers.size)

        for (row in (crateLineIndex - 1) downTo 0) {
            val currentRow = input[row]
            for (column in 0..currentRow.lastIndex step 4) {
                val crateName = extractCrateName(currentRow, column)
                stacks.load(column / 4, crateName)
            }
        }
        for (i in crateLineIndex + 2..input.lastIndex) {
            val cmd = parseCommands(input[i])
            stacks.process(cmd)
        }

        return stacks.crateHolders.joinToString("") { it.peek() }
    }

    fun part2(input: List<String>): String {
        val crateLineIndex = findCrateRow(input)
        val crateNumbers = input[crateLineIndex].trim().split(" ").filter(String::isNotEmpty)
        val stacks = SupplyStacks(crateNumbers.size)

        for (row in (crateLineIndex - 1) downTo 0) {
            for (column in 0..input[row].lastIndex step 4) {
                val crateName = extractCrateName(input[row], column)
                stacks.load(column / 4, crateName)
            }
        }
        for (i in crateLineIndex + 2..input.lastIndex) {
            val cmd = parseCommands(input[i])
            stacks.processV2(cmd)
        }

        return stacks.crateHolders.joinToString("") { it.peek() }
    }

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
