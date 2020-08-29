package io.github.patricsteiner.chessengine.application

import io.github.patricsteiner.chessengine.domain.Game
import io.github.patricsteiner.chessengine.domain.GameData
import io.github.patricsteiner.chessengine.domain.GameRepository
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color

class GameService(private val gameRepository: GameRepository) {

    fun move(gameId: String, color: Color, from: Position, to: Position): GameData {
        val game = getGame(gameId)
        game.move(color, from, to)
        return GameData.from(game)
    }

    fun possibleMoves(gameId: String, color: Color, from: Position): List<Position> {
        val game = getGame(gameId)
        return game.possibleMoves(color, from)
    }

    fun get(gameId: String): GameData {
        val game = getGame(gameId)
        return GameData.from(game)
    }

    private fun getGame(gameId: String): Game {
        return gameRepository.find(gameId) ?: throw RuntimeException("Can't find game")
    }

}
