package io.github.patricsteiner.chessengine.infra

import io.github.patricsteiner.chessengine.domain.Game
import io.github.patricsteiner.chessengine.domain.GameRepository
import io.github.patricsteiner.chessengine.domain.Player
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.BLACK
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import org.springframework.stereotype.Repository

@Repository
class DummyGameRepo : GameRepository {

    final val game: Game = Game.newGame(Player("foo", WHITE), Player("bar", BLACK))

    override fun find(id: String): Game? {
        return game
    }

}
