package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.*

class Knight(color: Color) : Piece(color) {

    override fun toChar(): Char {
        return if (color == WHITE) 'N' else 'n'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\u2658" else "\u265E"
    }

    override fun canMove(board: Board, from: Position, to: Position, deltaX: Int, deltaY: Int): Boolean {
        return (deltaX == 1 && deltaY == 2) || (deltaX == 2 && deltaY == 1)
    }

}
