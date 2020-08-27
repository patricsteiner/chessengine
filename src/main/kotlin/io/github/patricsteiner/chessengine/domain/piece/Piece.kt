package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.PieceData
import io.github.patricsteiner.chessengine.domain.Position

/**
 * A Piece must know it's possible moves and captures, _disregarding_ other pieces.
 */
abstract class Piece(val color: Color, initialPosition: Position) {

    var moveCount = 0; private set

    var position = initialPosition; private set

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

    fun move(to: Position) {
        position = to
        moveCount++
    }

    override fun toString(): String {
        return toUnicodeSymbol()
    }

    abstract fun toChar(): Char

    abstract fun toUnicodeSymbol(): String

    /**
     * Checks whether the piece has the ability to move from its current position to the given position,
     * disregarding any other pieces or rules on the board.
     */
    fun hasAbilityToMove(to: Position): Boolean {
        val deltaX = position.x - to.x
        val deltaY = position.y - to.y
        if (deltaX == 0 && deltaY == 0) return false // cannot stay on same position
        return hasAbilityToMove(to, deltaX, deltaY)
    }

    protected abstract fun hasAbilityToMove(to: Position, deltaX: Int, deltaY: Int): Boolean

    /**
     * Checks whether the piece has the ability to capture a piece at the given position,
     * disregarding any other pieces or rules on the board.
     */
    fun hasAbilityToCapture(position: Position): Boolean {
        val deltaX = this.position.x - position.x
        val deltaY = this.position.y - position.y
        return hasAbilityToCapture(position, deltaX, deltaY)
    }

    protected open fun hasAbilityToCapture(position: Position, deltaX: Int, deltaY: Int): Boolean {
        return hasAbilityToMove(position, deltaX, deltaY)
    }

    open fun canJumpOverPieces(): Boolean {
        return false
    }

    fun copy(): Piece {
        val pieceData = PieceData.from(this)
        return of(pieceData.type, pieceData.color, pieceData.position)
    }

}


