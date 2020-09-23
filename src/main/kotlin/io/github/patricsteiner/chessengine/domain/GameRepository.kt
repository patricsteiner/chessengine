package io.github.patricsteiner.chessengine.domain

interface GameRepository {

    fun find(id: GameId): Game?

    fun save(game: Game)

}
