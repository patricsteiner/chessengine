package io.github.patricsteiner.chessengine.domain

import io.github.patricsteiner.chessengine.domain.piece.King2
import io.github.patricsteiner.chessengine.domain.piece.Pawn2
import io.github.patricsteiner.chessengine.domain.piece.Piece2
import io.github.patricsteiner.chessengine.domain.piece.Piece2.Color
import io.github.patricsteiner.chessengine.domain.piece.Piece2.Color.BLACK
import io.github.patricsteiner.chessengine.domain.piece.Piece2.Color.WHITE
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Board2 {

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

    private val pieces = mutableListOf<Piece2>()

    fun setup() {
        for (i in 0 until N_RANKS) {
            pieces.add(Pawn2(WHITE, Position('a' + i, 2)))
            pieces.add(Pawn2(BLACK, Position('a' + i, 7)))
        }
        pieces.add(King2(WHITE, Position('e', 1)))
        pieces.add(King2(BLACK, Position('e', 8)))
    }

    private operator fun get(position: Position): Piece2? {
        return pieces.find { it.position == position }
    }

    fun move(from: Position, to: Position): MoveRecord2 {
        val piece = getOrThrow(from)
        if (this[to] != null) {
            throw IllegalArgumentException("$piece on $from cannot move to $to because position is occupied")
        }
        if (!piece.canMove(to)) {
            throw IllegalArgumentException("$piece on $from cannot move to $to")
        }
        return createAndApplyMoveRecord(piece, from, to, null)
    }

    fun capture(from: Position, to: Position): MoveRecord2 {
        val attacker = getOrThrow(from)
        val victim = getOrThrow(to)
        if (!attacker.canCapture(to)) {
            throw IllegalArgumentException("$attacker on $from cannot capture $victim on $to")
        }
        return createAndApplyMoveRecord(attacker, from, to, victim)
    }

    private fun createAndApplyMoveRecord(piece: Piece2, from: Position, to: Position, captured: Piece2?): MoveRecord2 {
        val moveRecord = MoveRecord2(piece,to, captured)
        apply(moveRecord)
        if (isCheck(moveRecord.piece.color)) {
            undo(moveRecord)
            throw IllegalArgumentException("$piece on $from cannot move to $to (cannot put it's king into check)")
        }
        return moveRecord
    }

    private fun getOrThrow(position: Position): Piece2 {
        return this[position] ?: throw IllegalArgumentException("No piece found on $position")
    }

    private fun apply(moveRecord: MoveRecord2) {
        moveRecord.captured?.let { pieces.removeIf { it.position == moveRecord.to } }
        getOrThrow(moveRecord.piece.position).position = moveRecord.to
    }

    fun undo(moveRecord: MoveRecord2) {
        getOrThrow(moveRecord.piece.position).position = moveRecord.piece.position
        moveRecord.captured?.let { pieces.add(it) }
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
                println("DEBUG: $piece at $it checks $color king at $kingPos")
                check = true
            }
        }
        return check
    }

    fun findKing(color: Color): Position? {
        var kingPos: Position? = null
        forEachPosition {
            if (this[it] is King2 && this[it]?.color == color) kingPos = it
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

}
