package io.github.patricsteiner.chessengine.domain

class GameException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)
class GameNotFoundException(gameId: GameId) : RuntimeException("Cannot find game with id $gameId")
