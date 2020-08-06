package io.github.patricsteiner.chessengine.domain

import io.github.patricsteiner.chessengine.domain.Board.Companion.N_RANKS

class Position(val file: Char, val rank: Int) {

    val x: Int
    val y: Int

    init {
        if (file < 'a' || file > 'a' + N_RANKS || rank < 1 || rank > N_RANKS) {
            throw IllegalArgumentException("Illegal position: $file$rank")
        }
        x = file - 'a'
        y = N_RANKS - rank
    }

    constructor(x: Int, y: Int): this('a' + x, N_RANKS - y)

    override fun toString(): String {
        return "$file$rank"
    }


}
