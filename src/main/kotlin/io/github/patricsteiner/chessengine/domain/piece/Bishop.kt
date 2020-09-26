package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Board.Companion.isOnSameDiagonal
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE

class Bishop(color: Color, position: Position) : BasicPiece(color, position) {

    override fun toChar(): Char {
        return if (color == WHITE) 'B' else 'b'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\u2657" else "\u265D"
    }

    override fun canMove(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return isOnSameDiagonal(position, to) && !board.hasPieceOnLineBetween(position, to)
    }

    override fun canAttack(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return canMove(to, board, deltaX, deltaY)
    }

}
