package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Position
import kotlin.math.abs

abstract class Piece(val color: Color) {

    enum class Color { BLACK, WHITE }

    override fun toString(): String {
        return toUnicodeSymbol()
    }

    protected abstract fun toChar(): Char

    protected abstract fun toUnicodeSymbol(): String

    fun canMove(board: Board, from: Position, to: Position): Boolean {
        if (board[to]?.color == color) return false // cannot move on a friendly piece
        val deltaX = abs(from.x - to.x)
        val deltaY = abs(from.y - to.y)
        if (deltaX == 0 && deltaY == 0) return false // cannot stay on same position
        return canMove(board, from, to, deltaX, deltaY)
    }

    protected abstract fun canMove(board: Board, from: Position, to: Position, deltaX: Int, deltaY: Int): Boolean

}


