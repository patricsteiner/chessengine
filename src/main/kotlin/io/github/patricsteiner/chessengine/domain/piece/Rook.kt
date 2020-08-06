package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE

class Rook(color: Color) : Piece(color) {

    override fun toChar(): Char {
        return if (color == WHITE) 'R' else 'r'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\u2656" else "\u265C"
    }

    override fun canMove(board: Board, from: Position, to: Position, deltaX: Int, deltaY: Int): Boolean {
        return (Board.isOnSameFile(from, to) || Board.isOnSameRank(from, to)) && !board.hasPieceOnLineBetween(from, to)
    }

}
