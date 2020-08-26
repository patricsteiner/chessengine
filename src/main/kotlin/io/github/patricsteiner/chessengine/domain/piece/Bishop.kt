package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE

class Bishop(color: Color, initialPosition: Position) : Piece(color, initialPosition) {

    override fun toChar(): Char {
        return if (color == WHITE) 'B' else 'b'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\u2657" else "\u265D"
    }

    override fun canMove(to: Position, deltaX: Int, deltaY: Int): Boolean {
        return Board.isOnSameDiagonal(position, to)
    }

}
