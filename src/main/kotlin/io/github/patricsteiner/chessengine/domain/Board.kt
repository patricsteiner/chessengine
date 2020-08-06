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
            val deltaX = abs(from.x - to.x)
            val deltaY = abs(from.y - to.y)
            return deltaX == deltaY
        }
    }

    private val matrix = Matrix<Piece?>(N_RANKS, N_RANKS)

    fun setup() {
        for (i in 0 until N_RANKS) {
            this[Position('a' + i, 2)] = Pawn(WHITE)
            this[Position('a' + i, 7)] = Pawn(BLACK)
        }
        this[Position('e', 1)] = King(WHITE)
        this[Position('e', 8)] = King(BLACK)
        this[Position('d', 1)] = Queen(WHITE)
        this[Position('d', 8)] = Queen(BLACK)
        this[Position('a', 1)] = Rook(WHITE)
        this[Position('h', 1)] = Rook(WHITE)
        this[Position('a', 8)] = Rook(BLACK)
        this[Position('h', 8)] = Rook(BLACK)
        this[Position('b', 1)] = Knight(WHITE)
        this[Position('g', 1)] = Knight(WHITE)
        this[Position('b', 8)] = Knight(BLACK)
        this[Position('g', 8)] = Knight(BLACK)
        this[Position('c', 1)] = Bishop(WHITE)
        this[Position('f', 1)] = Bishop(WHITE)
        this[Position('c', 8)] = Bishop(BLACK)
        this[Position('f', 8)] = Bishop(BLACK)

    }

    operator fun get(position: Position): Piece? {
        return matrix[position.y, position.x]
    }

    operator fun set(position: Position, piece: Piece?) {
        matrix[position.y, position.x] = piece
    }

    fun move(from: Position, to: Position): MoveRecord {
        val piece = this[from] ?: throw IllegalArgumentException("No piece to move on $from")
        if (!piece.canMove(this, from, to)) {
            throw IllegalArgumentException("$piece on $from cannot move to $to")
        }
        val moveRecord = MoveRecord(piece, from, to, this[to])
        apply(moveRecord)
        if (isCheck(piece.color)) {
            undo(moveRecord)
            throw IllegalArgumentException("$piece on $from cannot move to $to (cannot put it's king into check)")
        }
        return moveRecord
    }

    private fun apply(moveRecord: MoveRecord) {
        this[moveRecord.to] = this[moveRecord.from]
        this[moveRecord.from] = null
    }

    fun undo(moveRecord: MoveRecord) {
        this[moveRecord.to] = moveRecord.caputred
        this[moveRecord.from] = moveRecord.piece
    }

    /**
     * A line is either a file, a rank or a diagonal. Throws error otherwise.
     * From & to are exclusive.
     */
    fun hasPieceOnLineBetween(from: Position, to: Position): Boolean {
        if (isOnSameFile(from, to)) {
            val lo = min(from.y, to.y)
            val hi = max(from.y, to.y)
            for (i in lo + 1 until hi) {
                if (this[Position(from.x, i)] != null) return true
            }
            return false
        } else if (isOnSameRank(from, to)) {
            val lo = min(from.x, to.x)
            val hi = max(from.x, to.x)
            for (i in lo + 1 until hi) {
                if (this[Position(i, from.y)] != null) return true
            }
            return false
        } else if (isOnSameDiagonal(from, to)) {
            val loX = min(from.x, to.x)
            val hiX = max(from.x, to.x)
            val loY = min(from.y, to.y)
            val hiY = max(from.y, to.y)
            var x = loX + 1
            var y = loY + 1
            while (x < hiX && y < hiY) {
                if (this[Position(x, y)] != null) return true
                x++
                y++
            }
            return false
        }
        throw IllegalArgumentException("Positions must be on same rank, file or diagonal")
    }

    fun isCheck(color: Color): Boolean {
        var check = false
        val kingPos = findKing(color) ?: throw IllegalStateException("There is no king")
        forEachPosition {
            val piece = this[it]
            if (piece != null && piece.color != color && piece.canMove(this, it, kingPos)) {
                println("DEBUG: $piece at $it would be able to kill $color king at $kingPos")
                check = true
                return@forEachPosition
            }
        }
        return check
    }

    fun findKing(color: Color): Position? {
        var kingPos: Position? = null
        forEachPosition {
            if (this[it] is King && this[it]?.color == color) kingPos = it
        }
        return kingPos
    }

    private fun forEachPosition(action: (Position) -> Unit) {
        for (rank in 1..N_RANKS) {
            for (file in 'a' until 'a' + N_RANKS) {
                val pos = Position(file, rank)
                action(pos)
            }
        }
    }

    override fun toString(): String {
        return matrix.toString()
    }

}
