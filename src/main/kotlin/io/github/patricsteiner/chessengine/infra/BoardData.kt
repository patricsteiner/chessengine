package io.github.patricsteiner.chessengine.infra

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece

data class BoardData(
        val turn: Piece.Color,
        val pieces: List<PieceData>
) {
    companion object {
        fun from(board: Board): BoardData {
            return BoardData(board.turn, board.pieces().map { PieceData.from(it) })
        }
    }
}

data class PieceData(
        val char: Char,
        val color: Piece.Color,
        val unicodeSymbol: String,
        val position: Position
) {
    companion object {
        fun from(piece: Piece): PieceData {
            return PieceData(piece.toChar(), piece.color, piece.toUnicodeSymbol(), piece.position)
        }
    }
}
