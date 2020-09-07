package io.github.patricsteiner.chessengine.domain

import io.github.patricsteiner.chessengine.domain.piece.Piece
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import java.util.*

class BoardTest {

    class TestPiece(id: String, color: Color, initialPosition: Position, moveCount: Int) : Piece(id, color, initialPosition, moveCount) {

        constructor(color: Color, initialPosition: Position) : this(UUID.randomUUID().toString(), color, initialPosition, 0)

        override fun toChar(): Char {
            return 'T'
        }

        override fun toUnicodeSymbol(): String {
            return "T"
        }

        override fun hasAbilityToMove(to: Position, deltaX: Int, deltaY: Int): Boolean {
            return true
        }
    }

    @Test
    fun applyShouldNotMutateMoveRecord() {
        // given
        val from = Position('a', 1)
        val to = Position('b', 2)
        val piece = TestPiece(WHITE, from)
        val board = Board(listOf(piece))
        val moveRecord = MoveRecord(piece, to)

        // when
        board.apply(moveRecord)

        // then
        assertThat(piece.position).isEqualTo(to)
        assertThat(moveRecord.piece.position).isEqualTo(from)
    }

    @Test
    fun undoShouldNotMutateMoveRecord() {
        // given
        val from = Position('a', 1)
        val to = Position('b', 2)
        val piece = TestPiece(WHITE, from)
        val board = Board(listOf(piece))
        val moveRecord = MoveRecord(piece, to)

        // when
        board.apply(moveRecord)

        // then
        assertThat(piece.position).isEqualTo(to)
        assertThat(moveRecord.piece.position).isEqualTo(from)
    }

    @Test
    fun applyMoveRecord() {
        // given
        val from = Position('a', 1)
        val to = Position('b', 2)
        val piece = TestPiece(WHITE, from)
        val board = Board(listOf(piece))
        val moveRecord = MoveRecord(piece, to)

        // when
        board.apply(moveRecord)

        // then
        assertThat(piece.position).isEqualTo(to)
        assertThat(board[from]).isNull()
        assertThat(board[to]?.id).isEqualTo(piece.id)
    }

    @Test
    fun applyMoveRecordWithVictim() {
        // given
        val from = Position('a', 1)
        val to = Position('b', 2)
        val piece = TestPiece(WHITE, from)
        val victim = TestPiece(WHITE, to)
        val board = Board(listOf(piece, victim))
        val moveRecord = MoveRecord(piece, to, victim)

        // when
        board.apply(moveRecord)

        // then
        assertThat(piece.position).isEqualTo(to)
        assertThat(board[from]).isNull()
        assertThat(board[to]?.id).isEqualTo(piece.id)
        assertThat(board.pieces()).filteredOn { it.id == victim.id }.isEmpty()
    }

    @Test
    fun undoMoveRecordWithoutApplyFirst() {
        // given
        val from = Position('a', 1)
        val to = Position('b', 2)
        val piece = TestPiece(WHITE, from)
        val board = Board(listOf(piece))
        val moveRecord = MoveRecord(piece, to)

        // when
        board.undo(moveRecord)

        // then
        assertThat(board[to]).isNull()
        assertThat(board[from]?.id).isEqualTo(piece.id)
    }

    @Test
    fun undoMoveRecord() {
        // given
        val from = Position('a', 1)
        val to = Position('b', 2)
        val piece = TestPiece(WHITE, from)
        val board = Board(listOf(piece))
        val moveRecord = MoveRecord(piece, to)

        // when
        board.apply(moveRecord)
        board.undo(moveRecord)

        // then
        assertThat(board[to]).isNull()
        assertThat(board[from]?.id).isEqualTo(piece.id)
    }

    @Test
    fun undoMoveRecordWithVictim() {
        // given
        val from = Position('a', 1)
        val to = Position('b', 2)
        val piece = TestPiece(WHITE, from)
        val victim = TestPiece(WHITE, to)
        val board = Board(listOf(piece, victim))
        val moveRecord = MoveRecord(piece, to, victim)

        // when
        board.apply(moveRecord)
        board.undo(moveRecord)

        // then
        assertThat(board[from]?.id).isEqualTo(piece.id)
        assertThat(board[to]?.id).isEqualTo(victim.id)
    }

    @Test
    fun undoMoveRecordWithVictimWithoutApplyFirst() {
        // given
        val from = Position('a', 1)
        val to = Position('b', 2)
        val piece = TestPiece(WHITE, from)
        val victim = TestPiece(WHITE, to)
        val board = Board(listOf(piece, victim))
        val moveRecord = MoveRecord(piece, to, victim)

        // when
        board.undo(moveRecord)

        // then
        assertThat(board[from]?.id).isEqualTo(piece.id)
        assertThat(board[to]?.id).isEqualTo(victim.id)
    }

    @Test
    fun applySomeMoveRecords() {
        // given
        val board = Board()
        board.setupDefaultChessPieces()
        val d2Pawn = board[Position('d', 2)]!!
        val e7Pawn = board[Position('e', 7)]!!
        val moveRecords = listOf(
                MoveRecord(d2Pawn, Position('d', 4)),
                MoveRecord(e7Pawn, Position('e', 5)),
                MoveRecord(d2Pawn, Position('e', 5), e7Pawn)
        )

        // when
        moveRecords.forEach { board.apply(it) }

        // then
        assertThat(board[Position('d', 2)]).isNull()
        assertThat(board[Position('e', 7)]).isNull()
        assertThat(board[Position('e', 5)]?.id).isEqualTo(d2Pawn.id)
        assertThat(board.pieces()).filteredOn { it.id == e7Pawn.id }.isEmpty()
    }

    @Test
    fun applyAndUndoSomeMoveRecords() {
        // given
        val board = Board()
        board.setupDefaultChessPieces()
        val d2Pawn = board[Position('d', 2)]!!
        val e7Pawn = board[Position('e', 7)]!!
        val moveRecords = listOf(
                MoveRecord(d2Pawn, Position('d', 4)),
                MoveRecord(e7Pawn, Position('e', 5)),
                MoveRecord(d2Pawn, Position('e', 5), e7Pawn)
        )

        // when
        moveRecords.forEach { board.apply(it) }
        moveRecords.reversed().forEach { board.undo(it) }

        // then
        assertThat(board[Position('d', 2)]?.id).isEqualTo(d2Pawn.id)
        assertThat(board[Position('e', 7)]?.id).isEqualTo(e7Pawn.id)
        assertThat(board[Position('d', 4)]).isNull()
        assertThat(board[Position('e', 5)]).isNull()
    }

}

