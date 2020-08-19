package io.github.patricsteiner.chessengine.infra

import io.github.patricsteiner.chessengine.domain.Board
import io.github.patricsteiner.chessengine.domain.BoardRepository
import org.springframework.stereotype.Repository

@Repository
class DummyBoardRepo : BoardRepository {

    final val board: Board = Board()

    init {
        board.setup()
    }

    override fun find(id: String): Board? {
        return board
    }

}
