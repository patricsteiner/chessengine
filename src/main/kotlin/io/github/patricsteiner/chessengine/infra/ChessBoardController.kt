package io.github.patricsteiner.chessengine.infra

import io.github.patricsteiner.chessengine.application.ChessBoardApplicationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ChessBoardController(private val chessBoardApplicationService: ChessBoardApplicationService) {

    @GetMapping
    fun board(): BoardData {
        return chessBoardApplicationService.get("1")
    }

}
