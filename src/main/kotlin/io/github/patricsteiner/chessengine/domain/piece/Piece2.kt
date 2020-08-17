package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece2.Color.BLACK
import io.github.patricsteiner.chessengine.domain.piece.Piece2.Color.WHITE
import kotlin.math.abs

abstract class Piece2(val color: Color, var position: Position) {

    enum class Color { BLACK, WHITE }

    companion object {
        fun fromSerialized(serialized: String): Piece2 {
            val pieceChar = serialized[0]
            val file = serialized[1]
            val rank = serialized[2].toString().toInt()
            val position = Position(file, rank)
            return when (pieceChar) {
                'p' -> Pawn2(BLACK, position)
                'P' -> Pawn2(WHITE, position)
                'k' -> King2(BLACK, position)
                'K' -> King2(WHITE, position)
                else -> throw IllegalArgumentException("Cannot deserialize")
            }
        }
    }

    override fun toString(): String {
        return toUnicodeSymbol()
    }

    abstract fun toChar(): Char


    protected abstract fun toUnicodeSymbol(): String

    final fun canMove(to: Position): Boolean {
        val deltaX = abs(position.x - to.x)
        val deltaY = abs(position.y - to.y)
        if (deltaX == 0 && deltaY == 0) return false // cannot stay on same position
        return canMove(to, deltaX, deltaY)
    }

    protected abstract fun canMove(to: Position, deltaX: Int, deltaY: Int): Boolean

    final fun canCapture(position: Position): Boolean {
        val deltaX = abs(this.position.x - position.x)
        val deltaY = abs(this.position.y - position.y)
        return canCapture(position, deltaX, deltaY)
    }

    protected open fun canCapture(position: Position, deltaX: Int, deltaY: Int): Boolean {
        return canMove(position, deltaX, deltaY)
    }

    final fun serialize(): String {
        return toChar().toString() + this.position
    }

    final fun copy(): Piece2 {
        return fromSerialized(serialize())
    }

}


