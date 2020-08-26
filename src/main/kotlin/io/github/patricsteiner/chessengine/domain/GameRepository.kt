package io.github.patricsteiner.chessengine.domain

interface GameRepository {

    fun find(id: String): Game?

}
