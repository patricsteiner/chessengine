package io.github.patricsteiner.chessengine.infra

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandlerAdvice {

    @ExceptionHandler(RuntimeException::class)
    fun handleException(exception: RuntimeException): ResponseEntity<Map<String, String?>> {
        return ResponseEntity(mapOf("message" to exception.message), HttpStatus.NOT_ACCEPTABLE)
    }

}
