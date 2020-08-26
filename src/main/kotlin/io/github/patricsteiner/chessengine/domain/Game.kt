package io.github.patricsteiner.chessengine.domain

import io.github.patricsteiner.chessengine.domain.piece.Piece
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import java.util.*
import java.util.UUID.randomUUID

class Game(val id: String, val player1: Player, val player2: Player) {

    companion object {
        fun newGame(player1: Player, player2: Player): Game {
            val id = randomUUID().toString()
            return Game(id, player1, player2)
        }
    }

    val board = Board()
    val moveHistory = mutableListOf<MoveRecord>()
    var turn = WHITE; private set
    var winner: Piece.Color? = null; private set
    var draw: Boolean = false; private set

    fun isOver(): Boolean {
        return winner != null || draw
    }

    fun move(player: Player, from: Position, to: Position) {
        if (isOver()) {
            throw IllegalArgumentException("Game is over")
        }
        if (player.color != turn) {
            throw IllegalArgumentException("It's not ${player.color}'s turn")
        }
        val piece = board[from]
        if (piece != null && player.color != piece.color) {
            throw IllegalArgumentException("$player.color cannot move ${piece.color}'s pieces")
        }
        val moveRecord = board.move(from, to)
        moveHistory.add(moveRecord)
        val enemyColor = player.color.opposite()
        if (board.isCheckMate(enemyColor)) {
            winner = player.color
        } else if (board.isStaleMate(enemyColor)) {
            draw = true
        }
        turn = turn.opposite()
    }

}
