package io.github.patricsteiner.chessengine.domain.piece

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import java.lang.Error

class Tree(color: Color, position: Position) : Piece(color, position) {

    override fun toChar(): Char {
        return if (color == WHITE) 'T' else 't'
    }

    override fun toUnicodeSymbol(): String {
        return if (color == WHITE) "T" else "t"
    }

    override fun canMove(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return false
    }

    override fun canAttack(to: Position, board: Board, deltaX: Int, deltaY: Int): Boolean {
        return false
    }

    override fun move(to: Position, board: Board, deltaX: Int, deltaY: Int): MoveResult {
        return MoveResult.Error("A Tree cannot move")
    }

    override fun attack(to: Position, board: Board, deltaX: Int, deltaY: Int): MoveResult {
        return MoveResult.Error("A Tree cannot attack")
    }

}
