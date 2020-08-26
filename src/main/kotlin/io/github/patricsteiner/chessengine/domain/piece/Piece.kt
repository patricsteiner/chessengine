package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.PieceData
import io.github.patricsteiner.chessengine.domain.Position

/**
 * A Piece must know it's possible moves and captures, _disregarding_ other pieces.
 */
abstract class Piece(val color: Color, initialPosition: Position) {

    var moveCount = 0; private set

    var position = initialPosition
        set(value) {
            moveCount++
            field = value
        }

    enum class Color {
        BLACK, WHITE;

        fun opposite(): Color {
            return if (this == WHITE) BLACK else WHITE
        }
    }

    companion object {
        fun of(type: Class<out Piece>, color: Color, position: Position): Piece {
            return type.getConstructor(Color::class.java, Position::class.java).newInstance(color, position)
        }
    }

    override fun toString(): String {
        return toUnicodeSymbol()
    }

    abstract fun toChar(): Char

    abstract fun toUnicodeSymbol(): String

    /**
     * Checks whether the piece has the ability to move to given position, disregarding any other pieces or rules on the board.
     */
    fun canMove(to: Position): Boolean {
        val deltaX = position.x - to.x
        val deltaY = position.y - to.y
        if (deltaX == 0 && deltaY == 0) return false // cannot stay on same position
        return canMove(to, deltaX, deltaY)
    }

    protected abstract fun canMove(to: Position, deltaX: Int, deltaY: Int): Boolean

    /**
     * Checks whether the piece has the ability to capture a piece at the given position, disregarding any other pieces or rules on the board.
     */
    fun canCapture(position: Position): Boolean {
        val deltaX = this.position.x - position.x
        val deltaY = this.position.y - position.y
        return canCapture(position, deltaX, deltaY)
    }

    protected open fun canCapture(position: Position, deltaX: Int, deltaY: Int): Boolean {
        return canMove(position, deltaX, deltaY)
    }

    open fun canJumpOverPieces(): Boolean {
        return false
    }

    fun copy(): Piece {
        val pieceData = PieceData.from(this)
        return of(pieceData.type, pieceData.color, pieceData.position)
    }

}


