package io.github.patricsteiner.chessengine.application

import io.github.patricsteiner.chessengine.domain.*
import io.github.patricsteiner.chessengine.domain.Game.Companion.newGame

typealias GameDataConsumer = (GameData) -> Unit

class GameService(private val gameRepository: GameRepository) {

    private val onSaveListeners = mutableMapOf<GameId, MutableList<GameDataConsumer>>()

    fun addListener(id: GameId, gameDataConsumer: GameDataConsumer) {
        if (!onSaveListeners.containsKey(id)) {
            onSaveListeners[id] = mutableListOf()
        }
        onSaveListeners[id]!!.add(gameDataConsumer)
    }

    fun removeListener(id: GameId, gameDataConsumer: GameDataConsumer) {
        onSaveListeners[id]?.removeIf { it === gameDataConsumer }
    }

    fun move(gameId: String, colorToken: ColorToken, from: Position, to: Position): GameData {
        val game = getGame(gameId)
        val color = if (!game.requireColorTokens) game.turn else game.colorFromToken(colorToken) ?: throw RuntimeException("Invalid colorToken")
        game.moveOrAttack(color, from, to)
        saveGame(game)
        return GameData.from(game)
    }

    fun possibleMoves(gameId: String, from: Position): List<Position> {
        val game = getGame(gameId)
        return game.legalMoves(from)
    }

    fun get(gameId: String): GameData {
        val game = getGame(gameId)
        return GameData.from(game)
    }

    fun createNewGame(requireColorTokens: Boolean): GameAndTokenData {
        val game = newGame(requireColorTokens)
        saveGame(game)
        return GameAndTokenData(GameData.from(game), game.whiteToken, game.blackToken)
    }

    private fun getGame(gameId: GameId): Game {
        return gameRepository.find(gameId) ?: throw RuntimeException("Can't find game")
    }

    private fun saveGame(game: Game) {
        gameRepository.save(game)
        onSaveListeners[game.id]?.run {
            val gameData = GameData.from(game)
            forEach { it(gameData) }
        }
    }

}

data class GameAndTokenData(val game: GameData, val whiteToken: ColorToken, val blackToken: ColorToken)
