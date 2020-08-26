package io.github.patricsteiner.chessengine.application

import io.github.patricsteiner.chessengine.domain.*

class GameService(private val gameRepository: GameRepository) {

    fun move(gameId: String, player: Player, from: Position, to: Position): GameData {
        val game = gameRepository.find(gameId) ?: throw RuntimeException("Can't find game")
        game.move(player, from, to)
        return GameData.from(game)
    }

    fun possibleMoves(gameId: String, from: Position): List<Position> {
        val game = gameRepository.find(gameId) ?: throw RuntimeException("Can't find game")
        return game.board.possibleMoves(from)
    }

    fun get(gameId: String): GameData {
        val game = gameRepository.find(gameId) ?: throw RuntimeException("Can't find game")
        return GameData.from(game)
    }

}
