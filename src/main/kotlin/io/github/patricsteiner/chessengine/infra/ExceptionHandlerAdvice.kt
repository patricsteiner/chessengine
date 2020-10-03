package io.github.patricsteiner.chessengine.infra

import io.github.patricsteiner.chessengine.domain.GameException
import io.github.patricsteiner.chessengine.domain.GameNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandlerAdvice {

    @ExceptionHandler(GameException::class)
    fun handleException(exception: GameException): ResponseEntity<Map<String, String?>> {
        return ResponseEntity(mapOf("message" to exception.message), HttpStatus.NOT_ACCEPTABLE)
    }

    @ExceptionHandler(GameNotFoundException::class)
    fun handleException(exception: GameNotFoundException): ResponseEntity<Map<String, String?>> {
        return ResponseEntity(mapOf("message" to exception.message), HttpStatus.NOT_FOUND)
    }

}
