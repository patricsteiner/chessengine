package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE

class Rook(color: Color, position: Position) : BasicPiece(color, position) {

    override fun toChar(): Char {
        return if (color == WHITE) 'R' else 'r'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\u2656" else "\u265C"
    }

    override fun canMove(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return (Board.isOnSameRank(position, to) || Board.isOnSameFile(position, to)) && !board.hasPieceOnLineBetween(position, to)
    }

    override fun canAttack(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return canMove(to, board, deltaX, deltaY)
    }

}
