package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import kotlin.math.abs

class King(color: Color, position: Position) : BasicPiece(color, position) {

    override fun toChar(): Char {
        return if (color == WHITE) 'K' else 'k'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\u2654" else "\u265A"
    }

    override fun canMove(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean { // TODO castling
        return (abs(deltaX) <= 1 && abs(deltaY) <= 1 && abs(deltaX) + abs(deltaY) <= 2)
    }

    override fun canAttack(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return canMove(to, board, deltaX, deltaY)
    }

}
