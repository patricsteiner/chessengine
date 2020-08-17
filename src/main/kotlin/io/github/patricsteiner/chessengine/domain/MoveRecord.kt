package io.github.patricsteiner.chessengine.domain

import io.github.patricsteiner.chessengine.domain.piece.Piece
import io.github.patricsteiner.chessengine.domain.piece.Piece2

data class MoveRecord(val piece: Piece, val from: Position, val to: Position, val caputred: Piece?)

class MoveRecord2 {

    val piece: Piece2
    val to: Position
    val captured: Piece2?

    constructor(piece: Piece2, to: Position, captured: Piece2?) {
        this.piece = piece.copy()
        this.to = to
        this.captured = captured?.copy()
    }

}
