package io.github.patricsteiner.chessengine.domain

import io.github.patricsteiner.chessengine.domain.piece.*
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.BLACK
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Board {

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

    private val pieces = mutableListOf<Piece>()

    fun addPiece(piece: Piece) {
        if (pieces.any { it.position == piece.position }) throw RuntimeException("Cannot have 2 pieces at same position")
        pieces.add(piece)
    }

    fun removePiece(position: Position) {
        pieces.removeIf { it.position == position }
    }

    fun setupPieces() {
        // frontline
        addPiece(Scout(WHITE, Position('a', 2)))
        addPiece(Lumberjack(WHITE, Position('b', 2)))
        addPiece(Pawn(WHITE, Position('c', 2)))
        addPiece(Pawn(WHITE, Position('d', 2)))
        addPiece(Pawn(WHITE, Position('e', 2)))
        addPiece(Pawn(WHITE, Position('f', 2)))
        addPiece(Pawn(WHITE, Position('g', 2)))
        addPiece(Archer(WHITE, Position('h', 2)))

        addPiece(Scout(BLACK, Position('a', 7)))
        addPiece(Lumberjack(BLACK, Position('b', 7)))
        addPiece(Pawn(BLACK, Position('c', 7)))
        addPiece(Pawn(BLACK, Position('d', 7)))
        addPiece(Pawn(BLACK, Position('e', 7)))
        addPiece(Pawn(BLACK, Position('f', 7)))
        addPiece(Pawn(BLACK, Position('g', 7)))
        addPiece(Archer(BLACK, Position('h', 7)))

        // backline
        addPiece(Rook(WHITE, Position('a', 1)))
        addPiece(Rook(WHITE, Position('h', 1)))
        addPiece(Rook(BLACK, Position('a', 8)))
        addPiece(Rook(BLACK, Position('h', 8)))
        addPiece(Knight(WHITE, Position('b', 1)))
        addPiece(Knight(WHITE, Position('g', 1)))
        addPiece(Knight(BLACK, Position('b', 8)))
        addPiece(Knight(BLACK, Position('g', 8)))
        addPiece(Bishop(WHITE, Position('c', 1)))
        addPiece(Bishop(WHITE, Position('f', 1)))
        addPiece(Bishop(BLACK, Position('c', 8)))
        addPiece(Bishop(BLACK, Position('f', 8)))
        addPiece(Queen(WHITE, Position('d', 1)))
        addPiece(Queen(BLACK, Position('d', 8)))
        addPiece(King(WHITE, Position('e', 1)))
        addPiece(King(BLACK, Position('e', 8)))
    }

    fun addAdditionalPieces() {
        addPiece(Scout(WHITE, Position('a', 3)))
        addPiece(Scout(BLACK, Position('a', 6)))
        addPiece(Archer(WHITE, Position('h', 3)))
        addPiece(Archer(BLACK, Position('h', 6)))
        addPiece(Lumberjack(WHITE, Position('g', 3)))
        addPiece(Lumberjack(BLACK, Position('g', 6)))
        addPiece(Baneling(WHITE, Position('b', 3)))
        addPiece(Baneling(BLACK, Position('b', 6)))
    }

    fun setupDefaultChessPieces() {
        pieces.clear()
        for (i in 0 until N_RANKS) {
            addPiece(Pawn(WHITE, Position('a' + i, 2)))
            addPiece(Pawn(BLACK, Position('a' + i, 7)))
        }
        addPiece(Rook(WHITE, Position('a', 1)))
        addPiece(Rook(WHITE, Position('h', 1)))
        addPiece(Rook(BLACK, Position('a', 8)))
        addPiece(Rook(BLACK, Position('h', 8)))
        addPiece(Knight(WHITE, Position('b', 1)))
        addPiece(Knight(WHITE, Position('g', 1)))
        addPiece(Knight(BLACK, Position('b', 8)))
        addPiece(Knight(BLACK, Position('g', 8)))
        addPiece(Bishop(WHITE, Position('c', 1)))
        addPiece(Bishop(WHITE, Position('f', 1)))
        addPiece(Bishop(BLACK, Position('c', 8)))
        addPiece(Bishop(BLACK, Position('f', 8)))
        addPiece(Queen(WHITE, Position('d', 1)))
        addPiece(Queen(BLACK, Position('d', 8)))
        addPiece(King(WHITE, Position('e', 1)))
        addPiece(King(BLACK, Position('e', 8)))
    }

    operator fun get(position: Position): Piece? {
        return pieces.find { it.position == position }
    }

    /**
     * A line is either a file, a rank or a diagonal. If given positions do not form a line, returns true.
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
