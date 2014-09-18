package checkers;

import java.util.*;

/**
 * Litterature reference: Artificial Intelligence A Modern Approach (3rd
 * Edition) P.168,169.
 * 
 * @Authors Michael Lindell, Calle Bergmark.
 */
public class Player {

    private int player;
    private int opponent;
    private final int depth = 11;

    /**
     * Performs a move
     *
     * @param State The current state of the board
     * @param Due Time before which we must have returned
     * @return The next state the board is in after our move
     */
    public GameState play(final GameState State, final Deadline Due) {
        Vector<GameState> lNextStates = new Vector<GameState>();
        State.findPossibleMoves(lNextStates);
        player = State.getNextPlayer();
        opponent = lNextStates.elementAt(0).getNextPlayer();
        int index = 0;
        if (lNextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(State, new Move());
        } else if (lNextStates.size() == 1) {
            return lNextStates.get(0);
        } else {

            int tStore; // Temporary storage for the min value later on in the for-loop.
            int value = -2000;
            int alpha = -2000;
            int beta = 2000;

            for (int i = 0; i < lNextStates.size(); i++) {
                tStore = CheckForMinValue(lNextStates.get(i), 0, alpha, beta);
                if (tStore > value) {
                    index = i;
                }
                value = Math.max(value, tStore);
                alpha = Math.max(alpha, value);
            }
        }

        return lNextStates.elementAt(index);
    }

    /**
     * For references to the Constants - (Constants.CELL_RED for e.g):
     * Constants.java. Method that Check the score on the two players and
     * returns the total score (PlayerScore - OpponentScore).
     *
     *
     * @param state The current gamestate.
     * @return The total score on the board.
     */
    private int Evaluate(GameState state) {
        int playerPieces = 0;
        int enemyPieces = 0;

        if (state.isDraw()) {
            return 0;
        }
        if (player == Constants.CELL_RED) {
            if (state.isRedWin()) {
                return 700;
            } else if (state.isWhiteWin()) {
                return -700;
            }
        } else if (player == Constants.CELL_WHITE) {
            if (state.isRedWin()) {
                return -700;
            } else if (state.isWhiteWin()) {
                return 700;
            }
        }

        for (int i = 1; i <= state.cSquares; i++) {
            int piece = state.get(i); //Piece on the board.
            /* if player is a king. */
            if (0 != (piece & player)) {
                if (0 != (piece & Constants.CELL_KING)) {
                    playerPieces += 5;
                } else {
                    playerPieces++;
                }
                /* if opponent is a king. */
            } else if (0 != (piece & opponent)) {
                if (0 != (piece & Constants.CELL_KING)) {
                    enemyPieces += 5;
                } else {
                    enemyPieces++;
                }
            }
        }
        return playerPieces - enemyPieces;
    }

    /**
     * Method that uses alpha-beta pruning to find the worst possible move.
     *
     * @param state The current gamestate.
     * @param currentDepth The current deapth of moves.
     * @param alpha The alpha variable.
     * @param beta The beta variable.
     * @return Returns the MinValue (Worst move).
     */
    public int CheckForMinValue(GameState state, int currentDepth, int alpha, int beta) {
        if (state.isEOG()) //If the current gamestate is EndOfGame.
        {
            return Evaluate(state);
        }
        if (currentDepth < depth) {
            currentDepth++;
        } else {
            return Evaluate(state);
        }

        int value = 2000;

        Vector<GameState> newStates = new Vector<GameState>();
        state.findPossibleMoves(newStates);

        for (int i = 0; i < newStates.size(); i++) {
            value = Math.min(value, CheckForMaxValue(newStates.get(i), currentDepth, alpha, beta));

            if (alpha >= value) {
                return value;
            }

            beta = Math.min(value, beta);
        }
        return value;
    }

    /**
     * Method that uses alpha-beta prunning to find the largest
     * possible move from the player point of view.
     *
     * @param state Current gamestate.
     * @param currentDepth The current deapth of moves.
     * @param alpha The alpha variable.
     * @param beta The beta variable.
     * @return Returns the MaxValue (The longest move).
     */
    public int CheckForMaxValue(GameState state, int currentDepth, int alpha, int beta) {

        if (state.isEOG()) //If the current gamestate is EndOfGame.
        {
            return Evaluate(state);
        }
        if (currentDepth < depth) {
            currentDepth++;
        } else {
            return Evaluate(state);
        }

        int value = -2000;

        Vector<GameState> newStates = new Vector<GameState>();
        state.findPossibleMoves(newStates);

        for (int i = 0; i < newStates.size(); i++) {
            value = Math.max(value, CheckForMinValue(newStates.get(i), currentDepth, alpha, beta));

            if (value >= beta) {
                return value;
            }

            alpha = Math.max(alpha, value);
        }
        return value;
    }
}
