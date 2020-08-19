package io.github.patricsteiner.chessengine.infra

import io.github.patricsteiner.chessengine.application.ChessBoardApplicationService
import io.github.patricsteiner.chessengine.domain.BoardRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Config(@Autowired val boardRepository: BoardRepository) {

    @Bean
    fun chessBoardApplicationService() = ChessBoardApplicationService(boardRepository)

}
