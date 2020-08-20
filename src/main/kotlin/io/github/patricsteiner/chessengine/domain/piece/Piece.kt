package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.PieceData
import kotlin.math.abs

/**
 * A Piece must know it's possible moves and captures, _disregarding_ other pieces.
 */
abstract class Piece(val color: Color, var position: Position) {

    enum class Color { BLACK, WHITE }

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

    fun canMove(to: Position): Boolean {
        val deltaX = abs(position.x - to.x)
        val deltaY = abs(position.y - to.y)
        if (deltaX == 0 && deltaY == 0) return false // cannot stay on same position
        return canMove(to, deltaX, deltaY)
    }

    protected abstract fun canMove(to: Position, deltaX: Int, deltaY: Int): Boolean

    fun canCapture(position: Position): Boolean {
        val deltaX = abs(this.position.x - position.x)
        val deltaY = abs(this.position.y - position.y)
        return canCapture(position, deltaX, deltaY)
    }

    protected open fun canCapture(position: Position, deltaX: Int, deltaY: Int): Boolean {
        return canMove(position, deltaX, deltaY)
    }

    open fun canJumpOverPieces(): Boolean {
        return false
    }
//
//    fun serialize(): String {
//        return toChar().toString() + this.position
//    }

    fun copy(): Piece {
        val pieceData = PieceData.from(this)
        return of(pieceData.type, pieceData.color, pieceData.position)
//        return fromSerialized(serialize())
    }

}


