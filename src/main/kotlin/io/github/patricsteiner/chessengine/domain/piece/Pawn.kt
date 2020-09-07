package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.BLACK
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import java.util.*
import kotlin.math.abs

class Pawn(id: String, color: Color, initialPosition: Position, moveCount: Int) : Piece(id, color, initialPosition, moveCount) {

    constructor(color: Color, initialPosition: Position) : this(UUID.randomUUID().toString(), color, initialPosition, 0)

    override fun toChar(): Char {
        return if (color == WHITE) 'P' else 'p'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\u2659" else "\u265F"
    }

    override fun hasAbilityToMove(to: Position, deltaX: Int, deltaY: Int): Boolean {
        if (color == WHITE) {
            if ((deltaY == 1 || position.rank == 2 && deltaY == 2) && deltaX == 0) return true
        } else if (color == BLACK) {
            if ((deltaY == -1 || position.rank == 7 && deltaY == -2) && deltaX == 0) return true
        }
        return false
    }

    override fun hasAbilityToCapture(position: Position, deltaX: Int, deltaY: Int): Boolean {
        if (color == WHITE) {
            return abs(deltaX) == 1 && deltaY == 1
        } else if (color == BLACK) {
            return abs(deltaX) == 1 && deltaY == -1
        }
        return false
    }

}
