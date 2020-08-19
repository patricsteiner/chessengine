package io.github.patricsteiner.chessengine.domain

interface BoardRepository {

    fun find(id: String): Board?

}
