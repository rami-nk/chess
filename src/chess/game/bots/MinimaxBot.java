package chess.game.bots;

import chess.game.Game;
import chess.game.Move;
import chess.game.Player;
import chess.game.State;

public class MinimaxBot extends Player {

    private static final int desiredDepth = 2;
    private static Move bestMove = null;

    public MinimaxBot(boolean white) {
        super(white);
    }

    public Move getMove(Game game) {
        minimax(desiredDepth, game);
        return bestMove;
    }

    private int minimax(int depth, Game game) {
        State state = game.getCurrentState();
        if (depth == 0) return state.evaluate();

        int maxRating = Integer.MIN_VALUE;

        for (Move move : state.getAllLegalMoves()) {
            game.makeMove(move);
            int rating = -minimax(depth - 1, game);
            game.undo();
            if (rating > maxRating) {
                maxRating = rating;
                if (depth == desiredDepth) {
                    bestMove = move;
                }
            }
        }
        return maxRating;
    }


}