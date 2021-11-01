package chess.game;


import chess.game.bots.MinimaxBot;
import chess.game.bots.RandomBot;

import java.util.ArrayList;

public class Game {

    private State currentState;
    private Player player1;
    private Player player2;

    public Game() {
        currentState = new State();
        player1 = new Player(true);
        player2 = new MinimaxBot(false);
    }

    public Game(State state) {
        currentState = state;
    }

    public Game(String fen) {
        currentState = new State(fen, true);
        player1 = new Player(true);
        player2 = new MinimaxBot(false);
    }

    public void move(Move move) {
        if (!currentState.isCheckMate()) {
            if (isWhitesTurn() == player1.isWhite()) {
                makeMove(move);
                if (isWhitesTurn() == player2.isWhite() && !currentState.isCheckMate()) {
                    makeMove(((MinimaxBot) player2).getMove(this));
                }
            }
        }
    }

    public void makeMove(Move move) {
        if (currentState.isMoveLegal(move)) {
            State state = currentState.copy();
            state.move(move);
            state.setWhitesTurn(!isWhitesTurn());
            if (state.isLastMoveLegal()) {
                state.setNext(null);
                currentState.setNext(state);
                state.setPrevious(currentState);
                currentState = state;
            }
        }
    }

    public void undo() {
        if (currentState.getPrevious() != null) {
            currentState = currentState.getPrevious();
        }
    }

    public void redo() {
        if (currentState.getNext() != null) {
            currentState = currentState.getNext();
        }
    }

    public void udoAll() {
        while (currentState.getPrevious() != null) currentState = currentState.getPrevious();
    }

    public long perft(int depth) {
        ArrayList<Move> moves = currentState.getAllLegalMoves();
        int n_moves = moves.size();
        long totalNodes = 0L;

        if (depth == 1) {
            return n_moves;
        }
        for (Move move : moves) {
            makeMove(move);
            totalNodes += perft(depth - 1);
            if (currentState.getPrevious() != null) currentState = currentState.getPrevious();
        }
        return totalNodes;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public boolean isWhitesTurn() {
        return currentState.isWhitesTurn();
    }
}
