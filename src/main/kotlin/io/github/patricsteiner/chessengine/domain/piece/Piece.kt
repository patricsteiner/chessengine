package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.BLACK
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import kotlin.math.abs

/**
 * A Piece must know it's possible moves and captures, _disregarding_ other pieces.
 */
abstract class Piece(val color: Color, var position: Position) {

    enum class Color { BLACK, WHITE }

    companion object {
//
//        val pieceToChar = mapOf(
//                Pawn::class to 'p',
//                Rook::class to 'r',
//                Knight::class to 'n',
//                Bishop::class to 'b',
//                Queen::class to 'q',
//                King::class to 'k'
//        )
//        val charToPiece = pieceToChar.entries.associateBy({ it.value }) { it.key }
//        return pieceClass.java.getConstructor(Color::class.java, Position::class.java).newInstance(color, position)


        fun fromSerialized(serialized: String): Piece {
            val pieceChar = serialized[0]
            val file = serialized[1]
            val rank = serialized[2].toString().toInt()
            val position = Position(file, rank)
            return when (pieceChar) {
                'p' -> Pawn(BLACK, position)
                'P' -> Pawn(WHITE, position)
                'r' -> Rook(BLACK, position)
                'R' -> Rook(WHITE, position)
                'n' -> Knight(BLACK, position)
                'N' -> Knight(WHITE, position)
                'b' -> Bishop(BLACK, position)
                'B' -> Bishop(WHITE, position)
                'q' -> Queen(BLACK, position)
                'Q' -> Queen(WHITE, position)
                'k' -> King(BLACK, position)
                'K' -> King(WHITE, position)
                else -> throw IllegalArgumentException("Cannot deserialize $serialized")
            }
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

    fun serialize(): String {
        return toChar().toString() + this.position
    }

    fun copy(): Piece {
        return fromSerialized(serialize())
    }

}


