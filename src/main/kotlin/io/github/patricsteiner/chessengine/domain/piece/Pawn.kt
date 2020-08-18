package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.BLACK
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE

class Pawn(color: Color, position: Position) : Piece(color, position) {

    override fun toChar(): Char {
        return if (color == WHITE) 'P' else 'p'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\u2659" else "\u265F"
    }

    override fun canMove(to: Position, deltaX: Int, deltaY: Int): Boolean {
        if (color == WHITE) {
            // no backwards moves
            if (position.rank >= to.rank) return false
            // moving forward is allowed (1 or 2 steps if on starting position)
            if ((deltaY == 1 || position.rank == 2 && deltaY == 2) && deltaX == 0) return true
        } else if (color == BLACK) {
            if (position.rank <= to.rank) return false
            if ((deltaY == 1 || position.rank == 7 && deltaY == 2) && deltaX == 0) return true
        }
        return false
    }

    override fun canCapture(position: Position, deltaX: Int, deltaY: Int): Boolean {
        if (color == WHITE) {
            return deltaX == 1 && deltaY == 1
        } else if (color == BLACK) {
            return  deltaX == 1 && deltaY == -1
        }
        return false
    }
}
