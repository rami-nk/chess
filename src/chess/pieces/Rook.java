package chess.pieces;

import chess.board.Square;
import chess.exceptions.CanNotMoveToSquareException;
import chess.exceptions.CheckMateException;
import chess.exceptions.EnPassantException;
import chess.exceptions.KingCheckedException;
import chess.game.Move;
import chess.game.State;

import java.util.ArrayList;

public class Rook extends Piece {

    public Rook(boolean white) {
        super(white, 50);
    }

    @Override
    public ArrayList<Square> getPseudoLegalMoves(State state) {
        return state.getAllValidSquaresCross(this);
    }

    @Override
    public boolean isValidMove(Move move) {
        return move.isStraight();
    }

    @Override
    public void move(State state, Move move) {
        super.move(state, move);
    }

    @Override
    public String toString() {
        return this.isWhite() ? "♖" : "♜";
    }

}
