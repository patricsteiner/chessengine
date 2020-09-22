package io.github.patricsteiner.chessengine.application

import io.github.patricsteiner.chessengine.domain.*
import io.github.patricsteiner.chessengine.domain.Game.Companion.newGame
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color

class GameService(private val gameRepository: GameRepository) {

    fun move(gameId: String, colorToken: ColorToken, from: Position, to: Position): GameData {
        val game = getGame(gameId)
        val color = game.colorFromToken(colorToken) ?: throw RuntimeException("Invalid colorToken")
        game.move(color, from, to)
        gameRepository.save(game)
        return GameData.from(game)
    }

    fun possibleMoves(gameId: String, colorToken: ColorToken, from: Position): List<Position> {
        val game = getGame(gameId)
        val color = game.colorFromToken(colorToken) ?: throw RuntimeException("Invalid colorToken")
        return game.possibleMoves(color, from)
    }

    fun get(gameId: String): GameData {
        val game = getGame(gameId)
        return GameData.from(game)
    }

    @ExperimentalStdlibApi
    fun undo(gameId: String): GameData {
        val game = getGame(gameId)
        game.undo()
        return GameData.from(game)
    }

    @ExperimentalStdlibApi
    fun redo(gameId: String): GameData {
        val game = getGame(gameId)
        game.redo()
        return GameData.from(game)
    }

    fun createNewGame(): GameAndTokenData {
        val game = newGame()
        gameRepository.save(game)
        return GameAndTokenData(GameData.from(game), game.whiteToken, game.blackToken)
    }

    private fun getGame(gameId: String): Game {
        return gameRepository.find(gameId) ?: throw RuntimeException("Can't find game")
    }

}

data class GameAndTokenData(val game: GameData, val whiteToken: ColorToken, val blackToken: ColorToken)
