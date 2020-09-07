package io.github.patricsteiner.chessengine.domain

import io.github.patricsteiner.chessengine.domain.piece.*
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.BLACK
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * The Chessbaord can just apply or undo a MoveRecord, but it is not responsible for checking any rules. This is done in the Game class.
 */
class Board(pieces: List<Piece> = mutableListOf()) {

    companion object {
        const val N_RANKS = 8
        fun isOnSameRank(from: Position, to: Position): Boolean {
            return from.rank == to.rank
        }

        fun isOnSameFile(from: Position, to: Position): Boolean {
            return from.file == to.file
        }

        fun isOnSameDiagonal(from: Position, to: Position): Boolean {
            val deltaX = from.x - to.x
            val deltaY = from.y - to.y
            return abs(deltaX) == abs(deltaY)
        }

        // Top left to bottom right
        fun isOnSameMainDiagonal(from: Position, to: Position): Boolean {
            if (from == to) return true
            val isTopLeftToBottomRight = from.x < to.x && from.y < to.y
            val isBottomRightToTopLeft = from.x > to.x && from.y > to.y
            return isOnSameDiagonal(from, to) && (isTopLeftToBottomRight || isBottomRightToTopLeft)
        }

        // Top right to bottom left
        fun isOnSameAntiDiagonal(from: Position, to: Position): Boolean {
            if (from == to) return true
            return isOnSameDiagonal(from, to) && !isOnSameMainDiagonal(from, to)
        }
    }

    private val pieces = pieces.toMutableList()  // could use hashmap or sth instead

    fun addAdditionalPieces() {
        pieces.add(Scout(WHITE, Position('a', 3)))
        pieces.add(Scout(BLACK, Position('a', 6)))
        pieces.add(Archer(WHITE, Position('h', 3)))
        pieces.add(Archer(BLACK, Position('h', 6)))
    }

    fun setupDefaultChessPieces() {
        pieces.clear()
        for (i in 0 until N_RANKS) {
            pieces.add(Pawn(WHITE, Position('a' + i, 2)))
            pieces.add(Pawn(BLACK, Position('a' + i, 7)))
        }
        pieces.add(Rook(WHITE, Position('a', 1)))
        pieces.add(Rook(WHITE, Position('h', 1)))
        pieces.add(Rook(BLACK, Position('a', 8)))
        pieces.add(Rook(BLACK, Position('h', 8)))
        pieces.add(Knight(WHITE, Position('b', 1)))
        pieces.add(Knight(WHITE, Position('g', 1)))
        pieces.add(Knight(BLACK, Position('b', 8)))
        pieces.add(Knight(BLACK, Position('g', 8)))
        pieces.add(Bishop(WHITE, Position('c', 1)))
        pieces.add(Bishop(WHITE, Position('f', 1)))
        pieces.add(Bishop(BLACK, Position('c', 8)))
        pieces.add(Bishop(BLACK, Position('f', 8)))
        pieces.add(Queen(WHITE, Position('d', 1)))
        pieces.add(Queen(BLACK, Position('d', 8)))
        pieces.add(King(WHITE, Position('e', 1)))
        pieces.add(King(BLACK, Position('e', 8)))
    }

    operator fun get(position: Position): Piece? {
        return pieces.find { it.position == position }
    }

    private fun get(id: String): Piece {
        return pieces.find { it.id == id } ?: throw IllegalArgumentException("No piece found with id $id")
    }

    private fun remove(id: String) {
        val removed = pieces.removeIf { it.id == id }
        if (!removed) {
            throw IllegalStateException("There is no piece with id $id")
        }
    }

    fun apply(moveRecord: MoveRecord) {
        val piece = get(moveRecord.piece.id)
        piece.move(moveRecord.to)
        if (moveRecord.victim != null) {
            remove(moveRecord.victim.id)
        }
        if (moveRecord.promotionTo != null) {
            remove(moveRecord.piece.id)
            pieces.add(moveRecord.promotionTo.toPiece())
        }
        moveRecord.combinedMove?.let { apply(it) }
    }

    fun undo(moveRecord: MoveRecord) {
        remove(moveRecord.piece.id)
        pieces.add(moveRecord.piece.toPiece())
        if (moveRecord.victim != null) {
            pieces.add(moveRecord.victim.toPiece())
        }
        if (moveRecord.promotionTo != null) {
            remove(moveRecord.promotionTo.id)
        }
        moveRecord.combinedMove?.let { undo(it) }
    }

    /**
     * A line is either a file, a rank or a diagonal. Throws error otherwise.
     */
    fun hasPieceOnLineBetween(fromExclusive: Position, toExclusive: Position): Boolean {
        if (isOnSameFile(fromExclusive, toExclusive)) {
            val lo = min(fromExclusive.y, toExclusive.y)
            val hi = max(fromExclusive.y, toExclusive.y)
            for (i in lo + 1 until hi) {
                if (this[Position(fromExclusive.x, i)] != null) return true
            }
            return false
        } else if (isOnSameRank(fromExclusive, toExclusive)) {
            val lo = min(fromExclusive.x, toExclusive.x)
            val hi = max(fromExclusive.x, toExclusive.x)
            for (i in lo + 1 until hi) {
                if (this[Position(i, fromExclusive.y)] != null) return true
            }
            return false
        } else if (isOnSameMainDiagonal(fromExclusive, toExclusive)) {
            val loX = min(fromExclusive.x, toExclusive.x)
            val hiX = max(fromExclusive.x, toExclusive.x)
            val loY = min(fromExclusive.y, toExclusive.y)
            val hiY = max(fromExclusive.y, toExclusive.y)
            var x = loX + 1
            var y = loY + 1
            while (x < hiX && y < hiY) {
                if (this[Position(x, y)] != null) return true
                x++
                y++
            }
            return false
        } else if (isOnSameAntiDiagonal(fromExclusive, toExclusive)) {
            val loX = min(fromExclusive.x, toExclusive.x)
            val hiX = max(fromExclusive.x, toExclusive.x)
            val loY = min(fromExclusive.y, toExclusive.y)
            val hiY = max(fromExclusive.y, toExclusive.y)
            var x = loX + 1
            var y = hiY - 1
            while (x < hiX && y > loY) {
                if (this[Position(x, y)] != null) return true
                x++
                y--
            }
            return false
        }
        return true
//        throw IllegalArgumentException("Positions must be on same rank, file or diagonal")
    }

    fun findKing(color: Color): Position? {
        return pieces
                .filterIsInstance<King>()
                .filter { it.color == color }
                .map { it.position }
                .firstOrNull()
    }

    internal fun <R> eachPosition(function: (Position) -> R): List<R> {
        val results = mutableListOf<R>()
        for (y in 0 until N_RANKS) {
            for (x in 0 until N_RANKS) {
                val pos = Position(x, y)
                results.add(function(pos))
            }
        }
        return results
    }

    override fun toString(): String {
        val boardData = eachPosition { this[it]?.toString() }
        val sb = StringBuilder()
        boardData.forEachIndexed { idx, pieceAsString ->
            sb.append(pieceAsString ?: "\u2610").append("\t")
            if ((idx + 1) % N_RANKS == 0) {
                sb.append("\n")
            }
        }
        return sb.toString()
    }

    fun pieces(): List<Piece> {
        return pieces.toList()
    }

    fun asMatrix(): List<List<Piece?>> {
        val boardData = eachPosition { this[it] }
        return boardData.chunked(N_RANKS)
    }

}
