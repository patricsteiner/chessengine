package io.github.patricsteiner.chessengine.domain

import io.github.patricsteiner.chessengine.domain.piece.Piece

class MoveRecord(piece: Piece, val to: Position, victim: Piece? = null, val promotionTo: Piece? = null, val combinedMove: MoveRecord? = null) {

    val piece: Piece = piece.copy()
    val victim: Piece? = victim?.copy()

}
