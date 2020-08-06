package io.github.patricsteiner.chessengine.domain

import io.github.patricsteiner.chessengine.domain.piece.Piece

data class MoveRecord(val piece: Piece, val from: Position, val to: Position, val caputred: Piece?)
