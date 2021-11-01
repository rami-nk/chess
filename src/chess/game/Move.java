package chess.game;

import chess.board.Square;

public class Move {

    public final Square to;
    public final Square from;

    public Move(Square from, Square to) {
        this.from = from;
        this.to = to;
    }

    public int distance() {
        return Math.max(Math.abs(from.getRow() - to.getRow()),
                Math.abs(from.getColumn() - to.getColumn()));
    }

    public boolean isDiagonal() {
        return Math.abs(from.getRow() - to.getRow()) == Math.abs(from.getColumn() - to.getColumn());
    }

    public boolean isStraight() {
    return (((from.getColumn() - to.getColumn()) == 0) && ((from.getRow() - to.getRow()) != 0))
                || (((from.getColumn() - to.getColumn()) != 0) && ((from.getRow() - to.getRow()) == 0));
    }

    public boolean isKnightMove() {
        return Math.abs(from.getRow() - to.getRow()) * Math.abs(from.getColumn() - to.getColumn()) == 2;
    }

    public Square getDestinationSquare() {
        return to;
    }

    public Square getPreviousSquare() {
        return from;
    }

    @Override
    public String toString() {
        return from + "" + to;
    }
}