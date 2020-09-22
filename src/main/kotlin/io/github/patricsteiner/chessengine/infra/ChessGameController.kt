package io.github.patricsteiner.chessengine.infra

import io.github.patricsteiner.chessengine.application.GameAndTokenData
import io.github.patricsteiner.chessengine.application.GameService
import io.github.patricsteiner.chessengine.domain.ColorToken
import io.github.patricsteiner.chessengine.domain.GameData
import io.github.patricsteiner.chessengine.domain.GameId
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.Flux


@RestController
@CrossOrigin
class ChessGameController(private val gameService: GameService) {

    private val processor = DirectProcessor.create<GameData>().serialize() // TODO need 1 sink per game or sth?
    private val sink = processor.sink()

    @GetMapping(path = ["game/{id}"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun game(@PathVariable id: GameId): Flux<GameData> {
        return processor.startWith(gameService.get(id))
    }

    @PostMapping("game")
    fun createNewGame(): ResponseEntity<GameAndTokenData> {
        val gameAndTokenData = gameService.createNewGame()
        return ResponseEntity(gameAndTokenData, HttpStatus.OK)
    }

    @PostMapping("game/{id}")
    fun applyMove(@PathVariable id: GameId, @RequestParam colorToken: ColorToken, @RequestBody moveData: MoveData): ResponseEntity<GameData> {
        val newGameData = gameService.move(id, colorToken, Position(moveData.from.x, moveData.from.y), Position(moveData.to.x, moveData.to.y))
        sink.next(newGameData)
        return ResponseEntity(newGameData, HttpStatus.OK)
    }

    @ExperimentalStdlibApi
    @PostMapping("game/{id}/undo")
    fun undoMove(@PathVariable id: GameId): ResponseEntity<GameData> {
        val newGameData = gameService.undo(id)
        sink.next(newGameData)
        return ResponseEntity(newGameData, HttpStatus.OK)
    }

    @ExperimentalStdlibApi
    @PostMapping("game/{id}/redo")
    fun redoMove(@PathVariable id: GameId): ResponseEntity<GameData> {
        val newGameData = gameService.redo(id)
        sink.next(newGameData)
        return ResponseEntity(newGameData, HttpStatus.OK)
    }

    @GetMapping("game/{id}/possibleMoves")
    fun possibleMoves(@PathVariable id: GameId, @RequestParam colorToken: ColorToken, @RequestParam x: Int, @RequestParam y: Int): List<PositionData> {
        return gameService.possibleMoves(id, colorToken, Position(x, y))
                .map { PositionData(it.x, it.y) }
    }

}

data class MoveData(val from: PositionData, val to: PositionData)
data class PositionData(val x: Int, val y: Int)
