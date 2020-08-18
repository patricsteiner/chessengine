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

    constructor(x: Int, y: Int) : this('a' + x, N_RANKS - y)

    override fun toString(): String {
        return "$file$rank"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

}
