package io.github.patricsteiner.chessengine.domain

import io.github.patricsteiner.chessengine.domain.piece.MoveResult
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.BLACK
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import java.util.UUID.randomUUID

typealias GameId = String
typealias ColorToken = String

class Game(val id: GameId, val whiteToken: ColorToken, val blackToken: ColorToken) {

    companion object {
        fun newGame(): Game {
            val id = randomUUID().toString().substringBefore("-")
            val whiteToken = randomUUID().toString().substringBefore("-")
            val blackToken = randomUUID().toString().substringBefore("-")
            return Game(id, whiteToken, blackToken)
        }
    }

    val board = Board()
    var turn = WHITE; private set
    var winner: Color? = null; private set
    var check: Color? = null; private set
    var draw: Boolean = false; private set

    init {
        board.setupDefaultChessPieces()
        board.addAdditionalPieces()
    }

    fun colorFromToken(colorToken: ColorToken): Color? {
        if (colorToken == whiteToken) return WHITE
        if (colorToken == blackToken) return BLACK
        return null
    }

    private fun isOver(): Boolean {
        return winner != null || draw
    }

    fun isCheck(color: Color): Boolean {
        val kingPos = board.findKing(color) ?: throw IllegalStateException("There is no king")
        return board.pieces()
                .filter { it.color == color.opposite() }
                .any { it.canAttack(kingPos, board) }
    }

    private fun hasLegalMoves(color: Color): Boolean {
        return board.pieces().filter { it.color == color }.flatMap { legalMoves(it.position) }.isNotEmpty()
    }

    fun legalMoves(from: Position): List<Position> {
        val piece = board[from] ?: return listOf()
        return piece.possibleMovesOrAttacks(board)
                .filter {
                    // We need to actually do a move to see if it results in a check. It is only legal if it does not. Then undo it again.
                    val moveResult = piece.moveOrAttack(it, board)
                    var filterResult = false
                    if (moveResult is MoveResult.Success) {
                        filterResult = !isCheck(piece.color)
                        moveResult.undoFunction()
                    }
                    return@filter filterResult
                }
    }

    fun moveOrAttack(color: Color, from: Position, to: Position) {
        if (isOver()) {
            throw RuntimeException("Game is over")
        }
        if (color != turn) {
            throw RuntimeException("It's not $color's turn")
        }
        if (board[from]?.color != color) {
            throw RuntimeException("Cannot move enemy pieces")
        }
        if (board[from] == null) {
            throw RuntimeException("There is no piece on $from")
        }
        val moveResult = board[from]!!.moveOrAttack(to, board)
        if (moveResult is MoveResult.Error) {
            throw RuntimeException(moveResult.message)
        } else if (moveResult is MoveResult.Success && isCheck(color)) {
            moveResult.undoFunction()
            throw RuntimeException("Cannot move into check")
        }
        val enemyColor = turn.opposite()
        check = if (isCheck(enemyColor)) enemyColor else null
        if (!hasLegalMoves(enemyColor)) {
            if (check == enemyColor) {
                winner = turn // checkmate
            } else {
                draw = true // stalemate
            }
        }
        turn = enemyColor
    }

}
