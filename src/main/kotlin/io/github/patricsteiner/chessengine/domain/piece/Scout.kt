package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE

class Scout(color: Color, position: Position) : BasicPiece(color, position) {

    override fun toChar(): Char {
        return if (color == WHITE) 'S' else 's'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\uD83E\uDD89" else "\uD83E\uDD87"
    }

    override fun canMove(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return true
    }

    override fun canAttack(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return false
    }

}
