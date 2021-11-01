package chess.pieces;

import chess.board.Square;
import chess.exceptions.*;
import chess.game.Move;
import chess.game.State;

import java.util.ArrayList;

public abstract class Piece {

    private final boolean white;
    private boolean moved;
    private int value;

    public Piece(boolean white, int value) {
        this.white = white;
        this.value = value;
        this.moved = false;
    }

    public int getValue() {
        return value;
    }

    public boolean isWhite() {
        return white;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public abstract ArrayList<Square> getPseudoLegalMoves(State state);

    public abstract boolean isValidMove(Move move);

    public void move(State state, Move move) {
        state.getBoard()[move.from.getRow() - 1][move.from.getColumn() - 1] = null;
        state.getBoard()[move.to.getRow() - 1][move.to.getColumn() - 1] = this;
        if (!isMoved()) setMoved(true);
    }
}
