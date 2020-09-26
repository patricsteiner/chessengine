package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Position

sealed class MoveResult {
    class Success(val undoFunction: () -> Unit) : MoveResult()
    class Error(val message: String? = null) : MoveResult()
}

abstract class Piece(val color: Color, var position: Position, var moves: Int = 0, var attacks: Int = 0) {

    enum class Color {
        BLACK, WHITE;

        fun opposite(): Color {
            return if (this == WHITE) BLACK else WHITE
        }
    }

    override fun toString(): String {
        return toUnicodeSymbol()
    }

    abstract fun toChar(): Char

    abstract fun toUnicodeSymbol(): String

    protected abstract fun canMove(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean

    protected abstract fun canAttack(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean

    fun canMove(to: Position, board: Board): Boolean {
        val deltaX = position.x - to.x
        val deltaY = position.y - to.y
        return canMove(to, board, deltaX, deltaY)
    }

    fun canAttack(to: Position, board: Board): Boolean {
        val deltaX = position.x - to.x
        val deltaY = position.y - to.y
        return canAttack(to, board, deltaX, deltaY)
    }

    fun moveOrAttack(to: Position, board: Board): MoveResult {
        val deltaX = position.x - to.x
        val deltaY = position.y - to.y
        if (board[to] != null) {
            if (board[to]?.color == color) return MoveResult.Error("Cannot friendly fire")
            if (!canAttack(to, board, deltaX, deltaY)) return MoveResult.Error("Cannot attack $to")
            return attack(to, board, deltaX, deltaY)
        } else {
            if (position == to) return MoveResult.Error("Cannot stay on same position")
            if (!canMove(to, board, deltaX, deltaY)) return MoveResult.Error("Cannot move to $to")
            return move(to, board, deltaX, deltaY)
        }
    }

    protected abstract fun move(to: Position, board: Board, deltaX: Int, deltaY: Int): MoveResult

    protected abstract fun attack(to: Position, board: Board, deltaX: Int, deltaY: Int): MoveResult

    fun possibleMovesOrAttacks(board: Board): List<Position> {
        return board.eachPosition {
            return@eachPosition if (canMove(it, board) || canAttack(it, board)) it else null
        }.filterNotNull()
    }

}
