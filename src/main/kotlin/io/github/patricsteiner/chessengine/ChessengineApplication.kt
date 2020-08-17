package io.github.patricsteiner.chessengine

import io.github.patricsteiner.chessengine.domain.Board2
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece2
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChessengineApplication : CommandLineRunner {

    val board = Board2()

    override fun run(vararg args: String?) {
        board.setup()
        print()
        var input: String? = readLine()
        while (input != null) {
            val from = Position(input[1], input.substring(2, 3).toInt())
            val to = Position(input[3], input.substring(4, 5).toInt())
            if (input[0] == 'm') try {
                board.move(from, to)
            } catch (e: Exception) {
                println(e.message)
            }
            else try {
                board.capture(from, to)
            } catch (e: Exception) {
                println(e.message)
            }
            if (board.isCheck(Piece2.Color.WHITE)) println("WHITE IS CHECK")
            if (board.isCheck(Piece2.Color.BLACK)) println("BLACK IS CHECK")
            print()
            input = readLine()
        }
    }

    fun print() {
        println("==============================")
        println(board)
        println("==============================")
    }


}

fun main(args: Array<String>) {
    runApplication<ChessengineApplication>(*args)
}
