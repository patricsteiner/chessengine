//package io.github.patricsteiner.chessengine.domain.piece
//
//import io.github.patricsteiner.chessengine.domain.Board
//import io.github.patricsteiner.chessengine.domain.Position
//import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
//
//class Lumberjack(color: Color, position: Position) : Piece(color, position) {
//
//    override fun toChar(): Char {
//        return if (color == WHITE) 'L' else 'l'
//    }
//
//    override fun toUnicodeSymbol(): String {
//        return if (color == WHITE) "L" else "l"
//    }
//
//    override fun move(to: Position, board: Board, deltaX: Int, deltaY: Int, dryRun: Boolean): MoveResult {
//        TODO("Not yet implemented")
//    }
//
//    override fun attack(to: Position, board: Board, deltaX: Int, deltaY: Int, dryRun: Boolean): MoveResult {
//        TODO("Not yet implemented")
//    }
//
//}
