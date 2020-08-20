package io.github.patricsteiner.chessengine.infra

import io.github.patricsteiner.chessengine.application.ChessBoardApplicationService
import io.github.patricsteiner.chessengine.domain.BoardData
import io.github.patricsteiner.chessengine.domain.Position
import io.github.patricsteiner.chessengine.domain.piece.Piece
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ChessBoardController(private val chessBoardApplicationService: ChessBoardApplicationService) {

    @GetMapping
    fun board(): BoardData {
        return chessBoardApplicationService.get("1")
    }

    @PostMapping
    fun move(from: Position, to: Position): BoardData {
        return chessBoardApplicationService.move("1", null as Piece.Color, from, to)
    }

}
