package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.BLACK
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import kotlin.math.abs

class Pawn(color: Piece.Color, position: Position) : Piece(color, position) {

    override fun toChar(): Char {
        return if (color == WHITE) 'P' else 'p'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\u2659" else "\u265F"
    }

    override fun canMove(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        if (deltaX != 0) return false
        if (color == WHITE && deltaY != 1 && !(position.rank == 2 && deltaY == 2)) return false
        if (color == BLACK && deltaY != -1 && !(position.rank == 7 && deltaY == -2)) return false
        if (board.hasPieceOnLineBetween(position, to)) return false
        return true
    }

    override fun canAttack(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        if (board[to] == null) return false
        if (color == WHITE && abs(deltaX) == 1 && deltaY == 1) return true
        if (color == BLACK && abs(deltaX) == 1 && deltaY == -1) return true
        return false
    }

    override fun move(to: Position, board: Board, deltaX: Int, deltaY: Int): MoveResult {
        if (color == WHITE && to.rank == 8 || color == BLACK && to.rank == 1) {
            board.removePiece(position)
            board.addPiece(Queen(color, to))
            val undoFuntion = {
                board.removePiece(to)
                board.addPiece(this)
            }
            return MoveResult.Success(undoFuntion)
        }

        val prevPosition = position
        position = to
        val undoFuntion = {
            position = prevPosition
        }
        return MoveResult.Success(undoFuntion)
    }

    override fun attack(to: Position, board: Board, deltaX: Int, deltaY: Int): MoveResult {
        val victim = board[to] ?: throw RuntimeException("There is no victim on $to")
        if (color == WHITE && to.rank == 8 || color == BLACK && to.rank == 1) {
            board.removePiece(position)
            board.removePiece(to)
            board.addPiece(Queen(color, to))
            val undoFuntion = {
                board.removePiece(to)
                board.addPiece(victim)
                board.addPiece(this)
            }
            return MoveResult.Success(undoFuntion)
        }

        val prevPosition = position
        board.removePiece(to)
        position = to
        val undoFuntion = {
            position = prevPosition
            board.addPiece(victim)
        }
        return MoveResult.Success(undoFuntion)    }


}
