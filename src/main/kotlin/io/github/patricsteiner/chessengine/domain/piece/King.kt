package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import kotlin.math.abs

class King(color: Color, position: Position) : Piece(color, position) {

    override fun toChar(): Char {
        return if (color == WHITE) 'K' else 'k'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "\u2654" else "\u265A"
    }

    override fun canMove(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean { // TODO castling
        val (castlePiece, _) = getCastlePartner(to, board)
        if (castlePiece != null) return true
        return (abs(deltaX) <= 1 && abs(deltaY) <= 1 && abs(deltaX) + abs(deltaY) <= 2)
    }

    override fun canAttack(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return canMove(to, board, deltaX, deltaY)
    }

    override fun move(to: Position, board: Board, deltaX: Int, deltaY: Int): MoveResult {
        val (castlePiece, castlePieceNewPosition) = getCastlePartner(to, board) // TODO should also check if king.moves == 0 (but actually kind of funny to also allow castle after moving :))
        return if (castlePiece != null && castlePieceNewPosition != null) {
            val prevPosition = position
            val castlePiecePrevPosition = castlePiece.position
            val undoFunction = {
                position = prevPosition
                castlePiece.position = castlePiecePrevPosition
            }
            position = to
            castlePiece.position = castlePieceNewPosition
            MoveResult.Success(undoFunction)
        } else {
            val prevPosition = position
            val undoFunction = { position = prevPosition }
            position = to
            MoveResult.Success(undoFunction)
        }
    }

    override fun attack(to: Position, board: Board, deltaX: Int, deltaY: Int): MoveResult {
        val victim = board[to] ?: throw RuntimeException("There is no victim on $to")
        val prevPosition = position
        board.removePiece(to)
        position = to
        val undoFuntion = {
            position = prevPosition
            board.addPiece(victim)
        }
        return MoveResult.Success(undoFuntion)
    }

    // TODO should also check that the King cannot move through an attacked field
    private fun getCastlePartner(to: Position, board: Board): Pair<Piece?, Position?> {
        var castlePiece: Piece? = null
        var castlePieceNewPosition: Position? = null
        if (to.rank == 1) {
            if (to.file == 'c' &&
                    board[Position('a', 1)]?.moves == 0 &&
                    board[Position('b', 1)] == null &&
                    board[Position('c', 1)] == null &&
                    board[Position('d', 1)] == null) {
                castlePiece = board[Position('a', 1)] as Rook
                castlePieceNewPosition = Position('d', 1)
            } else if (to.file == 'g' &&
                    board[Position('h', 1)]?.moves == 0 &&
                    board[Position('f', 1)] == null &&
                    board[Position('g', 1)] == null) {
                castlePiece = board[Position('h', 1)] as Rook
                castlePieceNewPosition = Position('f', 1)
            }
        } else if (to.rank == 8) {
            if (to.file == 'c' &&
                    board[Position('a', 8)]?.moves == 0 &&
                    board[Position('b', 8)] == null &&
                    board[Position('c', 8)] == null &&
                    board[Position('d', 8)] == null) {
                castlePiece = board[Position('a', 8)] as Rook
                castlePieceNewPosition = Position('d', 8)
            } else if (to.file == 'g' &&
                    board[Position('h', 8)]?.moves == 0 &&
                    board[Position('f', 8)] == null &&
                    board[Position('g', 8)] == null) {
                castlePiece = board[Position('h', 8)] as Rook
                castlePieceNewPosition = Position('f', 8)
            }
        }
        return Pair(castlePiece, castlePieceNewPosition)
    }

}
