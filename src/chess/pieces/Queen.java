package chess.pieces;

import chess.board.Square;
import chess.exceptions.CanNotMoveToSquareException;
import chess.exceptions.CheckMateException;
import chess.exceptions.EnPassantException;
import chess.exceptions.KingCheckedException;
import chess.game.Move;
import chess.game.State;

import java.util.ArrayList;

public class Queen extends Piece {
    public Queen(boolean white) {
        super(white, 90);
    }

    @Override
    public ArrayList<Square> getPseudoLegalMoves(State state) {
        ArrayList<Square> occupiedSquares = new ArrayList<>();
        occupiedSquares.addAll(state.getAllValidSquaresCross(this));
        occupiedSquares.addAll(state.getAllValidSquaresDiagonal(this));
        return occupiedSquares;
    }

    @Override
    public boolean isValidMove(Move move) {
        return move.isStraight() || move.isDiagonal();
    }

    @Override
    public void move(State state, Move move) {
        super.move(state, move);
    }

    @Override
    public String toString() {
        return this.isWhite() ? "♕" : "♛";
    }

}
