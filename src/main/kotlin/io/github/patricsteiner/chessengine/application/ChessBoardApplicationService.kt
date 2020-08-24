package io.github.patricsteiner.chessengine.application

import io.github.patricsteiner.chessengine.domain.BoardRepository
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece
import io.github.patricsteiner.chessengine.domain.BoardData
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.BLACK
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE

class ChessBoardApplicationService(private val boardRepository: BoardRepository) {

    fun move(boardId: String, playerColor: Piece.Color, from: Position, to: Position): BoardData {
        val board = boardRepository.find(boardId) ?: throw RuntimeException("Can't find board")
        if (playerColor != board.turn) {
            throw IllegalArgumentException("It's not ${playerColor}'s turn")
        }
        val piece = board[from]
        if (piece != null && playerColor != piece.color) {
            throw IllegalArgumentException("$playerColor cannot move ${piece.color}'s pieces")
        }
        board.move(from, to)
        board.turn = if (board.turn == WHITE) BLACK else WHITE
        return BoardData.from(board)
    }

    fun possibleMoves(boardId: String, from: Position): List<Position> {
        val board = boardRepository.find(boardId) ?: throw RuntimeException("Can't find board")
        return board.possibleMoves(from)
    }

    fun get(boardId: String): BoardData {
        val board = boardRepository.find(boardId) ?: throw RuntimeException("Can't find board")
        return BoardData.from(board)
    }

}
