fun main() {
    class CathodeRayTube {
        private var cycle = 0
            set(value) {
                if (cycle % 40 in (x - 1)..(x + 1)) crt[field] = "#"
                field = value
            }
        private var x = 1

        private val crt = Array(40 * 6) { " " }

        var signalStrength = 0
            private set

        fun executeInstruction(instr: String) {
            if (instr == "noop") {
                cycle++
                updateSignalStrength()
            } else {
                val (cmd, value) = instr.split(" ")
                require(cmd == "addx")
                repeat(2) {
                    cycle++
                    updateSignalStrength()
                }
                x += value.toInt()
            }
        }

        private fun updateSignalStrength() {
            if (cycle in arrayOf(20, 60, 100, 140, 180, 220)) {
                signalStrength += cycle * x
            }
        }

        fun print() {
            for (i in crt.indices) {
                if (i % 40 == 0) println()
                print(crt[i].padEnd(2))
            }
        }
    }

    fun part1(input: List<String>): Int {
        val tube = CathodeRayTube()
        input.forEach {
            tube.executeInstruction(it)
        }
        return tube.signalStrength
    }

    fun part2(input: List<String>): Int {
        val tube = CathodeRayTube()
        input.forEach {
            tube.executeInstruction(it)
        }
        tube.print()
        return tube.signalStrength
    }

    run {
        val input = readInput("Day10")
        println(part1(input))
        part2(input)
    }
}
