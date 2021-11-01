package chess.game;

import chess.board.Square;
import chess.pieces.*;

public class PositionEvaluator {

    private static final int[][] pawnTable =
                    {{0, 0, 0, 0, 0, 0, 0, 0},
                    {50, 50, 50, 50, 50, 50, 50, 50},
                    {10, 10, 20, 30, 30, 20, 10, 10},
                    {5, 5, 10, 25, 25, 10, 5, 5},
                    {0, 0, 0, 20, 20, 0, 0, 0},
                    {5, -5, -10, 0, 0, -10, -5, 5},
                    {5, 10, 10, -20, -20, 10, 10, 5},
                    {0, 0, 0, 0, 0, 0, 0, 0}};

    private static final int[][] knightTable = {{-50, -40, -30, -30, -30, -30, -40, -50},
            {40, -20, 0, 0, 0, 0, -20, -40},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-30, 5, 15, 20, 20, 15, 5, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 5, 10, 15, 15, 10, 5, -30},
            {-40, -20, 0, 5, 5, 0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50}};

    private static final int[][] bishopTable = {{-20, -10, -10, -10, -10, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 10, 10, 5, 0, -10},
            {-10, 5, 5, 10, 10, 5, 5, -10},
            {-10, 0, 10, 10, 10, 10, 0, -10},
            {-10, 10, 10, 10, 10, 10, 10, -10},
            {-10, 5, 0, 0, 0, 0, 5, -10},
            {-20, -10, -10, -10, -10, -10, -10, -20}};

    private static final int[][] rookTable = {{0, 0, 0, 0, 0, 0, 0, 0},
            {5, 10, 10, 10, 10, 10, 10, 5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {0, 0, 0, 5, 5, 0, 0, 0}};

    private static final int[][] queenTable = {{-20, -10, -10, -5, -5, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 5, 5, 5, 0, -10},
            {-5, 0, 5, 5, 5, 5, 0, -5},
            {0, 0, 5, 5, 5, 5, 0, -5},
            {-10, 5, 5, 5, 5, 5, 0, -10},
            {-10, 0, 5, 0, 0, 0, 0, -10},
            {-20, -10, -10, -5, -5, -10, -10, -20}};

    private static final int[][] kingTable = {{-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-20, -30, -30, -40, -40, -30, -30, -20},
            {-10, -20, -20, -20, -20, -20, -20, -10},
            {20, 20, 0, 0, 0, 0, 20, 20},
            {20, 30, 10, 0, 0, 10, 30, 20}};

    static int evaluatePosition(State state) {
        int rating = 0;

        rating += materialCount(state);
        rating += evaluatePiecePositionByTable(state);

        return rating;
    }

    private static int evaluatePiecePositionByTable(State state) {
        int eval = 0;

        boolean color = state.isWhitesTurn();

        for (Piece piece : state.getAllPiecesOfOneColor(color)) {
            Square square = state.getSquareOfPiece(piece);

            if (piece instanceof Pawn) {
                eval += pawnTable[square.getRow() - 1][square.getColumn() - 1];
            } else if (piece instanceof Bishop) {
                eval += bishopTable[square.getRow() - 1][square.getColumn() - 1];
            } else if (piece instanceof Knight) {
                eval += knightTable[square.getRow() - 1][square.getColumn() - 1];
            } else if (piece instanceof Queen) {
                eval += queenTable[square.getRow() - 1][square.getColumn() - 1];
            } else if (piece instanceof King) {
                eval += kingTable[square.getRow() - 1][square.getColumn() - 1];
            } else if (piece instanceof Rook) {
                eval += rookTable[square.getRow() - 1][square.getColumn() - 1];
            }
        }

        if (!state.isWhitesTurn()) {
            eval = -eval;
        }
        return eval;
    }

    private static int materialCount(State state) {
        int material = 0;

        for (Piece piece : state.getAllPieces()) {
            material += piece.isWhite() ? piece.getValue() : -piece.getValue();
        }
        return material;
    }
}
