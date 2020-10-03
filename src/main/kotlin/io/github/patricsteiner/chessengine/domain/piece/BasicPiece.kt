package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.GameException
import io.github.patricsteiner.chessengine.domain.Position

abstract class BasicPiece(color: Color, position: Position) : Piece(color, position) {

    override fun move(to: Position, board: Board, deltaX: Int, deltaY: Int): MoveResult {
        val prevPosition = position
        val undoFunction = { position = prevPosition }
        position = to
        return MoveResult.Success(undoFunction)
    }

    override fun attack(to: Position, board: Board, deltaX: Int, deltaY: Int): MoveResult {
        val victim = board[to] ?: throw GameException("There is no victim on $to")
        val prevPosition = position
        board.removePiece(to)
        position = to
        val undoFuntion = {
            position = prevPosition
            board.addPiece(victim)
        }
        return MoveResult.Success(undoFuntion)
    }

}
