package s21504053;

import java.util.*;

public class Model {
    private boolean success; // on successful team
    private boolean spy; // was a spy
    private HashMap<Move, Integer[]> moves;

    public Model() {
        moves = new HashMap<Move, Integer[]>();
        Integer[] empty = new Integer[2];
        Arrays.fill(empty, 0);
        moves.put(Move.SELECTED_TEAM_SUCCESSFUL, empty.clone());
        moves.put(Move.SELECTED_TEAM_UNSUCCESSFUL, empty.clone());
        moves.put(Move.ON_TEAM_SUCCESSFUL, empty.clone());
        moves.put(Move.ON_TEAM_UNSUCCESSFUL, empty.clone());
        moves.put(Move.VOTED_TEAM_SUCCESSFUL, empty.clone());
        moves.put(Move.VOTED_TEAM_UNSUCCESSFUL, empty.clone());
    }

    /**
     * Record a move made and if it was true
     *
     * @param move the action taken
     * @param truthy if the action was spyish or not
     */
    public void act(Move move, boolean truthy) {
        moves.get(move)[0]++;
        if (truthy) {
            moves.get(move)[1]++;
        }
    }

    /**
     * Record if model was on the winning team or not
     *
     * @param success the result of game for given agent (win or lose)
     */
    public void end(boolean success, boolean spy) {
        this.success = success;
        this.spy = spy;
    }

    /**
     * Return the model in string form
     *
     * @return string representation of the model
     */
    public String toString() {
        String out = "";
        out += "SELECTED_TEAM_SUCCESSFUL: " + Arrays.toString(moves.get(Move.SELECTED_TEAM_SUCCESSFUL)) + "\n";
        out += "SELECTED_TEAM_UNSUCCESSFUL: " + Arrays.toString(moves.get(Move.SELECTED_TEAM_UNSUCCESSFUL)) + "\n";
        out += "ON_TEAM_SUCCESSFUL: " + Arrays.toString(moves.get(Move.ON_TEAM_SUCCESSFUL)) + "\n";
        out += "ON_TEAM_UNSUCCESSFUL: " + Arrays.toString(moves.get(Move.ON_TEAM_UNSUCCESSFUL)) + "\n";
        out += "VOTED_TEAM_SUCCESSFUL: " + Arrays.toString(moves.get(Move.VOTED_TEAM_SUCCESSFUL)) + "\n";
        out += "VOTED_TEAM_UNSUCCESSFUL: " + Arrays.toString(moves.get(Move.VOTED_TEAM_UNSUCCESSFUL)) + "\n";
        out += "TEAM_SUCCESSFUL: " + success + "\n";
        out += "IS_SPY: " + spy;
        return out;
    }
}