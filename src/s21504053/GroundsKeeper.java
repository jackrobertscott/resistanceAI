package s21504053;

import java.util.*;

public class GroundsKeeper {
    private final static double weightA = (0.17636363636363636 - 0.22931541369172617);
    private final static double weightB = (0.15196733481811434 - 0.19466975666280417);
    private final static double weightC = (0.05421487603305785 - 0.5403191936161277);
    private final static double weightD = (0.8007423904974016 - 0.6616454229432214);
    private final static double weightE = (0.7766942148760331 - 0.9615707685846283);
    private final static double weightF = (0.7937639198218263 - 0.7439165701042874);

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
     * Guess the spyishness of an agent
     *
     * @param shrub set of moves made by a agent
     * @return spyishness
     */
    public double tame(HashMap<Move, Integer[]> shrub) {
        double spyishness = 0.0;
        spyishness += calculatePercentage(shrub, Move.SELECTED_TEAM_SUCCESSFUL) * weightA;
        spyishness += calculatePercentage(shrub, Move.SELECTED_TEAM_UNSUCCESSFUL) * weightB;
        spyishness += calculatePercentage(shrub, Move.ON_TEAM_SUCCESSFUL) * weightC;
        spyishness += calculatePercentage(shrub, Move.ON_TEAM_UNSUCCESSFUL) * weightD;
        spyishness += calculatePercentage(shrub, Move.VOTED_TEAM_SUCCESSFUL) * weightE;
        spyishness += calculatePercentage(shrub, Move.VOTED_TEAM_UNSUCCESSFUL) * weightF;
        return spyishness;
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
    public String movesToString(HashMap<Move, Integer[]> moves) {
        String out = "-------------------------------\n";
        out += "SELECTED_TEAM_SUCCESSFUL: " + calculatePercentage(moves, Move.SELECTED_TEAM_SUCCESSFUL) + "\n";
        out += "SELECTED_TEAM_UNSUCCESSFUL: " + calculatePercentage(moves, Move.SELECTED_TEAM_UNSUCCESSFUL) + "\n";
        out += "ON_TEAM_SUCCESSFUL: " + calculatePercentage(moves, Move.ON_TEAM_SUCCESSFUL) + "\n";
        out += "ON_TEAM_UNSUCCESSFUL: " + calculatePercentage(moves, Move.ON_TEAM_UNSUCCESSFUL) + "\n";
        out += "VOTED_TEAM_SUCCESSFUL: " + calculatePercentage(moves, Move.VOTED_TEAM_SUCCESSFUL) + "\n";
        out += "VOTED_TEAM_UNSUCCESSFUL: " + calculatePercentage(moves, Move.VOTED_TEAM_UNSUCCESSFUL);
        return out;
    }

    private double calculatePercentage(HashMap<Move, Integer[]> moves, Move move) {
        Integer[] action = moves.get(move);
        if (action[0] == 0) return 0;
        return (double) action[1] / (double) action[0];
    }

    /**
     * Print out the long term spy data
     */
    public void printLongTermSpy() {
        String msg = movesToString(longTermSpy);
        msg += "\nIS_SPY: true";
        System.out.println(msg);
    }

    /**
     * Print out the long term non-spy data
     */
    public void printLongTermNonSpy() {
        String msg = movesToString(longTermNonSpy);
        msg += "\nIS_SPY: false";
        System.out.println(msg);
    }
}