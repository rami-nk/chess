package chess.game.bots;

import chess.game.Move;
import chess.game.Player;
import chess.game.State;

import java.util.ArrayList;
import java.util.Random;

public class RandomBot extends Player {

    public RandomBot(boolean white) {
        super(white);
    }

    public Move getMove(State state) {
        Random random = new Random();
        ArrayList<Move> allMoves = state.getAllLegalMoves();
        return allMoves.get(Math.abs(random.nextInt()) % allMoves.size());
    }
}
