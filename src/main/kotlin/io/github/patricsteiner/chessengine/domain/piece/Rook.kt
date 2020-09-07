package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board.Companion.isOnSameFile
import io.github.patricsteiner.chessengine.domain.Board.Companion.isOnSameRank
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import java.util.*

class Rook(id: String, color: Color, initialPosition: Position, moveCount: Int) : Piece(id, color, initialPosition, moveCount) {

    constructor(color: Color, initialPosition: Position) : this(UUID.randomUUID().toString(), color, initialPosition, 0)

    override fun toChar(): Char {
        return if (color == WHITE) 'R' else 'r'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\u2656" else "\u265C"
    }

    override fun hasAbilityToMove(to: Position, deltaX: Int, deltaY: Int): Boolean {
        return (isOnSameFile(position, to) || isOnSameRank(position, to))
    }

}
