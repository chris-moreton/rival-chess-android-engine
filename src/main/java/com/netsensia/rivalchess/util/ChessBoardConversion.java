package com.netsensia.rivalchess.util;

import com.netsensia.rivalchess.engine.core.EngineChessBoard;
import com.netsensia.rivalchess.engine.core.RivalConstants;
import com.netsensia.rivalchess.exception.IllegalFenException;
import com.netsensia.rivalchess.exception.IllegalMoveException;
import com.netsensia.rivalchess.model.Square;
import com.netsensia.rivalchess.model.Move;

public class ChessBoardConversion 
{

	public static Square getBoardRefFromBitRef(int bitRef)
	{
		bitRef = 63 - bitRef;
		int x = bitRef % 8;
		int y = bitRef / 8;
		return new Square(x,y);
	}
	
	public static int getBitRefFromBoardRef(Square boardRef)
	{
		return 63 - (8*boardRef.getYRank()) - boardRef.getXFile();
	}

	public static Move getMoveRefFromEngineMove(int move)
	{
		int from = (move >> 16) & 63;
		int to = move & 63;
		Square boardRefFrom = ChessBoardConversion.getBoardRefFromBitRef(from);
		Square boardRefTo = ChessBoardConversion.getBoardRefFromBitRef(to);

		Move moveRef = new Move(boardRefFrom, boardRefTo);

		int promotionPieceCode = move & RivalConstants.PROMOTION_PIECE_TOSQUARE_MASK_FULL;
		switch (promotionPieceCode)
		{
			case RivalConstants.PROMOTION_PIECE_TOSQUARE_MASK_QUEEN :
				moveRef.setPromotedPieceCode(to >= 56 ? "Q" : "q");
				break;
			case RivalConstants.PROMOTION_PIECE_TOSQUARE_MASK_ROOK : 
				moveRef.setPromotedPieceCode(to >= 56 ? "R" : "r");
				break;
			case RivalConstants.PROMOTION_PIECE_TOSQUARE_MASK_KNIGHT : 
				moveRef.setPromotedPieceCode(to >= 56 ? "N" : "n");
				break;
			case RivalConstants.PROMOTION_PIECE_TOSQUARE_MASK_BISHOP : 
				moveRef.setPromotedPieceCode(to >= 56 ? "B" : "b");
				break;
			default:
				moveRef.setPromotedPieceCode("");
				break;
		}
		
		return moveRef;
	}

	public static String getSimpleAlgebraicMoveFromCompactMove(int compactMove)
	{
		if (compactMove == 0)
		{
			return "zero";
		}
		int from = (compactMove >> 16) & 63;
		int to = compactMove & 63;
		
		Move move = getMoveRefFromEngineMove(compactMove);
		String pp = move.getPromotedPieceCode();
		
		return getSimpleAlgebraicFromBitRef(from) + getSimpleAlgebraicFromBitRef(to) + pp;
	}

	public static String getSimpleAlgebraicFromBitRef(int bitRef)
	{
		Square boardRef = ChessBoardConversion.getBoardRefFromBitRef(bitRef);
		
		char a = (char)(boardRef.getXFile() + 97);
		
		return "" + a + ((7-boardRef.getYRank()) + 1);
	}

