package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.BLACK
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE

class Pawn(color: Color) : Piece(color) {

    override fun toChar(): Char {
        return if (color == WHITE) 'P' else 'p'
    }


    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\u2659" else "\u265F"
    }

    override fun canMove(board: Board, from: Position, to: Position, deltaX: Int, deltaY: Int): Boolean {
        if (color == WHITE) {
            // no backwards moves
            if (from.rank >= to.rank) return false
            // moving forward is allowed when nothing stands in the way
            if ((deltaY == 1 || from.rank == 2 && deltaY == 2) && deltaX == 0 && board[to] == null) return true
            // moving diagonally is allowed when there is an enemy to capture
            if (deltaX == 1 && deltaY == 1 && board[to]?.color != color) return true
        } else if (color == BLACK) {
            if (from.rank <= to.rank) return false
            if ((deltaY == 1 || from.rank == 7 && deltaY == 2) && deltaX == 0 && board[to] == null) return true
            // moving diagonally is allowed when there is an enemy to capture
            if (deltaX == 1 && deltaY == 1 && board[to]?.color != color) return true
        }
        return false
    }

}
