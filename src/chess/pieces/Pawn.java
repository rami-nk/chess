package chess.pieces;

import chess.board.Square;
import chess.exceptions.CanNotMoveToSquareException;
import chess.exceptions.CheckMateException;
import chess.exceptions.EnPassantException;
import chess.exceptions.KingCheckedException;
import chess.game.Move;
import chess.game.State;

import java.util.ArrayList;

public class Pawn extends Piece {

    public Pawn(boolean white) {
        super(white, 10);
    }

    @Override
    public ArrayList<Square> getPseudoLegalMoves(State state) {
        ArrayList<Square> occupiedSquares = new ArrayList<>();
        Square pieceSquare = state.getSquareOfPiece(this);
        int row = pieceSquare.getRow() - 1;
        int column = pieceSquare.getColumn() - 1;
        int offset = 1;
        int white = this.isWhite() ? 1 : -1;
        boolean moved = (row + 1) != (this.isWhite() ? 2 : 7);
        this.setMoved(moved);

        if (!this.isMoved()) {
            occupiedSquares.add(new Square(row +(2*white) + 1, column + 1));
        }
        if (state.inBoardRange(row + white, column)) {
            occupiedSquares.add(new Square(row + white + 1, column + 1));
        }
        for (int i = 0; i < 2; i++) {
            if (state.inBoardRange(row + white, column + offset)) {
                Square square = new Square(row + white + 1, column + offset + 1);
                occupiedSquares.add(square);
            }
            offset *= -1;
        }

        return occupiedSquares;
    }

    @Override
    public boolean isValidMove(Move move) {
        return Math.abs(move.distance()) <= 2;
    }

    private boolean isEnPassantMove(State state, Move move) {
        Square pieceSquare = state.getSquareOfPiece(this);
        boolean onEnPassantSquare = (isWhite() && pieceSquare.getRow() == 5) || (!isWhite() && pieceSquare.getRow() == 4);
        boolean squareIsEmpty = state.getPiece(move.to) == null;
        // TODO: Bessere Überprüfung ob en passant!
        return squareIsEmpty && onEnPassantSquare;
    }

    private void enPassant(State state, Move move) {
        super.move(state, move);
        Square pieceSquare = state.getSquareOfPiece(this);
        int offset = isWhite() ? -1 : 1;
        Square pawnSquare = new Square(pieceSquare.getRow() + offset, pieceSquare.getColumn());
        state.setPiece(pawnSquare, null);
    }

    private boolean isPromoteMove(Move move) {
        int promoteRow = isWhite() ? 8 : 1;
        return move.to.getRow() == promoteRow;
    }

    private void promote(State state, Move move) {
        super.move(state, move);
        Queen queen = new Queen(isWhite());
        state.setPiece(move.to, queen);
    }

    @Override
    public void move(State state, Move move) {
        if (isEnPassantMove(state, move)) {
            enPassant(state, move);
            return;
        }
        if (isPromoteMove(move)) {
            promote(state, move);
            return;
        }
        super.move(state, move);
    }

    @Override
    public String toString() {
        return this.isWhite() ? "♙" : "♟";
    }

}
