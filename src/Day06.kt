fun main() {

    fun numberOfCharacterBeforeSeeingTheFirstDistinctSequence(input: String, distinctCount: Int): Int {
        val charactersSeen = mutableListOf<Char>()
        input.forEachIndexed { currentIndex, character ->
            if (charactersSeen.size == distinctCount) {
                return currentIndex
            }
            val index = charactersSeen.indexOf(character)
            if (index == -1) {
                charactersSeen.add(character)
                // all characters are distinct till now
                return@forEachIndexed
            }
            // remove all characters till only the distinct characters exists even after adding [character]
            repeat(index + 1) {
                charactersSeen.removeAt(0)
            }
            charactersSeen.add(character)
        }
        throw RuntimeException()
    }

    fun part1(input: String): Int {
        return numberOfCharacterBeforeSeeingTheFirstDistinctSequence(input, 4)
    }

    fun part2(input: String): Int {
        return numberOfCharacterBeforeSeeingTheFirstDistinctSequence(input, 14)
    }

    assert(part1("mjqjpqmgbljsphdztnvjfqwrcgsmlb") == 7)
    assert(part1("bvwbjplbgvbhsrlpgdmjqwftvncz") == 5)
    assert(part1("nppdvjthqldpwncqszvftbrmjlhg") == 6)
    assert(part1("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 10)
    assert(part1("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") == 11)

    assert(part2("mjqjpqmgbljsphdztnvjfqwrcgsmlb") == 19)
    assert(part2("bvwbjplbgvbhsrlpgdmjqwftvncz") == 23)
    assert(part2("nppdvjthqldpwncqszvftbrmjlhg") == 23)
    assert(part2("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 29)
    assert(part2("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") == 26)


    val input = readInput("Day06")
    println(part1(input.first()))
    println(part2(input.first()))
}
