private fun Pair<Int, Int>.contains(other: Pair<Int, Int>): Boolean {
    return other.first in this.first..this.second && other.second in this.first..this.second
}

private class Signal(val left: String, val right: String) {
    val firstItems = extractPackets(left)
    val secondItems = extractPackets(right)

    /**
     * Given `[1,[2,[3,[4,[5,6,7]]]],8,9]`, it returns `0: [1], 1: [2,[3,[4,[5,6,7]]]], 2: 8, 3: 9`
     */
    private fun extractPackets(input: String): List<String> {
        val result = mutableListOf<String>()
        if (input.none { it == '[' }) {
            // all are just plain strings
            result.add(input)
            return result
        }

        val positionOfOpeningBrackets = mutableListOf<Int>()
        val bracketPairs = mutableSetOf<Pair<Int, Int>>()

        // for every opening brackets, find its corresponding closing bracket
        for (index in input.indices) {
            if (input[index] == '[') {
                positionOfOpeningBrackets.add(index)
            } else if (input[index] == ']') {
                val opening = positionOfOpeningBrackets.removeLast()
                bracketPairs.add(opening to index)
            }
        }

        // we don't need the outermost bracket
        // [  [], [ [] [] [] [] ], [] ]  ------>  [], [ [] [] [] [] ], []
        // just its inner content is enough
        val innerBrackets = bracketPairs.reversed().drop(1)

        // we also don't need the elements at depth > 1
        // so make larger bracket consume the overlapping ones
        //  [], [ [] [] [] [] ], []    ------>       [], [], []
        for (i in 1..innerBrackets.lastIndex) {
            val current = innerBrackets[i - 1]
            val next = innerBrackets[i]
            if (current.contains(next)) {
                bracketPairs.remove(next) // remove the brackets that are overlapped
            }
        }

        val wholeBracket = bracketPairs.find { it.first == 0 }!!
        // remove the whole bracket (it starts from 0th index and goes all the way to end)
        // It is not needed cause we are only interested in its content
        bracketPairs.remove(wholeBracket)

        // now restore all the non list items
        var index = 1   // start from 1 since we do not need the '['
        while (index <= input.lastIndex) {
            if (index in bracketPairs.map { it.first }) {                   // list starts here
                val found = bracketPairs.find { index == it.first }!!
                result.add(input.slice(found.first..found.second))  // add the content of whole list as string
                index = found.second + 1
            } else {
                // these are plain integers.
                // integer can be of multiple digits.
                // to find the end of an integer, find the ',' or ']' or even go till last character
                val end = minOf(
                    if (input.indexOf(',', index) < 0) Int.MAX_VALUE else input.indexOf(',', index),
                    if (input.indexOf(']', index) < 0) Int.MAX_VALUE else input.indexOf(']', index),
                    input.length
                )
                val digit = input.slice(index until end)
                result.add(digit)
                index = end + 1
            }
        }

        return result.filter { it.isNotEmpty() }
    }

    private fun convertToList(value: String) = "[$value]"

    fun isInRightOrder(): Boolean? {
        return isInRightOrder(left, right)
    }

    private fun isInRightOrder(leftStr: String?, rightStr: String?): Boolean? {
        if (leftStr == null && rightStr == null) {
            TODO()
        }

        // if one of them finishes early
        if (leftStr == null) return true
        else if (rightStr == null) return false

        // check if integers
        val firstAsInt = leftStr.toIntOrNull()
        val secondAsInt = rightStr.toIntOrNull()

        if (firstAsInt != null && secondAsInt != null) {
            // both are digits
            return if (firstAsInt < secondAsInt) true   // order is right
            else if (firstAsInt > secondAsInt) false    // wrong order
            else null
        }

        // one of 'em is integer, so convert to list
        if ((firstAsInt == null && secondAsInt != null)) {
            return Signal(leftStr, convertToList(rightStr)).isInRightOrder()
        } else if ((firstAsInt != null && secondAsInt == null)) {
            return Signal(convertToList(leftStr), rightStr).isInRightOrder()
        }

        // both are list, need to check its contents
        for (i in 0..maxOf(firstItems.lastIndex, secondItems.lastIndex)) {
            val firstStr = firstItems.getOrNull(i) ?: return true
            val secondStr = secondItems.getOrNull(i) ?: return false
            val isRightOrder = Signal(firstStr, secondStr).isInRightOrder()
            if (isRightOrder != null) return isRightOrder
        }
        return null
    }
}

class SignalCompare(val value: String) : Comparable<SignalCompare> {
    override fun toString(): String {
        return "SignalCompare(value=$value)"
    }


    override fun compareTo(other: SignalCompare): Int {
        return when (Signal(value, other.value).isInRightOrder()) {
            true -> 1
            false -> -1
            else -> 0
        }
    }

}

fun main() {
    fun isInRightOrder(input: List<String>): Boolean {
        val (first, second) = input
        val isRightOrder = Signal(first, second).isInRightOrder()
        return isRightOrder!!

    }

    fun part1(input: List<String>): Int {
        return input
            .chunked(3)
            .mapIndexed { index, list ->
                if (isInRightOrder(list)) index + 1 else 0
            }.sum()
    }

    fun part2(input: List<String>): Int {
        val _input = input.toMutableList().also {
            it.add("[[2]]")
            it.add("[[6]]")
        }
            .filter { it.isNotEmpty() }
            .map {
                SignalCompare(it)
            }

        val sorted = _input.sortedDescending()
        val indexOfFirstDivider = sorted.indexOfFirst { it.value == "[[2]]" } + 1
        val indexOfSecondDivider = sorted.indexOfFirst { it.value == "[[6]]" } + 1
        return indexOfFirstDivider * indexOfSecondDivider

    }

    run {
        val input = readInput("Day13")
        println(part1(input))
        println(part2(input))
    }
}
