package com.netsensia.rivalchess.engine.core.type

import com.netsensia.rivalchess.enums.PROMOTION_PIECE_TOSQUARE_MASK_FULL
import com.netsensia.rivalchess.enums.promotionMask
import com.netsensia.rivalchess.model.Move
import com.netsensia.rivalchess.model.SquareOccupant
import com.netsensia.rivalchess.util.getBitRefFromBoardRef

class EngineMove {
    @JvmField
    val compact: Int

    constructor(compact: Int) {
        this.compact = compact
    }

    fun from() = compact shr 16 and 63
    fun to() = compact and 63
    fun promotionPieceMask() = compact and PROMOTION_PIECE_TOSQUARE_MASK_FULL

    constructor(move: Move) {
        val from = getBitRefFromBoardRef(move.srcBoardRef)
        val to = getBitRefFromBoardRef(move.tgtBoardRef)
        val promotionPiece = move.promotedPiece
        val promotionPart = if (promotionPiece == SquareOccupant.NONE) 0 else promotionMask(promotionPiece.piece)
        compact = to + (from shl 16) + (promotionPart shl 32)
    }

    override fun equals(other: Any?): Boolean {
        other as EngineMove
        return this.compact == other.compact
    }

    override fun toString(): String {
        return "" + from() + "-" + to()
    }
}