package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import kotlin.math.abs

class Lumberjack(color: Color, position: Position) : BasicPiece(color, position) {

    override fun toChar(): Char {
        return if (color == WHITE) 'L' else 'l'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "L" else "l"
    }

    override fun canMove(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return abs(deltaX) <= 1 && abs(deltaY) <= 1 && abs(deltaX) + abs(deltaY) <= 2
    }

    override fun canAttack(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return board[to] is Tree
    }

    override fun move(to: Position, board: Board, deltaX: Int, deltaY: Int): MoveResult {
        val prevPosition = position
        position = to
        board.addPiece(Tree(color, prevPosition))
        val undoFunction = {
            board.removePiece(prevPosition)
            position = prevPosition
        }
        return MoveResult.Success(undoFunction)
    }


}
