package io.github.patricsteiner.chessengine.application

import io.github.patricsteiner.chessengine.domain.BoardRepository
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece
import io.github.patricsteiner.chessengine.domain.BoardData

class ChessBoardApplicationService(private val boardRepository: BoardRepository) {

    fun move(boardId: String, playerColor: Piece.Color, from: Position, to: Position): BoardData {
        val board = boardRepository.find(boardId) ?: throw RuntimeException("Can't find board")
        board.move(from, to)
        return BoardData.from(board)
    }

    fun get(boardId: String): BoardData {
        val board = boardRepository.find(boardId) ?: throw RuntimeException("Can't find board")
        return BoardData.from(board)
    }

}
