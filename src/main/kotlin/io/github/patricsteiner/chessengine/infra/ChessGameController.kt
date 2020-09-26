package io.github.patricsteiner.chessengine.infra

import io.github.patricsteiner.chessengine.application.GameAndTokenData
import io.github.patricsteiner.chessengine.application.GameDataConsumer
import io.github.patricsteiner.chessengine.application.GameService
import io.github.patricsteiner.chessengine.domain.ColorToken
import io.github.patricsteiner.chessengine.domain.GameData
import io.github.patricsteiner.chessengine.domain.GameId
import io.github.patricsteiner.chessengine.domain.Position
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink


@RestController
@CrossOrigin
class ChessGameController(private val gameService: GameService) {

    @GetMapping(path = ["game/{id}"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun game(@PathVariable id: GameId): Flux<GameData> {
        return Flux.create<GameData>({ sink ->
            val listener: GameDataConsumer = { sink.next(it) }
            gameService.addListener(id, listener)
            sink.onCancel { gameService.removeListener(id, listener) }
        }, FluxSink.OverflowStrategy.LATEST).startWith(gameService.get(id))
    }

    @PostMapping("game")
    fun createNewGame(): ResponseEntity<GameAndTokenData> {
        val gameAndTokenData = gameService.createNewGame()
        return ResponseEntity(gameAndTokenData, HttpStatus.OK)
    }

    @PostMapping("game/{id}")
    fun applyMove(@PathVariable id: GameId, @RequestParam colorToken: ColorToken, @RequestBody moveData: MoveData): ResponseEntity<GameData> {
        val newGameData = gameService.move(id, colorToken, Position(moveData.from.x, moveData.from.y), Position(moveData.to.x, moveData.to.y))
        return ResponseEntity(newGameData, HttpStatus.OK)
    }

    @GetMapping("game/{id}/possibleMoves")
    fun possibleMoves(@PathVariable id: GameId, @RequestParam x: Int, @RequestParam y: Int): List<PositionData> {
        return gameService.possibleMoves(id, Position(x, y))
                .map { PositionData(it.x, it.y) }
    }

}

data class MoveData(val from: PositionData, val to: PositionData)
data class PositionData(val x: Int, val y: Int)
