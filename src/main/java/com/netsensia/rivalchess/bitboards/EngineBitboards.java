package com.netsensia.rivalchess.bitboards;

import com.netsensia.rivalchess.config.Limit;
import com.netsensia.rivalchess.model.Colour;
import com.netsensia.rivalchess.model.SquareOccupant;

import java.util.Arrays;

import static com.netsensia.rivalchess.bitboards.BitboardUtilsKt.getFirstOccupiedSquare;
import static com.netsensia.rivalchess.bitboards.BitboardUtilsKt.isBishopAttackingSquare;
import static com.netsensia.rivalchess.bitboards.BitboardUtilsKt.isRookAttackingSquare;
import static com.netsensia.rivalchess.bitboards.BitboardUtilsKt.getPawnMovesCaptureOfColour;

public class EngineBitboards {

    /**
     * @deprecated Use getters and setters
     */
    @Deprecated
    public long[] pieceBitboards;

    public EngineBitboards() {
        reset();
    }

    @Deprecated
    public void setPieceBitboard(int i, long bitboard) {
        pieceBitboards[i] = bitboard;
    }

    @Deprecated
    public long getPieceBitboard(int i) {
        return pieceBitboards[i];
    }

    public long getAllPieceBitboard() {
        return getPieceBitboard(BitboardType.ALL);
    }

    public void xorPieceBitboard(int i, long xorBy) {
        this.pieceBitboards[i] ^= xorBy;
    }

    public void xorPieceBitboard(BitboardType type, long xorBy) {
        this.pieceBitboards[type.getIndex()] ^= xorBy;
    }

    public void orPieceBitboard(BitboardType type, long xorBy) {
        this.pieceBitboards[type.getIndex()] |= xorBy;
    }

    public void reset() {
        pieceBitboards = new long[BitboardType.getNumBitboardTypes()];
        Arrays.fill(pieceBitboards, 0);
    }

    public void setPieceBitboard(BitboardType type, long bitboard) {
        pieceBitboards[type.getIndex()] = bitboard;
    }

    public long getPieceBitboard(BitboardType type) {
        return pieceBitboards[type.getIndex()];
    }

    public void movePiece(SquareOccupant piece, int compactMove) {

        final byte moveFrom = (byte) (compactMove >>> 16);
        final byte moveTo = (byte) (compactMove & 63);

        final long fromMask = 1L << moveFrom;
        final long toMask = 1L << moveTo;

        pieceBitboards[piece.getIndex()] ^= fromMask | toMask;
    }

    public long getRookMovePiecesBitboard(Colour colour) {
        return colour == Colour.WHITE
                ? getPieceBitboard(BitboardType.WR) | getPieceBitboard(BitboardType.WQ)
                : getPieceBitboard(BitboardType.BR) | getPieceBitboard(BitboardType.BQ);
    }

    public long getBishopMovePiecesBitboard(Colour colour) {
        return colour == Colour.WHITE
                ? getPieceBitboard(BitboardType.WB) | getPieceBitboard(BitboardType.WQ)
                : getPieceBitboard(BitboardType.BB) | getPieceBitboard(BitboardType.BQ);
    }

    public boolean isSquareAttackedBy(final int attackedSquare, final Colour attacker) {

        if ((pieceBitboards[SquareOccupant.WN.ofColour(attacker).getIndex()] & Bitboards.knightMoves.get(attackedSquare)) != 0 ||
                (pieceBitboards[SquareOccupant.WK.ofColour(attacker).getIndex()] & Bitboards.kingMoves.get(attackedSquare)) != 0 ||
                (pieceBitboards[SquareOccupant.WP.ofColour(attacker).getIndex()]
                        & getPawnMovesCaptureOfColour(attacker.opponent()).get(attackedSquare)) != 0)
            return true;

        long bitboardBishop = getBishopMovePiecesBitboard(attacker);

        while (bitboardBishop != 0) {
            final int pieceSquare = getFirstOccupiedSquare(bitboardBishop);
            bitboardBishop ^= (1L << (pieceSquare));
            if (isBishopAttackingSquare(attackedSquare, pieceSquare, getAllPieceBitboard())) {
                return true;
            }
        }

        long bitboardRook = getRookMovePiecesBitboard(attacker);

        while (bitboardRook != 0) {
            final int pieceSquare = Long.numberOfTrailingZeros(bitboardRook);
            bitboardRook ^= (1L << (pieceSquare));
            if (isRookAttackingSquare(attackedSquare, pieceSquare, getAllPieceBitboard())) {
                return true;
            }
        }

        return false;
    }
}
