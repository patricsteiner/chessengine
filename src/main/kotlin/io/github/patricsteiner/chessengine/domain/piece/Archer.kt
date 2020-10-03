package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.GameException
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import kotlin.math.abs

class Archer(color: Color, position: Position) : BasicPiece(color, position) {

    override fun toChar(): Char {
        return if (color == WHITE) 'A' else 'a'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\uD83C\uDFF9" else "\u2650"
    }

    override fun canMove(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return (abs(deltaX) <= 1 && abs(deltaY) <= 1 && abs(deltaX) + abs(deltaY) <= 2)
    }

    override fun canAttack(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return abs(deltaX) == 3 && abs(deltaY) == 3
    }

    override fun attack(to: Position, board: Board, deltaX: Int, deltaY: Int): MoveResult {
        val victim = board[to] ?: throw GameException("There is no victim on $to")
        board.removePiece(to)
        val undoFunction = {
            board.addPiece(victim)
        }
        return MoveResult.Success(undoFunction)
    }

}
