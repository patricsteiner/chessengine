package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece2.Color.WHITE

class King2(color: Color, position: Position) : Piece2(color, position) {

    override fun toChar(): Char {
        return if (color == WHITE) 'K' else 'k'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\u2654" else "\u265A"
    }

    override fun canMove(to: Position, deltaX: Int, deltaY: Int): Boolean {
        if (deltaX > 1 || deltaY > 1) return false
        return deltaX + deltaY <= 2
    }

}
