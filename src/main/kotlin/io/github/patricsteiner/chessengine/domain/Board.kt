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

    var turn = WHITE
        private set

    private val pieces = mutableListOf<Piece>() // TODO could use hashmap or sth instead

    fun setup() {
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
        pieces.add(Queen(WHITE, Position('e', 6))) // just for testing
    }

    private operator fun get(position: Position): Piece? {
        return pieces.find { it.position == position }
    }

    fun move(from: Position, to: Position): MoveRecord {
        val piece = getOrThrow(from)
        if (turn != piece.color) {
            throw IllegalArgumentException("It's not ${piece.color}'s turn")
        }
        val victim = this[to]
        val isCapturingMove = victim != null
        if (isCapturingMove) {
            if (piece.color == victim?.color) {
                throw IllegalArgumentException("$piece on $from cannot friendly-fire $victim on $to")
            }
            if (!piece.canCapture(to)) {
                throw IllegalArgumentException("$piece on $from cannot capture $victim on $to")
            }
        } else if (!isCapturingMove && !piece.canMove(to)) {
            throw IllegalArgumentException("$piece on $from cannot move to $to because position is occupied by $victim")
        }
        if (!piece.canJumpOverPieces() && hasPieceOnLineBetween(from, to)) {
            throw IllegalArgumentException("$piece on $from cannot move to $to because there are other pieces in between")
        }
        val moveRecord = MoveRecord(piece, to, victim)
        apply(moveRecord)
        if (isCheck(moveRecord.piece.color)) {
            undo(moveRecord)
            throw IllegalArgumentException("$piece on $from cannot move to $to (cannot put it's king into check)")
        }
        turn = if (turn == WHITE) BLACK else WHITE
        return moveRecord
    }

    private fun getOrThrow(position: Position): Piece {
        return this[position] ?: throw IllegalArgumentException("No piece found on $position")
    }

    fun apply(moveRecord: MoveRecord) {
        if (moveRecord.victim != null) {
            pieces.removeIf { it.position == moveRecord.victim.position }
        }
        val piece = getOrThrow(moveRecord.piece.position)
        piece.position = moveRecord.to
    }

    fun undo(moveRecord: MoveRecord) {
        val piece = getOrThrow(moveRecord.to)
        piece.position = moveRecord.piece.position
        if (moveRecord.victim != null) {
            pieces.add(moveRecord.victim)
        }
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
        } else if (isOnSameDiagonal(fromExclusive, toExclusive)) {
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
        }
        throw IllegalArgumentException("Positions must be on same rank, file or diagonal")
    }

    fun isCheck(color: Color): Boolean {
        var check = false
        val kingPos = findKing(color) ?: throw IllegalStateException("There is no king")
        forEachPosition {
            val piece = this[it]
            if (piece != null && piece.color != color && piece.canCapture(kingPos)) {
                check = true
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

    private fun forEachPosition(function: (Position) -> Any?): MutableList<Any?> {
        val results = mutableListOf<Any?>()
        for (y in 0 until N_RANKS) {
            for (x in 0 until N_RANKS) {
                val pos = Position(x, y)
                results.add(function(pos))
            }
        }
        return results
    }

    override fun toString(): String {
        val boardData = forEachPosition { this[it]?.toString() }
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

}
