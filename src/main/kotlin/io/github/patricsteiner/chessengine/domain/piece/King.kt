package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import kotlin.math.abs

class King(color: Color, initialPosition: Position) : Piece(color, initialPosition) {

    override fun toChar(): Char {
        return if (color == WHITE) 'K' else 'k'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\u2654" else "\u265A"
    }

    override fun canMove(to: Position, deltaX: Int, deltaY: Int): Boolean {
        if (abs(deltaX) > 1 || abs(deltaY) > 1) return false
        return abs(deltaX) + abs(deltaY) <= 2
    }

}
