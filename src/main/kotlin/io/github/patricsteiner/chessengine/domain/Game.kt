package io.github.patricsteiner.chessengine.domain

import io.github.patricsteiner.chessengine.domain.piece.Piece.Color
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import java.util.UUID.randomUUID

class Game(val id: String, val player1: Player, val player2: Player) {

    companion object {
        fun newGame(player1: Player, player2: Player): Game {
            val id = randomUUID().toString()
            return Game(id, player1, player2)
        }
    }

    val board = Board()
    private val moveHistory = mutableListOf<MoveRecord>()
    var turn = WHITE; private set
    var winner: Color? = null; private set
    var draw: Boolean = false; private set

    fun isOver(): Boolean {
        return winner != null || draw
    }

    fun isCheckMate(color: Color): Boolean {
        return isCheck(color) && !hasPossibleMoves(color)
    }

    fun isStaleMate(color: Color): Boolean {
        return !isCheck(color) && !hasPossibleMoves(color)
    }

    fun isCheck(color: Color): Boolean {
        var check = false
        val kingPos = board.findKing(color) ?: throw IllegalStateException("There is no king")
        board.eachPosition {
            val piece = board[it]
            if (piece != null && piece.color != color && piece.hasAbilityToCapture(kingPos) &&
                    (piece.canJumpOverPieces() || !board.hasPieceOnLineBetween(piece.position, kingPos))) {
                check = true
            }
        }
        return check
    }

    private fun hasPossibleMoves(color: Color): Boolean {
        return board.pieces().filter { it.color == color }.flatMap { possibleMoves(color, it.position) }.isNotEmpty()
    }

    fun possibleMoves(color: Color, from: Position): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        board.eachPosition {
            try {
                createMoveRecordIfLegal(color, from, it)
                possibleMoves.add(it)
            } catch (e: Exception) {
            }
        }
        return possibleMoves
    }

    fun createMoveRecordIfLegal(color: Color, from: Position, to: Position): MoveRecord {
        val piece = board[from] ?: throw IllegalArgumentException("No piece")
        if (color != piece.color) {
            throw IllegalArgumentException("$color cannot move ${piece.color}'s pieces")
        }
        val victim = board[to]
        val isCapturingMove = victim != null
        if (isCapturingMove) {
            if (piece.color == victim?.color) {
                throw IllegalArgumentException("$piece on $from cannot friendly-fire $victim on $to")
            }
            if (!piece.hasAbilityToCapture(to)) {
                throw IllegalArgumentException("$piece on $from cannot capture $victim on $to")
            }
        } else if (!isCapturingMove && !piece.hasAbilityToMove(to)) {
            throw IllegalArgumentException("$piece on $from cannot move to $to")
        }
        if (!piece.canJumpOverPieces() && board.hasPieceOnLineBetween(from, to)) {
            throw IllegalArgumentException("$piece on $from cannot move to $to because there are other pieces in between")
        }
        val moveRecord = MoveRecord(piece, to, victim)
        board.apply(moveRecord)
        if (isCheck(color)) {
            board.undo(moveRecord)
            throw IllegalArgumentException("$piece on $from cannot move to $to (cannot put it's king into check)")
        }
        board.undo(moveRecord) // always undo the move, in this function we just need to find out if the move is legal
        return moveRecord
    }

    fun move(color: Color, from: Position, to: Position) {
        if (isOver()) {
            throw IllegalArgumentException("Game is over")
        }
        if (color != turn) {
            throw IllegalArgumentException("It's not ${color}'s turn")
        }
        val moveRecord = createMoveRecordIfLegal(color, from, to)
        board.apply(moveRecord)
        moveHistory.add(moveRecord)
        val enemyColor = color.opposite()
        if (isCheckMate(enemyColor)) {
            winner = color
        } else if (isStaleMate(enemyColor)) {
            draw = true
        }
        turn = enemyColor
    }

}
