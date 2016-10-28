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
 *
 * @author Jack Scott
 **/


public class BoneCrusher implements cits3001_2016s2.Agent {

    final static boolean DEBUG = true;

    private String name; // agent name
    private String players; // all players in the game
    private Random random;

    private int round; // mission number
    private String team; // members on the mission executed
    private String leader; // mission leader proposed
    private HashSet<Integer> failures; // set of failed missions
    private int votes; // number of votes for executed round

    private boolean spy; // is this agent a spy
    private String spies; // all known spies


    public BoneCrusher() {
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

        this.round = round;
        if (this.failures.size() != failures) this.failures.add(round - 1);
        debug("Missions failed: " + this.failures.toString());

        if (spies.indexOf('?') == -1) {
            this.spies = spies;
            this.spy = spies.indexOf(name) != -1;
        } else {
            this.spies = "";
        }
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
        HashSet<Character> team = new HashSet<Character>();
        for (int i = 0; i < number; i++) {
            char c = players.charAt(random.nextInt(players.length()));
            while (team.contains(c)) c = players.charAt(random.nextInt(players.length()));
            team.add(c);
        }
        String tm = "";
        for (Character c : team) tm += c;
        return tm;
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
     * @param names the player group to check
     * @return
     */
    private boolean containsOneOrMore(String group, String names) {
        char[] checked = group.toCharArray();
        boolean contains = false;
        for (int i = 0; i < checked.length; i++) {
            if (names.indexOf(checked[i]) != -1) {
                contains = true;
                break;
            }
        }
        return contains;
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
        if (spy) { // is government spy
            if (this.failures.size() == 2) {
                return containsOneOrMore(team, spies); // RULE: approve mission if a spy is on it and nearly won
            }
        } else { // is resistance
            if (votes == 5) {
                return true; // RULE: approve 5th mission else government wins
            }
            if (leader.equals(name)) {
                return true; // RULE: approve mission if I am leader
            }
            if (spies.length() > 0) {
                return !containsOneOrMore(team, spies); // RULE: don't approve if spy is on mission team
            }
        }
        return (random.nextInt(2) != 0); // Keep 'em on their toes
    }

    /**
     * Reports the votes for the previous round
     *
     * @param yays the names of the agents who voted for the round
     **/
    public void get_Votes(String yays) {
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
        // this is only run if you are a spy
        return (spy ? random.nextInt(2) != 0 : false);
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
    }

    /**
     * Optional method to process an accusation.
     *
     * @param accuser the name of the agent making the accusation.
     * @param accused the names of the Agents being Accused, concatenated in a String.
     */
    public void get_Accusation(String accuser, String accused) {
    }

    public void debug(String msg) {
        if (DEBUG) {
            System.out.println("~~~DEBUG: " + msg);
        }
    }
}
