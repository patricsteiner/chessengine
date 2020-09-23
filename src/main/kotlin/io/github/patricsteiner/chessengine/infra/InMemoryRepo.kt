package io.github.patricsteiner.chessengine.infra

import io.github.patricsteiner.chessengine.domain.Game
import io.github.patricsteiner.chessengine.domain.GameId
import io.github.patricsteiner.chessengine.domain.GameRepository
import org.springframework.stereotype.Repository

@Repository
class InMemoryGameRepo : GameRepository {

    private val games = mutableMapOf<GameId, Game>()

    override fun save(game: Game) {
        games[game.id] = game
    }

    override fun find(id: GameId): Game? {
        return games[id]
    }

}
