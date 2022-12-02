fun main() {
    fun part1(input: List<String>): Int {
        var totalScores = 0
        input.forEach { line ->
            val (opponentMove, myMove) = line.split(" ")
            when (myMove) {
                "X" -> totalScores += 1
                "Y" -> totalScores += 2
                "Z" -> totalScores += 3
            }
            if ((opponentMove == "A" && myMove == "X") || (opponentMove == "B" && myMove == "Y") || (opponentMove == "C" && myMove == "Z")) {
                totalScores += 3
            } else if ((opponentMove == "A" && myMove == "Y") || (opponentMove == "B" && myMove == "Z") || (opponentMove == "C" && myMove == "X")) {
                totalScores += 6
            }
        }
        return totalScores
    }

    fun part2(input: List<String>): Int {
        var totalScores = 0
        input.forEach { line ->
            val (opponentMove, result) = line.split(" ")
            // X means you need to lose, Y means you need to end the round in a draw, and Z means you need to win.
            when (result) {
                "X" -> totalScores += 0 // lose
                "Y" -> totalScores += 3 // draw
                "Z" -> totalScores += 6 // win
            }

            // A for Rock, B for Paper, and C for Scissors.
            // 1 for Rock, 2 for Paper, and 3 for Scissors
            if (opponentMove == "A") { // rock
                if (result == "X") totalScores += 3 // lose so choose scissors
                if (result == "Y") totalScores += 1 // draw so choose rock
                if (result == "Z") totalScores += 2 // win so choose paper
            } else if (opponentMove == "C") { // scissors
                if (result == "X") totalScores += 2 // lose
                if (result == "Y") totalScores += 3 // draw
                if (result == "Z") totalScores += 1 // win
            } else if (opponentMove == "B") { // paper
                if (result == "X") totalScores += 1 // lose
                if (result == "Y") totalScores += 2 // draw
                if (result == "Z") totalScores += 3 // win
            }
        }
        return totalScores
    }

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
