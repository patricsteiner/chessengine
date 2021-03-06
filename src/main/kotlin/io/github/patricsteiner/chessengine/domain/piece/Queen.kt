package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Board.Companion.isOnSameDiagonal
import io.github.patricsteiner.chessengine.domain.Board.Companion.isOnSameFile
import io.github.patricsteiner.chessengine.domain.Board.Companion.isOnSameRank
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE

class Queen(color: Color, position: Position) : BasicPiece(color, position) {

    override fun toChar(): Char {
        return if (color == WHITE) 'Q' else 'q'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\u2655" else "\u265B"
    }

    override fun canMove(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return (isOnSameFile(position, to) || isOnSameRank(position, to) || isOnSameDiagonal(position, to))
                && !board.hasPieceOnLineBetween(position, to)
    }

    override fun canAttack(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return canMove(to, board, deltaX, deltaY)
    }

}
