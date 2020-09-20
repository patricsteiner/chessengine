package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import java.util.*
import kotlin.math.abs

class Archer(id: String, color: Color, initialPosition: Position, moveCount: Int) : Piece(id, color, initialPosition, moveCount) {

    constructor(color: Color, initialPosition: Position) : this(UUID.randomUUID().toString(), color, initialPosition, 0)

    override fun toChar(): Char {
        return if (color == WHITE) 'A' else 'a'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\uD83C\uDFF9" else "\u2650"
    }

    override fun hasAbilityToMove(to: Position, deltaX: Int, deltaY: Int): Boolean {
        return abs(deltaX) == 1 || abs(deltaY) == 1
    }

    override fun hasAbilityToCapture(position: Position, deltaX: Int, deltaY: Int): Boolean {
        return abs(deltaX) == 3 && abs(deltaY) == 3
    }

    override fun isMelee(): Boolean {
        return false
    }
}