	public static String getPgnMoveFromCompactMove(int move, String fen)
			throws IllegalFenException, IllegalMoveException {

		EngineChessBoard board = new EngineChessBoard();
		board.setBoard(FenUtils.getBoardModel(fen));

		String pgnMove = "";
		int to = move & 63;
		int from = (move >>> 16) & 63;
		int promotionPiece = move & RivalConstants.PROMOTION_PIECE_TOSQUARE_MASK_FULL;
		
		switch (board.getSquareOccupant(from).getIndex() % 6)
		{
			case RivalConstants.WN : pgnMove = "N"; break;  
			case RivalConstants.WK : pgnMove = "K"; break;  
			case RivalConstants.WQ : pgnMove = "Q"; break;  
			case RivalConstants.WB : pgnMove = "B"; break;  
			case RivalConstants.WR : pgnMove = "R"; break;
			case RivalConstants.WP : break;
			default:
				throw new IllegalMoveException("No piece found on source square");
		}
		
		char qualifier = ' ';
		
		int[] legalMoves = new int[RivalConstants.MAX_LEGAL_MOVES];
		board.setLegalMoves(legalMoves);
		
		int moveCount = 0;
		int legalMove = legalMoves[moveCount] & 0x00FFFFFF;
		while (legalMove != 0)
		{
			final int legalMoveTo = legalMove & 63;
			final int legalMoveFrom = (legalMove >>> 16) & 63;
			if (legalMoveTo == to)
			{
				if (board.getSquareOccupant(legalMoveFrom).getIndex() == board.getSquareOccupant(from).getIndex())
				{
					if (legalMoveFrom != from)
					{
						if (legalMoveFrom % 8 == from % 8) // same file
						{
							qualifier = (char)((int)'1' + (from / 8)); 
						}
						else
						{
							qualifier = (char)((int)'a' + (7-(from % 8))); 
						}
					}
				}
			}
			moveCount ++;
			legalMove = legalMoves[moveCount] & 0x00FFFFFF;
		}
		
		if (qualifier != ' ' ) pgnMove += qualifier;
		
		if (board.getSquareOccupant(to).getIndex() != -1)
		{
			if (board.getSquareOccupant(from).getIndex() % 6 == RivalConstants.WP)
			{
				pgnMove += (char)((int)'a' + (7-(from % 8)));
			}
			pgnMove += "x";
		}
		
		pgnMove += ChessBoardConversion.getSimpleAlgebraicFromBitRef(to);
		
		switch (promotionPiece)
		{
			case RivalConstants.PROMOTION_PIECE_TOSQUARE_MASK_QUEEN : pgnMove += "=Q"; break; 
			case RivalConstants.PROMOTION_PIECE_TOSQUARE_MASK_KNIGHT : pgnMove += "=N"; break; 
			case RivalConstants.PROMOTION_PIECE_TOSQUARE_MASK_BISHOP : pgnMove += "=B"; break; 
			case RivalConstants.PROMOTION_PIECE_TOSQUARE_MASK_ROOK : pgnMove += "=R"; break; 
		}
		
		if (board.makeMove(move))
		{
			if (board.isCheck()) pgnMove += "+";
			board.unMakeMove();
		}
		
		return pgnMove;
	}
	
	public static int getCompactMoveFromSimpleAlgebraic(String s)
	{
		int fromX = s.toUpperCase().charAt(0) - 65;
		int fromY = 7 - (s.toUpperCase().charAt(1) - 49);
		int toX = s.toUpperCase().charAt(2) - 65;
		int toY = 7 - (s.toUpperCase().charAt(3) - 49);
		
		int fromBitRef = getBitRefFromBoardRef(new Square(fromX, fromY));
		int toBitRef = getBitRefFromBoardRef(new Square(toX, toY));
		
		int l = s.length();
		char promotionPiece;
		if (l == 5)
		{
			promotionPiece = s.toUpperCase().charAt(4);
			switch (promotionPiece)
			{
				case 'Q' : toBitRef |= RivalConstants.PROMOTION_PIECE_TOSQUARE_MASK_QUEEN; break;
				case 'R' : toBitRef |= RivalConstants.PROMOTION_PIECE_TOSQUARE_MASK_ROOK; break;
				case 'N' : toBitRef |= RivalConstants.PROMOTION_PIECE_TOSQUARE_MASK_KNIGHT; break;
				case 'B' : toBitRef |= RivalConstants.PROMOTION_PIECE_TOSQUARE_MASK_BISHOP; break;
			}
		}
		
		return (fromBitRef << 16) | toBitRef;
	}
}
