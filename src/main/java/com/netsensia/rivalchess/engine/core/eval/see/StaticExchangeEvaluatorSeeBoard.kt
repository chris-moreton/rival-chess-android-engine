package com.netsensia.rivalchess.engine.core.eval.see

import com.netsensia.rivalchess.engine.core.BITBOARD_BK
import com.netsensia.rivalchess.engine.core.BITBOARD_WK
import com.netsensia.rivalchess.engine.core.board.*
import com.netsensia.rivalchess.engine.core.eval.pieceValue
import com.netsensia.rivalchess.engine.core.type.EngineMove
import com.netsensia.rivalchess.exception.InvalidMoveException
import com.netsensia.rivalchess.model.Colour
import com.netsensia.rivalchess.model.Piece
import java.lang.Long.numberOfTrailingZeros

class StaticExchangeEvaluatorSeeBoard : StaticExchangeEvaluator {

    @ExperimentalStdlibApi
    @Throws(InvalidMoveException::class)
    override fun staticExchangeEvaluation(board: EngineBoard, move: EngineMove): Int {
        val seeBoard = SeeBoard(board)

        if (board.makeMove(move)) {
            val materialBalance = materialBalanceFromMoverPerspective(seeBoard)
            val captureSquare = move.compact and 63
            val materialGain = seeBoard.makeMove(move)
            val seeValue = -seeSearch(seeBoard, captureSquare, -(materialBalance + materialGain)) - materialBalance
            board.unMakeMove()
            return seeValue
        }
        return -Int.MAX_VALUE
    }

    @ExperimentalStdlibApi
    @Throws(InvalidMoveException::class)
    fun seeSearch(seeBoard: SeeBoard, captureSquare: Int, materialBalance: Int): Int {

        var bestScore = materialBalance

        for (move in seeBoard.generateCaptureMovesOnSquare(captureSquare)) {
            val materialGain = seeBoard.makeMove(move)

            val kingBitboard = if (seeBoard.mover == Colour.WHITE) BITBOARD_WK else BITBOARD_BK
            if (seeBoard.bitboards.getPieceBitboard(kingBitboard) == 0L) {
                seeBoard.unMakeMove()
                return bestScore + pieceValue(Piece.KING)
            }

            val seeScore = -seeSearch(seeBoard, captureSquare, -(materialBalance + materialGain))
            seeBoard.unMakeMove()
            bestScore = seeScore.coerceAtLeast(bestScore)
        }

        return bestScore
    }

    private fun materialBalanceFromMoverPerspective(seeBoard: SeeBoard) =
        if (seeBoard.mover == Colour.WHITE)
            (seeBoard.whitePieceValues - seeBoard.blackPieceValues) else
            (seeBoard.blackPieceValues - seeBoard.whitePieceValues)

}