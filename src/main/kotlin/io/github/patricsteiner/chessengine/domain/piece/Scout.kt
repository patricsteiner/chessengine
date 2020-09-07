package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import java.util.UUID.randomUUID

class Scout(id: String, color: Color, initialPosition: Position, moveCount: Int) : Piece(id, color, initialPosition, moveCount) {

    constructor(color: Color, initialPosition: Position) : this(randomUUID().toString(), color, initialPosition, 0)

    override fun toChar(): Char {
        return if (color == WHITE) 'S' else 's'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\uD83E\uDD89" else "\uD83E\uDD87"
    }

    override fun hasAbilityToMove(to: Position, deltaX: Int, deltaY: Int): Boolean {
        return true
    }

    override fun hasAbilityToCapture(position: Position, deltaX: Int, deltaY: Int): Boolean {
        return false
    }

    override fun canJumpOverPieces(): Boolean {
        return true
    }

}
