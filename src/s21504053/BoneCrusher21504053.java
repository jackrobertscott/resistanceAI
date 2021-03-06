/**
 * Author: Jack Scott
 * Student #: 21054053
 * Last Revision: 31-10-16
 */

package s21504053;

import cits3001_2016s2.*;
import java.util.*;

/**
 * A Java class for an agent to play in Resistance.
 * Each agent is given a single capital letter, which will be their name for the game.
 * The game actions will be encoded using strings.
 * The agent will be created entirely in a single game, and the agent must maintain its own state.
 * Methods will be used for informing agents of game events (get_ methods, must return in 100ms) or requiring actions (do_ methods, must return in 1000ms).
 * If actions do not meet the required specification, a nominated default action will be recorded.
 **/
public class BoneCrusher21504053 implements Agent {
    private final static boolean DEBUG = false;

    private String name; // agent name
    private String players; // all players in the game
    private GroundsKeeper gk;
    private HashMap<Character, HashMap<Move, Integer[]>> stats;
    private Random random;

    private int round; // mission number
    private String team; // members on the mission executed
    private String yays; // members who voted for mission
    private String leader; // mission leader proposed
    private HashSet<Integer> failures; // set of failed missions
    private int votes; // number of votes for executed round

    private boolean spy; // is this agent a spy
    private String spies; // all known spies

    public BoneCrusher21504053() {
        gk = new GroundsKeeper();
        stats = new HashMap<Character, HashMap<Move, Integer[]>>();
        random = new Random();
        failures = new HashSet<Integer>();
        votes = 0;
    }

    /**
     * Reports the current status, including players name, the name of all players, the names of the spies (if known), the round number and the number of failed missions
     *
     * @param name     a string consisting of a single letter, the agent's names.
     * @param players  a string consisting of one letter for everyone in the game.
     * @param spies    a String consisting of the latter name of each spy, if the agent is a spy, or n questions marks where n is the number of spies allocated; this should be sufficient for the agent to determine if they are a spy or not.
     * @param round    the next round to be launched
     * @param failures the number of failed missions
     */
    public void get_status(String name, String players, String spies, int round, int failures) {
        this.name = name;
        this.players = players;
        this.spies = spies;
        this.spy = spies.contains(name);
        this.round = round;
        boolean failed = false;

        if (this.failures.size() != failures) { // mission failed
            this.failures.add(round - 1);
            failed = true;
        }

        for (char player : players.toCharArray()) {
            if (round == 1) {
                stats.put(player, gk.plant());
            } else {
                if (failed) {
                    gk.feed(stats.get(player), Move.SELECTED_TEAM_UNSUCCESSFUL, leader.equals(player+""));
                    gk.feed(stats.get(player), Move.ON_TEAM_UNSUCCESSFUL, team.contains(player+""));
                    gk.feed(stats.get(player), Move.VOTED_TEAM_UNSUCCESSFUL, yays.contains(player+""));
                } else {
                    gk.feed(stats.get(player), Move.SELECTED_TEAM_SUCCESSFUL, leader.equals(player+""));
                    gk.feed(stats.get(player), Move.ON_TEAM_SUCCESSFUL, team.contains(player+""));
                    gk.feed(stats.get(player), Move.VOTED_TEAM_SUCCESSFUL, yays.contains(player+""));
                }
            }
        }
    }

    /**
     * Based on a number of observations combined with weightings, guess who is most likely to be a spy
     *
     * @return a string of assumed spies
     */
    private String guessSpies() {
        if (spies.indexOf('?') == -1) return spies; // when spies are already known

        int numPlayers = players.length();
        char peeps[] = new char[numPlayers]; // don't include self
        double data[] = new double[numPlayers]; // don't include self
        Arrays.fill(peeps, '0');
        Arrays.fill(data, Double.NEGATIVE_INFINITY);

        for (int i = 0; i < numPlayers; i++) {
            char player = players.charAt(i);
            double spyishness = gk.tame(stats.get(player));
            if (name.equals(player+"")) spyishness = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < numPlayers; j++) {
                if (data[j] < spyishness) {
                    for (int a = numPlayers - 1; a > j; a--) {
                        data[a] = data[a - 1];
                        peeps[a] = peeps[a - 1];
                    }
                    data[j] = spyishness;
                    peeps[j] = player;
                    break;
                }
            }
        }

