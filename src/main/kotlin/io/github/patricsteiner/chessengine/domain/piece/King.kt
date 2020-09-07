package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import java.util.*
import kotlin.math.abs

class King(id: String, color: Color, initialPosition: Position, moveCount: Int) : Piece(id, color, initialPosition, moveCount) {

    constructor(color: Color, initialPosition: Position) : this(UUID.randomUUID().toString(), color, initialPosition, 0)

    override fun toChar(): Char {
        return if (color == WHITE) 'K' else 'k'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\u2654" else "\u265A"
    }

    override fun hasAbilityToMove(to: Position, deltaX: Int, deltaY: Int): Boolean {
        if (abs(deltaX) > 1 || abs(deltaY) > 1) return false
        return abs(deltaX) + abs(deltaY) <= 2
    }

}
