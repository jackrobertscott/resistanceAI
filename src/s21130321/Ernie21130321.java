package cits3001_2016s2;

import java.util.*;

/**
 * A Java class for an agent to play in Resistance.
 * Each agent is given a single capital letter, which will be their name for the game.
 * The game actions will be encoded using strings.
 * The agent will be created entirely in a single game, and the agent must maintain its own state.
 * Methods will be used for informing agents of game events (get_ methods, must return in 100ms) or requiring actions (do_ methods, must return in 1000ms).
 * If actions do not meet the required specification, a nominated default action will be recorded.
 * @author Tim French
 * **/

/*
* Tahmer Hijjawi
* 21130321
*/
public class Ernie21130321 implements Agent{

  private String name;
  private String players;
  private String spies;
  private String notSpies;

  private boolean spy;
  private Random random;
  private int numPlayers;
  private int nextMish;
  private int fails;
  private boolean failedLast;

  public Ernie21130321(){
    random = new Random();
    notSpies = "";
  }

  /**
   * Reports the current status, including players name, the name of all players, the names of the spies (if known), the mission number and the number of failed missions
   * @param name a string consisting of a single letter, the agent's names.
   * @param players a string consisting of one letter for everyone in the game.
   * @param spies a String consisting of the latter name of each spy, if the agent is a spy, or n questions marks where n is the number of spies allocated; this should be sufficient for the agent to determine if they are a spy or not.
   * @param mission the next mission to be launched
   * @param failures the number of failed missions
   * */
  public void get_status(String name, String players, String spies, int mission, int failures){
    this.name = name;
    this.numPlayers = players.length();
    this.players = players;
    this.spies = spies;
    spy = spies.indexOf(name)!=-1;

    if(spy)
    {
      for(int i = 0; i < numPlayers; i++)
      {
        char guy = players.charAt(i);
        if(spies.indexOf(guy) == -1){
          notSpies += guy;
        }
      }
    }

    this.nextMish = mission;
    this.fails = failures;
  }

  /**
   * Nominates a group of agents to go on a mission.
   * If the String does not correspond to a legitimate mission (<i>number</i> of distinct agents, in a String),
   * a default nomination of the first <i>number</i> agents (in alphabetical order) will be used, as if this was what the agent nominated.
   * @param number the number of agents to be sent on the mission
   * @return a String containing the names of all the agents in a mission
   * */
  public String do_Nominate(int number){
    if(spy)
    {
      String team = name;
      team += notSpies.substring(0, number - 1);
    }
    else{

    }
    //(Selection) Include itself when selecting teams; lowers probability of spy on team
    //(Selection) (Spy) Include one spy when selecting teams (always self).
  }

  /**
   * Provides information of a given mission.
   * @param leader the leader who proposed the mission
   * @param mission a String containing the names of all the agents in the mission
   **/
  public void get_ProposedMission(String leader, String mission){}

  /**
   * Gets an agents vote on the last reported mission
   * @return true, if the agent votes for the mission, false, if they vote against it.
   * */
  public boolean do_Vote(){
    return (random.nextInt(2)!=0);
    //(Voting) Approve missions on the fifth voting attempt.
    //(Voting) Approve any voting attempt in the first mission.
    //(Voting) Approve missions where self is the leader.
    //(Voting) Reject teams of three not featuring self.
    //(Voting) Reject missions with known spies on the team.

    //(Voting) (Spy) Approve missions with at least one spy if spies only need one mission to win.
    //(Voting) (Spy) Reject missions where the entire team is spies.
    //(Voting) (Spy) Reject missions with zero or both spies on the team.
    //||
    //(Voting) (Spy) Approve missions with at least one spy on the team.

  }

  /**
   * Reports the votes for the previous mission
   * @param yays the names of the agents who voted for the mission
   **/
  public void get_Votes(String yays){}

  /**
   * Reports the agents being sent on a mission.
   * Should be able to be inferred from tell_ProposedMission and tell_Votes, but included for completeness.
   * @param mission the Agents being sent on a mission
   **/
  public void get_Mission(String mission){}

  /**
   * Agent chooses to betray or not.
   * @return true if agent betrays, false otherwise
   **/
  public boolean do_Betray(){
    return (spy?random.nextInt(2)!=0:false);
  }

  /**
   * Reports the number of people who betrayed the mission
   * @param traitors the number of people on the mission who chose to betray (0 for success, greater than 0 for failure)
   **/
  public void get_Traitors(int traitors){
    if(traitors != 0){
      failedLast = true;
    }
  }


  /**
   * Optional method to accuse other Agents of being spies.
   * Default action should return the empty String.
   * Convention suggests that this method only return a non-empty string when the accuser is sure that the accused is a spy.
   * Of course convention can be ignored.
   * @return a string containing the name of each accused agent.
   * */
  public String do_Accuse(){
    int number = random.nextInt(players.length());
    HashSet<Character> team = new HashSet<Character>();
    for(int i = 0; i<number; i++){
      char c = players.charAt(random.nextInt(players.length()));
      while(team.contains(c)) c = players.charAt(random.nextInt(players.length()));
      team.add(c);
    }
    String tm = "";
    for(Character c: team)tm+=c;
    return tm;
  }

  /**
   * Optional method to process an accusation.
   * @param accuser the name of the agent making the accusation.
   * @param accused the names of the Agents being Accused, concatenated in a String.
   * */
  public void get_Accusation(String accuser, String accused){}

}