        String guesses = "";
        for (int i = 0; i < spies.length(); i++) {
            guesses += peeps[i];
        }
        return guesses;
    }

    /**
     * Nominates a group of agents to go on a round.
     * If the String does not correspond to a legitimate round (<i>number</i> of distinct agents, in a String),
     * a default nomination of the first <i>number</i> agents (in alphabetical order) will be used, as if this was what the agent nominated.
     *
     * @param number the number of agents to be sent on the round
     * @return a String containing the names of all the agents in a round
     */
    public String do_Nominate(int number) {
        HashSet<Character> nominees = new HashSet<Character>();
        nominees.add(name.charAt(0)); // RULE: add self to the nominees
        String guesses = spy ? spies : guessSpies();

        for (int i = 0; i < number; i++) {
            char c = players.charAt(random.nextInt(players.length()));
            while ((nominees.contains(c) || guesses.contains(c+"")) && nominees.size() < number) {
                c = players.charAt(random.nextInt(players.length()));
            }
            nominees.add(c); // RULE: do not add any (other) spies
        }

        String nominated = "";
        for (Character c : nominees) nominated += c;
        return nominated;
    }

    /**
     * Provides information of a given round.
     *
     * @param leader the leader who proposed the round
     * @param team   a String containing the names of all the agents in the round
     **/
    public void get_ProposedMission(String leader, String team) {
        // update data with who nominated who...
        this.leader = leader;
        this.team = team;
    }

    /**
     * Get the number of mentioned names in the group
     *
     * @param names the player group to check
     * @return the number of names that were contained in the group
     */
    private int numberContained(String group, String names) {
        int contained = 0;
        for (char check : group.toCharArray()) {
            if (names.indexOf(check) != -1) {
                contained++;
            }
        }
        return contained;
    }

    /**
     * Gets an agents votes on the last reported round
     *
     * @return true, if the agent votes for the round, false, if they votes against it.
     */
    public boolean do_Vote() {
        votes++;
        if (round == 1) {
            return true; // RULE: approve any mission on the first round
        }
        int spiesOnMission = numberContained(team, spy ? spies : guessSpies());
        if (spy) { // is government spy
            if (failures.size() == 2) {
                if (round + failures.size() >= 5) {
                    return true; // e.g. round 4 with only one failure so far, must fail rounds 4 + 5 to win
                } else {
                    return touchOfRandom(true); // RULE: approve mission if a spy is on it and nearly won
                }
            }
            if (team.length() == spiesOnMission) {
                return touchOfRandom(false); // RULE: don't approve a mission with only spies
            }
            if (spies.length() == spiesOnMission || spiesOnMission == 0) {
                return touchOfRandom(false); // RULES: reject mission with zero or both spies on mission
            }
            return touchOfRandom(true); // RULE: approve if atleast one spy is on the team
        } else { // is resistance
            if (votes == 5) {
                return true; // RULE: approve 5th mission else government wins
            }
            if (leader.equals(name)) {
                return true; // RULE: approve mission if I am leader
            }
            if (team.length() == 3 && team.contains(name)) {
                return false; // RULE: don't approve if team of 3 and agent not on team
            }
            if (spiesOnMission > 0) {
                return false; // RULE: don't approve if spy is on mission team
            }
            return true; // RULE: approve all other missions
        }
    }

    /**
     * Reports the votes for the previous round
     *
     * @param yays the names of the agents who voted for the round
     **/
    public void get_Votes(String yays) {
        this.yays = yays;
    }

    /**
     * Reports the agents being sent on a round.
     * Should be able to be inferred from tell_ProposedMission and tell_Votes, but included for completeness.
     *
     * @param team the Agents being sent on a round
     **/
    public void get_Mission(String team) {
        votes = 0; // reset votes for the next round
    }

    /**
     * Agent chooses to betray or not.
     *
     * @return true if agent betrays, false otherwise
     **/
    public boolean do_Betray() {
        if (!spy) {
            return false; // should only be called if is spy... but just in case
        }
        if (round + failures.size() >= 5) {
            return true; // e.g. round 4 with only one failure so far, must fail rounds 4 + 5 to win
        }
        if (failures.size() == 2) {
            return true; // RULE: winning move!
        }
        int spiesOnMission = numberContained(team, spies);
        if (team.length() == spiesOnMission) {
            return false; // RULE: don't betray if the whole team are spies
        }
        if (spiesOnMission == 1 && team.indexOf(name) != 0) {
            return touchOfRandom(true);
        }
        return touchOfRandom(true);
    }

    /**
     * Reports the number of people who betrayed the round
     *
     * @param traitors the number of people on the round who chose to betray (0 for success, greater than 0 for failure)
     **/
    public void get_Traitors(int traitors) {
        if (!spy && traitors == spies.length()) {
            spies = team;
        }
    }

    /**
     * Optional method to accuse other Agents of being spies.
     * Default action should return the empty String.
     * Convention suggests that this method only return a non-empty string when the accuser is sure that the accused is a spy.
     * Of course convention can be ignored.
     *
     * @return a string containing the name of each accused agent.
     */
    public String do_Accuse() {
        if (spy) {
            int number = random.nextInt(players.length());
            HashSet<Character> team = new HashSet<Character>();
            for (int i = 0; i < number; i++) {
                char c = players.charAt(random.nextInt(players.length()));
                while (team.contains(c)) c = players.charAt(random.nextInt(players.length()));
                team.add(c);
            }
            String tm = "";
            for (Character c : team) tm += c;
            return tm;
        } else {
            return guessSpies();
        }
    }

    /**
     * Optional method to process an accusation.
     *
     * @param accuser the name of the agent making the accusation.
     * @param accused the names of the Agents being Accused, concatenated in a String.
     */
    public void get_Accusation(String accuser, String accused) {
    }

    /**
     * This function is to slightly put off compeditors by making slightly random decisions.
     * This should only be used on non-critical return values or values that will not lose the game
     *
     * @param expected the value to slightly randomise
     * @return a slightly randomised value
     */
    private boolean touchOfRandom(boolean expected) {
        return expected;// random.nextInt(10) == 0 ? !expected : expected;
    }

    /**
     * Log debug text to the console when set to debug mode
     *
     * @param msg the message to log
     */
    private void debug(String msg) {
        if (DEBUG) {
            System.out.println("~~~DEBUG: " + msg);
        }
    }
}
