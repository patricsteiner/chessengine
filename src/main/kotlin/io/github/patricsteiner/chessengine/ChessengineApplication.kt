package io.github.patricsteiner.chessengine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChessengineApplication

fun main(args: Array<String>) {
    runApplication<ChessengineApplication>(*args)
}
