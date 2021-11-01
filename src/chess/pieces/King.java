package chess.pieces;

import chess.board.Square;
import chess.exceptions.CanNotMoveToSquareException;
import chess.exceptions.CheckMateException;
import chess.exceptions.EnPassantException;
import chess.exceptions.KingCheckedException;
import chess.game.Move;
import chess.game.State;

import java.util.ArrayList;

public class King extends Piece {

    private boolean inCheck;

    public King(boolean white) {
        super(white, 900);
    }

    @Override
    public ArrayList<Square> getPseudoLegalMoves(State state) {
        ArrayList<Square> occupiedSquares = new ArrayList<>();
        Square pieceSquare = state.getSquareOfPiece(this);
        int row = pieceSquare.getRow() - 1;
        int column = pieceSquare.getColumn() - 1;
        int rowOffset = -1;
        int columnOffset = 1;

        // Drei Felder über dem König und drei Felder unter dem König
        for (int i=0; i < 2; i++) {
            for (int j=0; j < 3; j++) {
                if (state.inBoardRange(row + rowOffset, column + columnOffset)) {
                    Square square = new Square(row + rowOffset + 1, column + columnOffset + 1);
                    occupiedSquares.add(square);
                }
                columnOffset -= 1;
            }
            rowOffset *= -1;
            columnOffset = 1;
        }
        // Zwei Felder neben dem König
        for (int i=0; i < 2; i++) {
            if (state.inBoardRange(row, column + columnOffset)) {
                Square square = new Square(row + 1, column + columnOffset + 1);
                occupiedSquares.add(square);
            }
            columnOffset *=-1;
        }

        return occupiedSquares;
    }

    @Override
    public boolean isValidMove(Move move) {
        return Math.abs(move.distance()) <= 2;
    }

    @Override
    public void move(State state, Move move) {
        if (Math.abs(move.distance()) >= 2) {
            boolean shorT = (move.from.getColumn() - move.to.getColumn()) < 0;
            castle(state, shorT);
            return;
        }
        if (Math.abs(move.distance()) == 1) {
            super.move(state, move);
        }
    }

    private void castle(State state, boolean shorT) {
        int homeRow = isWhite() ? 0 : 7;
        int direction = shorT ? 2 : -2;
        int rookOffset = shorT ? -2 : 3;
        Square from = state.getSquareOfPiece(this);
        Piece rook = shorT ? state.getPiece(homeRow, 7) : state.getPiece(homeRow, 0);
        if (rook != null) {
            Square rookSquare = state.getSquareOfPiece(rook);
            super.move(state, new Move(from, new Square(homeRow + 1, from.getColumn() + direction)));
            rook.move(state, new Move(rookSquare, new Square(homeRow + 1, rookSquare.getColumn() + rookOffset)));
        }
    }

    public void setInCheck(boolean inCheck) {
        this.inCheck = inCheck;
    }

    public boolean isInCheck() {
        return inCheck;
    }

    @Override
    public String toString() {
        return this.isWhite() ? "♔" : "♚";
    }

}
