package s21504053;

import java.io.*;
import java.util.*;

public class GroundsKeeper {
    private HashMap<Move, Integer[]> longTermSpy;
    private HashMap<Move, Integer[]> longTermNonSpy;
    private HashMap<Move, Double> potion;

    public GroundsKeeper() {
        longTermSpy = createMoves();
        longTermNonSpy = createMoves();
        potion = new HashMap<Move, Double>();
        brewPotion();
    }

    public GroundsKeeper(String file) {
        longTermSpy = createMoves();
        longTermNonSpy = createMoves();
        potion = new HashMap<Move, Double>();

        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            for (Move move : Move.values()) {
                double d = Double.parseDouble(dis.readUTF());
                potion.put(move, d);
//                System.out.println("~~~~~~~~~~~~~OUT: " + d);
            }
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * @param move   the action taken
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
        for (Move move : Move.values()) {
            spyishness += calculatePercentage(shrub, move) * potion.get(move);
        }
        return spyishness;
    }

    /**
     * Brew a glorious potion with many tasty ingredients including rotten eggs, giant's feet and ear wax.
     * Warning: may contain nuts.
     */
    public void brewPotion() {
        for (Move move : Move.values()) {
            potion.put(move, calculatePercentage(longTermSpy, move) - calculatePercentage(longTermNonSpy, move));
        }

        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream("memory.txt"));
            for (Move move : Move.values()) {
                String s = String.valueOf(potion.get(move));
//                System.out.println("~~~~~~~~~~~~~OUT: " + s);
                dos.writeUTF(s);
            }
            dos.flush();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        longTermSpy = createMoves();
        longTermNonSpy = createMoves();
    }

    /**
     * Show the ingredients of the brew
     */
    public void printIngredients() {
        String msg = "~~~POTION BREWED~~~\n";
        for (Move move : potion.keySet()) {
            msg += move.name() + ": " + potion.get(move) + "\n";
        }
        System.out.print(msg);
    }

    /**
     * @return data record keeper
     */
    private HashMap<Move, Integer[]> createMoves() {
        HashMap<Move, Integer[]> moves = new HashMap<Move, Integer[]>();
        Integer[] empty = new Integer[2];
        Arrays.fill(empty, 0);
        for (Move move : Move.values()) {
            moves.put(move, empty.clone());
        }
        return moves;
    }

    /**
     * Return the model in string form
     *
     * @return string representation of the model
     */
    public String movesToString(HashMap<Move, Integer[]> moves) {
        String out = "\n";
        for (Move move : Move.values()) {
            out += move.name() + ": " + calculatePercentage(moves, move) + "\n";
        }
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
        msg += "IS_SPY: true\n";
        System.out.println(msg);
    }

    /**
     * Print out the long term non-spy data
     */
    public void printLongTermNonSpy() {
        String msg = movesToString(longTermNonSpy);
        msg += "IS_SPY: false\n";
        System.out.println(msg);
    }
}