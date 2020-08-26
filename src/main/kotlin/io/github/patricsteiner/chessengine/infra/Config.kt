package io.github.patricsteiner.chessengine.infra

import io.github.patricsteiner.chessengine.application.GameService
import io.github.patricsteiner.chessengine.domain.GameRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Config(@Autowired val gameRepository: GameRepository) {

    @Bean
    fun chessBoardApplicationService() = GameService(gameRepository)

}
