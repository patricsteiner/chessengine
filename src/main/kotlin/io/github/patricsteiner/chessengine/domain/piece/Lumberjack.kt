package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board.Companion.isOnSameFile
import io.github.patricsteiner.chessengine.domain.Board.Companion.isOnSameRank
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import java.util.*
import kotlin.math.abs

class Lumberjack(id: String, color: Color, initialPosition: Position, moveCount: Int) : Piece(id, color, initialPosition, moveCount) {

    constructor(color: Color, initialPosition: Position) : this(UUID.randomUUID().toString(), color, initialPosition, 0)

    override fun toChar(): Char {
        return if (color == WHITE) 'L' else 'l'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "L" else "l"
    }

    override fun hasAbilityToMove(to: Position, deltaX: Int, deltaY: Int): Boolean {
        return abs(deltaX) == 1 || abs(deltaY) == 1 // TODO why is this NOT buggy? x=1 => he can move any y?
    }

    override fun hasAbilityToCapture(position: Position, deltaX: Int, deltaY: Int): Boolean {
        return false
    }

}
