package io.github.patricsteiner.chessengine.domain

import io.github.patricsteiner.chessengine.domain.piece.Piece

class MoveRecord(
        val piece: PieceData,
        val to: Position,
        val victim: PieceData? = null,
        val promotionTo: PieceData? = null,
        val combinedMove: MoveRecord? = null
) {
    constructor(piece: Piece, to: Position, victim: Piece? = null, promotionTo: Piece? = null, combinedMove: MoveRecord? = null)
            : this(PieceData.from(piece), to, victim?.let { PieceData.from(it) }, promotionTo?.let { PieceData.from(it) }, combinedMove)
}
