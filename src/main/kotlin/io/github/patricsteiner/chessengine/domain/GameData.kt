package io.github.patricsteiner.chessengine.domain

import io.github.patricsteiner.chessengine.domain.piece.Piece
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color

data class GameData(val id: String, val player1: Player, val player2: Player, val turn: Color, val winner: Color?, val draw: Boolean, val board: BoardData) {
    companion object {
        fun from(game: Game): GameData {
            return GameData(game.id, game.player1, game.player2, game.turn, game.winner, game.draw, BoardData.from(game.board))
        }
    }
}

data class BoardData(val ranks: Int, val pieces: List<PieceData>, val asMatrix: List<List<PieceData?>>) {
    companion object {
        fun from(board: Board): BoardData {
            return BoardData(
                    Board.N_RANKS,
                    board.pieces().map { PieceData.from(it) },
                    board.asMatrix().map { it.map { it?.let { PieceData.from(it) } } }
            )
        }
    }
}

data class PieceData(
        val type: Class<out Piece>,
        val char: Char,
        val color: Color,
        val unicodeSymbol: String,
        val position: Position
) {
    companion object {
        fun from(piece: Piece): PieceData {
            return PieceData(piece::class.java, piece.toChar(), piece.color, piece.toUnicodeSymbol(), piece.position)
        }
    }
}
