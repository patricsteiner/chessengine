package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE

class Baneling(color: Piece.Color, position: Position) : Piece(color, position) {

    override fun toChar(): Char {
        return if (color == WHITE) 'L' else 'l'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "BL" else "bl"
    }

    override fun canMove(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return Board.isOnSameDiagonal(position, to) && !board.hasPieceOnLineBetween(position, to)
    }

    override fun canAttack(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return Board.isOnSameFile(position, to) && !board.hasPieceOnLineBetween(position, to)
    }

    override fun move(to: Position, board: Board, deltaX: Int, deltaY: Int): MoveResult {
        val prevPosition = position
        position = to
        val undoFunction = {
            position = prevPosition
        }
        return MoveResult.Success(undoFunction)
    }

    override fun attack(to: Position, board: Board, deltaX: Int, deltaY: Int): MoveResult {
        val victim = board[to] ?: throw RuntimeException("There is no victim on $to")
        val aoe = Position(to.x, to.y + if (deltaY > 0) -1 else 1)
        // this aoe damage is kind of tricky, because it is not considered in canAttack().. therefore we just make the king immune to aoe for now to avoid complications.
        val aoeVictim = if (board[aoe] != null && board[aoe] !is King) board[aoe] else null
        board.removePiece(to)
        board.removePiece(aoe)
        board.removePiece(position)
        val undoFunction = {
            board.addPiece(this)
            board.addPiece(victim)
            if (aoeVictim != null) board.addPiece(aoeVictim)
        }
        return MoveResult.Success(undoFunction)
    }

}
