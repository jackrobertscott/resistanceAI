package s21504053;

import java.util.*;

public class GroundsKeeper {
    private HashMap<Move, Integer[]> longTermSpy;
    private HashMap<Move, Integer[]> longTermNonSpy;

    public GroundsKeeper() {
        longTermSpy = createMoves();
        longTermNonSpy = createMoves();
    }

    /**
     * Create a new map to use
     *
     * @return map
     */
    public HashMap<Move, Integer[]> plant() {
        return createMoves();
    }

    /**
     * Record a move made and if it was true
     *
     * @param move the action taken
     * @param truthy if the action was spyish or not
     */
    public void feed(HashMap<Move, Integer[]> shrub, Move move, boolean truthy) {
        shrub.get(move)[0]++;
        if (truthy) {
            shrub.get(move)[1]++;
        }
    }

    /**
     * Record if model was on the winning team or not
     *
     * @param success the result of game for given agent (win or lose)
     */
    public void reap(HashMap<Move, Integer[]> shrub, boolean success, boolean spy) {
        if (success) { // filter off unsuccessful plays so we get super smart AI
            if (spy) {
                for (Move m : shrub.keySet()) {
                    longTermSpy.get(m)[0] += shrub.get(m)[0];
                    longTermSpy.get(m)[1] += shrub.get(m)[1];
                }
            } else {
                for (Move m : shrub.keySet()) {
                    longTermNonSpy.get(m)[0] += shrub.get(m)[0];
                    longTermNonSpy.get(m)[1] += shrub.get(m)[1];
                }
            }
        }
    }

    /**
     * @return data record keeper
     */
    private HashMap<Move, Integer[]> createMoves() {
        HashMap<Move, Integer[]> moves = new HashMap<Move, Integer[]>();
        Integer[] empty = new Integer[2];
        Arrays.fill(empty, 0);
        moves.put(Move.SELECTED_TEAM_SUCCESSFUL, empty.clone());
        moves.put(Move.SELECTED_TEAM_UNSUCCESSFUL, empty.clone());
        moves.put(Move.ON_TEAM_SUCCESSFUL, empty.clone());
        moves.put(Move.ON_TEAM_UNSUCCESSFUL, empty.clone());
        moves.put(Move.VOTED_TEAM_SUCCESSFUL, empty.clone());
        moves.put(Move.VOTED_TEAM_UNSUCCESSFUL, empty.clone());
        return moves;
    }

    /**
     * Return the model in string form
     *
     * @return string representation of the model
     */
    private String movesToString(HashMap<Move, Integer[]> moves, boolean spy) {
        String out = "";
        out += "SELECTED_TEAM_SUCCESSFUL: " + Arrays.toString(moves.get(Move.SELECTED_TEAM_SUCCESSFUL)) + "\n";
        out += "SELECTED_TEAM_UNSUCCESSFUL: " + Arrays.toString(moves.get(Move.SELECTED_TEAM_UNSUCCESSFUL)) + "\n";
        out += "ON_TEAM_SUCCESSFUL: " + Arrays.toString(moves.get(Move.ON_TEAM_SUCCESSFUL)) + "\n";
        out += "ON_TEAM_UNSUCCESSFUL: " + Arrays.toString(moves.get(Move.ON_TEAM_UNSUCCESSFUL)) + "\n";
        out += "VOTED_TEAM_SUCCESSFUL: " + Arrays.toString(moves.get(Move.VOTED_TEAM_SUCCESSFUL)) + "\n";
        out += "VOTED_TEAM_UNSUCCESSFUL: " + Arrays.toString(moves.get(Move.VOTED_TEAM_UNSUCCESSFUL)) + "\n";
        out += "IS_SPY: " + spy;
        return out;
    }

    /**
     * Print out the long term spy data
     */
    public void printLongTermSpy() {
        System.out.println(movesToString(longTermSpy, true));
    }

    /**
     * Print out the long term non-spy data
     */
    public void printLongTermNonSpy() {
        System.out.println(movesToString(longTermNonSpy, false));
    }
}