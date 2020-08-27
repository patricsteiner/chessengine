package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import kotlin.math.abs

class Knight(color: Color, initialPosition: Position) : Piece(color, initialPosition) {

    override fun toChar(): Char {
        return if (color == WHITE) 'N' else 'n'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\u2658" else "\u265E"
    }

    override fun hasAbilityToMove(to: Position, deltaX: Int, deltaY: Int): Boolean {
        return (abs(deltaX) == 1 && abs(deltaY) == 2) || (abs(deltaX) == 2 && abs(deltaY) == 1)
    }

    override fun canJumpOverPieces(): Boolean {
        return true
    }

}
