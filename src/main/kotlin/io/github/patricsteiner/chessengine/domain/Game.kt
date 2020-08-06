package io.github.patricsteiner.chessengine.domain

import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.BLACK
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE

class Game {

    val whitePlayer = Player(WHITE)
    val blackPlayer = Player(BLACK)
    val board = Board()
    val moves = mutableListOf<MoveRecord>()
    val turn = WHITE

    fun start() {
        board.setup()
    }

    fun move(player: Player, from: Position, to: Position) {
        if (turn != player.color) return
        moves.add(board.move(from, to))
    }

    fun isOver(): Boolean {
        return false // TODO
    }

}
