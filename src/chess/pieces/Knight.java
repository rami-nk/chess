package chess.pieces;

import chess.board.Square;
import chess.exceptions.CanNotMoveToSquareException;
import chess.exceptions.CheckMateException;
import chess.exceptions.EnPassantException;
import chess.exceptions.KingCheckedException;
import chess.game.Move;
import chess.game.State;

import java.util.ArrayList;

public class Knight extends Piece {

    public Knight(boolean white) {
        super(white, 30);
    }

    @Override
    public ArrayList<Square> getPseudoLegalMoves(State state) {
        ArrayList<Square> occupiedSquares = new ArrayList<>();
        Square pieceSquare = state.getSquareOfPiece(this);
        int row = pieceSquare.getRow() - 1;
        int column = pieceSquare.getColumn() - 1;
        int firstOffset = 2;
        int secondOffset = 1;

        for (int i=0; i < 8; i++) {
            if (state.inBoardRange(row + firstOffset, column + secondOffset)) {
                occupiedSquares.add(new Square(row + firstOffset + 1, column + secondOffset + 1));
            }
            secondOffset *= -1;
            if (i%2 != 0) firstOffset *= -1;
            if (i == 3) {
                int tmp = secondOffset;
                secondOffset = firstOffset;
                firstOffset = tmp;
            }
        }
        return occupiedSquares;
    }

    @Override
    public boolean isValidMove(Move move) {
        return move.isKnightMove();
    }

    @Override
    public void move(State state, Move move) {
        super.move(state, move);
    }

    @Override
    public String toString() {
        return this.isWhite() ? "♘" : "♞";
    }
}
