package io.github.patricsteiner.chessengine.domain

import io.github.patricsteiner.chessengine.domain.piece.King
import io.github.patricsteiner.chessengine.domain.piece.Pawn
import io.github.patricsteiner.chessengine.domain.piece.Piece
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.BLACK
import io.github.patricsteiner.chessengine.domain.piece.Piece.Color.WHITE
import io.github.patricsteiner.chessengine.domain.piece.Queen
import java.util.UUID.randomUUID

class Game(val id: String, val player1: Player, val player2: Player) {

    companion object {
        fun newGame(player1: Player, player2: Player): Game {
            val id = randomUUID().toString()
            return Game(id, player1, player2)
        }
    }

    val board = Board()
    private val moveHistory = mutableListOf<MoveRecord>()
    private val undoHistory = mutableListOf<MoveRecord>()
    var turn = WHITE; private set
    var winner: Color? = null; private set
    var check: Color? = null; private set
    var draw: Boolean = false; private set

    init {
        board.setupDefaultChessPieces()
        board.addAdditionalPieces()
    }

    private fun isOver(): Boolean {
        return winner != null || draw
    }

    private fun isCheck(color: Color): Boolean {
        val kingPos = board.findKing(color) ?: throw IllegalStateException("There is no king")
        return board.pieces()
                .filter { it.color == color.opposite() }
                .filter { it.hasAbilityToCapture(kingPos) }
                .any { it.canJumpOverPieces() || !board.hasPieceOnLineBetween(it.position, kingPos) }
    }

    private fun hasPossibleMoves(color: Color): Boolean {
        return board.pieces().filter { it.color == color }.flatMap { possibleMoves(color, it.position) }.isNotEmpty()
    }

    @ExperimentalStdlibApi
    fun undo() {
        if (moveHistory.isEmpty()) return
        val lastMove = moveHistory.removeLast()
        board.undo(lastMove)
        undoHistory.add(lastMove)
    }

    @ExperimentalStdlibApi
    fun redo() {
        if (undoHistory.isEmpty()) return
        val lastUndoneMove = undoHistory.removeLast()
        board.apply(lastUndoneMove)
        moveHistory.add(lastUndoneMove)
    }

    fun possibleMoves(color: Color, from: Position): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        board.eachPosition {
            if (makeMoveRecordIfLegal(color, from, it) != null) {
                possibleMoves.add(it)
            }
        }
        return possibleMoves
    }

    private fun makeMoveRecordIfLegal(color: Color, from: Position, to: Position): MoveRecord? {
        val piece = board[from] ?: throw IllegalArgumentException("No piece on $from")
        val victim = board[to]
        val moveRecord = MoveRecord(piece, to, victim)

        if (violatesGameRules(color, moveRecord)) return null

        var combinedMove: MoveRecord? = null
        if (!pieceHasAbilityToExecute(moveRecord)) {
            combinedMove = createCastleMoveIfPossible(piece, to)
            if (combinedMove == null) return null
        }
        if (otherPieceBlocks(moveRecord)) return null
        if (resultsInCheck(moveRecord)) return null

        val promotionTo = getPromotionIfPossible(piece, to)

        return MoveRecord(moveRecord.piece, moveRecord.to, moveRecord.victim, promotionTo?.let { PieceData.from(it) }, combinedMove)
    }

    private fun getPromotionIfPossible(piece: Piece, to: Position): Piece? {
        if (piece is Pawn && (piece.color == WHITE && to.rank == 8 || piece.color == BLACK && to.rank == 1)) {
            return Queen(piece.color, to)
        }
        return null
    }

    // TODO add restriction that you cannot castle when a field the king moves over is check
    private fun createCastleMoveIfPossible(piece: Piece, to: Position): MoveRecord? {
        if (piece is King && piece.moveCount == 0) {
            if (to.rank == 1) {
                if (to.file == 'c' &&
                        board[Position('a', 1)]?.moveCount == 0 &&
                        board[Position('b', 1)] == null &&
                        board[Position('c', 1)] == null &&
                        board[Position('d', 1)] == null) {
                    return MoveRecord(board[Position('a', 1)]!!, Position('d', 1))
                } else if (to.file == 'g' &&
                        board[Position('h', 1)]?.moveCount == 0 &&
                        board[Position('f', 1)] == null &&
                        board[Position('g', 1)] == null) {
                    return MoveRecord(board[Position('h', 1)]!!, Position('f', 1))
                }
            } else if (to.rank == 8) {
                if (to.file == 'c' &&
                        board[Position('a', 8)]?.moveCount == 0 &&
                        board[Position('b', 8)] == null &&
                        board[Position('c', 8)] == null &&
                        board[Position('d', 8)] == null) {
                    return MoveRecord(board[Position('a', 8)]!!, Position('d', 8))
                } else if (to.file == 'g' &&
                        board[Position('h', 8)]?.moveCount == 0 &&
                        board[Position('f', 8)] == null &&
                        board[Position('g', 8)] == null) {
                    return MoveRecord(board[Position('h', 8)]!!, Position('f', 8))
                }
            }
        }
        return null
    }

    fun move(color: Color, from: Position, to: Position) {
        if (isOver()) {
            throw IllegalArgumentException("Game is over")
        }
        if (color != turn) {
            throw IllegalArgumentException("It's not $color's turn")
        }
        val moveRecord = makeMoveRecordIfLegal(color, from, to)
                ?: throw IllegalArgumentException("This is not a legal move")
        board.apply(moveRecord)
        moveHistory.add(moveRecord)
        val enemyColor = turn.opposite()
        check = if (isCheck(enemyColor)) enemyColor else null
        if (!hasPossibleMoves(enemyColor)) {
            if (check == enemyColor) {
                winner = turn // checkmate
            } else {
                draw = true // stalemate
            }
        }
        turn = enemyColor
    }

    private fun resultsInCheck(moveRecord: MoveRecord): Boolean {
        board.apply(moveRecord)
        val resultsInCheck = isCheck(moveRecord.piece.color)
        board.undo(moveRecord)
        return resultsInCheck
    }

    private fun violatesGameRules(color: Color, moveRecord: MoveRecord): Boolean {
        if (moveRecord.piece.color != color) {
            return true // "Cannot move enemy pieces"
        }
        if (moveRecord.piece.color == board[moveRecord.to]?.color) {
            return true // "Cannot friendly-fire"
        }
        return false
    }

    private fun otherPieceBlocks(moveRecord: MoveRecord): Boolean {
        if (moveRecord.piece.toPiece().canJumpOverPieces()) return false
        val targetPosition =  moveRecord.victim?.position ?: moveRecord.to
        return board.hasPieceOnLineBetween(moveRecord.piece.position, targetPosition)
    }

    private fun pieceHasAbilityToExecute(moveRecord: MoveRecord): Boolean {
        val piece = moveRecord.piece.toPiece()
        val isCapturingMove = moveRecord.victim != null
        if (isCapturingMove && piece.hasAbilityToCapture(moveRecord.victim!!.position)) {
            return true
        } else if (!isCapturingMove && piece.hasAbilityToMove(moveRecord.to)) {
            return true
        }
        return false
    }

}
