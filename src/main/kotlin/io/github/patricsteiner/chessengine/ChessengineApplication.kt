package io.github.patricsteiner.chessengine

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Position
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import io.github.patricsteiner.chessengine.domain.piece.Piece

@SpringBootApplication
class ChessengineApplication : CommandLineRunner {

	val board = Board()

	override fun run(vararg args: String?) {
		board.setup()
		print()
		var input: String? = readLine()
		while (input != null) {
			val from = Position(input[0], input.substring(1, 2).toInt())
			val to = Position(input[2], input.substring(3, 4).toInt())
			try {
				board.move(from, to)
			} catch (e: Exception) {
				println(e.message)
			}
			if (board.isCheck(Piece.Color.WHITE)) println("WHITE IS CHECK")
			if (board.isCheck(Piece.Color.BLACK)) println("BLACK IS CHECK")
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
