package io.github.patricsteiner.chessengine.infra

import io.github.patricsteiner.chessengine.application.GameService
import io.github.patricsteiner.chessengine.domain.GameData
import io.github.patricsteiner.chessengine.domain.Player
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

    private val processor = DirectProcessor.create<GameData>().serialize()
    private val sink = processor.sink()

    @GetMapping(path = ["game/{id}"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun game(@PathVariable id: String): Flux<GameData> {
        return processor.startWith(gameService.get(id))
    }

    @PostMapping("game/{id}")
    fun applyMove(@PathVariable id: String, @RequestBody moveData: MoveData): ResponseEntity<GameData> {
        val newGameData = gameService.move(id, moveData.player.color, Position(moveData.from.x, moveData.from.y), Position(moveData.to.x, moveData.to.y))
        sink.next(newGameData)
        return ResponseEntity(newGameData, HttpStatus.OK)
    }

    @ExperimentalStdlibApi
    @PostMapping("game/{id}/undo")
    fun undoMove(@PathVariable id: String): ResponseEntity<GameData> {
        val newGameData = gameService.undo(id)
        sink.next(newGameData)
        return ResponseEntity(newGameData, HttpStatus.OK)
    }

    @ExperimentalStdlibApi
    @PostMapping("game/{id}/redo")
    fun redoMove(@PathVariable id: String): ResponseEntity<GameData> {
        val newGameData = gameService.redo(id)
        sink.next(newGameData)
        return ResponseEntity(newGameData, HttpStatus.OK)
    }

    @GetMapping("game/{id}/possibleMoves")
    fun possibleMoves(@PathVariable id: String, @RequestParam color: Color, @RequestParam x: Int, @RequestParam y: Int): List<PositionData> {
        return gameService.possibleMoves(id, color, Position(x, y))
                .map { PositionData(it.x, it.y) }
    }

}

data class MoveData(val player: Player, val from: PositionData, val to: PositionData)
data class PositionData(val x: Int, val y: Int)
