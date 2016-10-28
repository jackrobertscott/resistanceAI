package s21130321;

import java.util.*;

import cits3001_2016s2.*;

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
public class Ernie21130321 implements cits3001_2016s2.Agent{

  private String name;
  private String players;
  private String spies;
  private String notSpies;
  private String leader;
  private String mishTeam;

  private boolean spy;
  private boolean failedLast;

  private Random random;

  private int nextMish;
  private int fails;
  private int votingRound;





  public Ernie21130321(){
    random = new Random();
    notSpies = "";
    failedLast = false;
    votingRound = 0;
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
    this.nextMish = mission;
    this.players = players;
    this.spies = spies;
    this.fails = failures;
    spy = spies.indexOf(name)!=-1;

    if(spy){
      for(int i = 0; i < players.length(); i++)
      {
        char guy = players.charAt(i);
        if(spies.indexOf(guy) == -1){
          notSpies += guy;
        }
      }
    }
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
      //TODO: randomise the order of the team string
      String team = name;
      team += notSpies.substring(0, number - 1);
      return team;
    }
    else{
      //TODO: get lowest probability of being a spy. and add to team with always include me.
      String team = "";
      team += players.substring(0, number);
      return team;
    }
    //(Selection) Include itself when selecting teams; lowers probability of spy on team
    //(Selection) (Spy) Include one spy when selecting teams (always self).
  }

  /**
   * Provides information of a given mission.
   * @param leader the leader who proposed the mission
   * @param mission a String containing the names of all the agents in the mission
   **/
  public void get_ProposedMission(String leader, String mission){
    votingRound++;
    this.leader = leader;
    this.mishTeam = mission;
  }

  /**
   * Gets an agents vote on the last reported mission
   * @return true, if the agent votes for the mission, false, if they vote against it.
   * */
  public boolean do_Vote(){
    //(Voting) Approve missions on the fifth voting attempt.
    //(Voting) Approve any voting attempt in the first mission.
    //(Voting) Approve missions where self is the leader.
    if(votingRound == 5) return true;
    if(nextMish == 1) return true;
    if(leader == name) return true;

    if(!spy){
      //(Voting) Reject teams of three not featuring self.
      if(players.length() == 5 || (mishTeam.indexOf(name) == -1)) return false;

      //(Voting) Reject missions with known spies on the team.
        //if beyesian probability is high to fail the mission false limit determined by trial and error
      return true;
    }
    else{
      int sOM = numSpiesOnMish();
      //(Voting) (Spy) Approve missions with at least one spy if spies only need one mission to win.
      if(fails == 2 || (sOM > 0)) return true;
      //(Voting) (Spy) Reject missions where the entire team is spies.
      if(sOM != 1) return false;

      return true;
    }
  }

  private int numSpiesOnMish(){
    int numSpiesOnMish = 0;
    for(int i = 0; i < mishTeam.length(); i++)
    {
      if(spies.indexOf(mishTeam.charAt(i)) != -1) numSpiesOnMish++;
    }
    return numSpiesOnMish;
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
  public void get_Mission(String mission){
    votingRound = 0;
  }

  /**
   * Agent chooses to betray or not.
   * @return true if agent betrays, false otherwise
   **/
  public boolean do_Betray(){
    if (!spy) {
      return false; // should only be called if is spy... but just incase
    }
    else{
      int sOM = numSpiesOnMish();
      if(sOM > 2) return false; //more than one spy then let them fail it.
      return true;
    }

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
    return "";
  }

  /**
   * Optional method to process an accusation.
   * @param accuser the name of the agent making the accusation.
   * @param accused the names of the Agents being Accused, concatenated in a String.
   * */
  public void get_Accusation(String accuser, String accused){}

}
