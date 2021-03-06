package io.github.patricsteiner.chessengine.domain

import io.github.patricsteiner.chessengine.domain.piece.MoveResult
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.BLACK
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import java.util.UUID.randomUUID

typealias GameId = String
typealias ColorToken = String

class Game(val id: GameId, val whiteToken: ColorToken, val blackToken: ColorToken, val requireColorTokens: Boolean) {

    companion object {
        fun newGame(requireColorTokens: Boolean): Game {
            val id = randomUUID().toString().substring(0, 3)
            val whiteToken = randomUUID().toString().substring(0, 3)
            val blackToken = randomUUID().toString().substring(0, 3)
            return Game(id, whiteToken, blackToken, requireColorTokens)
        }
    }

    val board = Board()
    var turn = WHITE; private set
    var winner: Color? = null; private set
    var check: Color? = null; private set
    var draw: Boolean = false; private set
    var latestMoveOrAttack: MoveData? = null; private set

    init {
        board.setupPieces()
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
            throw GameException("Game is over")
        }
        if (color != turn) {
            throw GameException("It's not $color's turn")
        }
        if (board[from] == null) {
            throw GameException("No piece to move")
        }
        if (board[from]?.color != color) {
            throw GameException("Cannot move enemy pieces")
        }
        if (board[from] == null) {
            throw GameException("There is no piece on $from")
        }
        val moveResult = board[from]!!.moveOrAttack(to, board)
        if (moveResult is MoveResult.Error) {
            throw GameException(moveResult.message)
        } else if (moveResult is MoveResult.Success && isCheck(color)) {
            moveResult.undoFunction()
            throw GameException("Cannot move into check")
        }

        // move successful! update game data:
        latestMoveOrAttack = MoveData(from, to)
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

data class MoveData(val from: Position, val to: Position)

