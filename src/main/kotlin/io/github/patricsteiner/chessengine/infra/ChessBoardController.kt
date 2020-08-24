package io.github.patricsteiner.chessengine.infra

import io.github.patricsteiner.chessengine.application.ChessBoardApplicationService
import io.github.patricsteiner.chessengine.domain.BoardData
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.Flux


@RestController
@CrossOrigin
class ChessBoardController(private val chessBoardApplicationService: ChessBoardApplicationService) {

    private val processor = DirectProcessor.create<BoardData>().serialize()
    private val sink = processor.sink()

    @GetMapping(path = ["/board"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun boardFlux(): Flux<BoardData> {
        return processor.startWith(chessBoardApplicationService.get("1"))
    }

    @PostMapping
    fun move(@RequestBody moveData: MoveData): ResponseEntity<BoardData> {
//        return try {
            val newBoardState = chessBoardApplicationService.move("1", Piece.Color.WHITE, Position(moveData.from.x, moveData.from.y), Position(moveData.to.x, moveData.to.y))
            sink.next(newBoardState)
            return ResponseEntity(newBoardState, HttpStatus.OK)
//        } catch (e: Exception) {
//            throw ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, e.toString())
//        }
    }

    @GetMapping("possibleMoves")
    fun possibleMoves(@RequestParam x: Int, @RequestParam y: Int): List<PositionData> {
        return chessBoardApplicationService.possibleMoves("1", Position(x, y))
                .map { PositionData(it.x, it.y) }
    }

}

data class MoveData(val from: PositionData, val to: PositionData)
data class PositionData(val x: Int, val y: Int)
