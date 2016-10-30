
/*
* Tahmer Hijjawi
* 21130321
* version v0.2
*/

package s21130321;

import java.util.*;
import java.io.*;

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

public class Ernie21130321 implements cits3001_2016s2.Agent{

  private String name;
  private String players;
  private String spies;
  private String notSpies;
  private char leader;
  private String mishTeam;
  private String votedyay;
  private String votednay;

  private boolean spy;
  private boolean failedLast;

  private Random random;

  private int nextMish;
  private int fails;
  private int votingRound;
  private int numTraitors;

Double [] xs;
  Map spyish;
  Map ff;

  public Ernie21130321(){
    notSpies = "";
    votednay = "";
    failedLast = false;
    votingRound = 0;
    Double [] xsp = {0.886376, 0.4455828, 0.9970207, 0.3436828, 0.8449278,0.3919858, 0.8398001, 0.2361175, 0.9078746, 0.2775158,0.3608134, 0.2737935, 0.08736814, 0.7127835, 0.4166021};
    xs = xsp;
    setupff();
  }

  /**
   * Reports the current status, including players name, the name of all players, the names of the spies (if known), the mission number and the number of failed missions
   * @param name a string consisting of a single letter, the agent"s names.
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

    if(nextMish == 1){
      setupspyish();
    }

    if(spy){
      for(int i = 0; i < players.length(); i++)
      {
        char guy = players.charAt(i);
        if(spies.indexOf(guy) == -1){
          notSpies += guy;
        }
      }
      for(int j = 0; j < spies.length(); j++)
      {
        spyish.put(spies.charAt(j), 1.0);
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
    Random r = new Random();
    if(spy)
    {
      String team = name;
      while(team.length() != number)
      {
        int i = r.nextInt(notSpies.length());
        if(team.indexOf(notSpies.charAt(i))==-1) team += notSpies.charAt(i);
      }
      return team;
    }
    else{
      //get lowest probability of being a spy. and add to team with always include me.
      String team = name;
      List sl = entriesSortedByValues(spyish);
      //System.out.println("sorted list = " + sl);
      for(int i = 0 ; i < number - 1; i++)
      {
        char e = (char)((Map.Entry)sl.get(i)).getKey();
        team += e;
      }
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
    this.leader = leader.charAt(0);;
    this.mishTeam = mission;

    //selectsTeamFeaturingSelf: Frequency of subject selecting teams featuring itself.
    updatespyish(this.leader, ff.get("selectsTeamFeaturingSelf"));
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
    if(leader == name.charAt(0)) return true;

    if(!spy){
      //(Voting) Reject teams of three not featuring self.
      if(players.length() == 5 || (mishTeam.indexOf(name) == -1)) return false;

      //(Voting) Reject missions with known spies on the team.
      // if probability is high to fail the mission false limit determined by trial and error
      double teamrisk = 0.0;
      for(int i = 0 ; i < mishTeam.length() - 1; i++)
      {
        double e = (double)spyish.get(mishTeam.charAt(i));
        teamrisk += e;
      }
      if(teamrisk / (double)mishTeam.length() >= xs[14]) return false;
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

  /**
   * Reports the votes for the previous mission
   * @param yays the names of the agents who voted for the mission
   **/
  public void get_Votes(String yays){
    votedyay = yays;
    for(int i = 0; i < players.length(); i++)
    {
      if(votedyay.indexOf(players.charAt(i)) == -1) votednay += players.charAt(i);
    }
    for(int i = 0; i < players.length(); i++)
    {
      String guy = String.valueOf(players.charAt(i));
      boolean onteam = mishTeam.contains(guy);
      boolean votyay = votedyay.contains(guy);
      //votesForTeamNotFeaturingSelf: Frequency of subject voting for teams not featuring itself.
      if(!onteam && votyay) updatespyish(leader, ff.get("votesForTeamNotFeaturingSelf"));
      //votesForTeamFeaturingSelf: Frequency of subject voting for teams featuring itself.
      if(onteam && votyay) updatespyish(leader, ff.get("votesForTeamFeaturingSelf"));
      //votesAgainstTeamOnFifthAttempt: Frequency of subject voting against teams on the fifth voting attempt
      if(!votyay && votingRound == 5) updatespyish(leader, ff.get("votesAgainstTeamOnFifthAttempt"));
    }
  }

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
      if (fails == 2) return true;
      if (fails == 1 && nextMish == 4) return true;
      if(mishTeam.length() == 2) return false; //dont fail if its only you and another.
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
    numTraitors = traitors;
  }


  /**
   * Optional method to accuse other Agents of being spies.
   * Default action should return the empty String.
   * Convention suggests that this method only return a non-empty string when the accuser is sure that the accused is a spy.
   * Of course convention can be ignored.
   * @return a string containing the name of each accused agent.
   * */
  public String do_Accuse(){
    roundResult();
    return "";
  }

  /**
   * Optional method to process an accusation.
   * @param accuser the name of the agent making the accusation.
   * @param accused the names of the Agents being Accused, concatenated in a String.
   * */
  public void get_Accusation(String accuser, String accused){}

  private int numSpiesOnMish(){
    int numSpiesOnMish = 0;
    for(int i = 0; i < mishTeam.length(); i++)
    {
      if(spies.indexOf(mishTeam.charAt(i)) != -1) numSpiesOnMish++;
    }
    return numSpiesOnMish;
  }

  private void setupspyish(){
    spyish = new HashMap();
    for(int i = 0; i < players.length(); i++)
    {
      //x = spys/players
      spyish.put(players.charAt(i), 0.0);
    }
  }
  private void updatespyish(char p, Object fa){
    double val = (double)spyish.get(p);
    double fff = (double)fa;
    val = val + fff;
    //if(val > 1) val = 1;
    //if(val < 0) val = 0;
    spyish.put(p, val);
  }
  private void roundResult(){
    if(failedLast){
      //who selected it (leader)
      //selectsUnsuccessfulTeam
      updatespyish(leader, ff.get("selectsUnsuccessfulTeam"));
      //who voted for it
      //votesForUnsuccessfulTeam
      for(int i = 0; i < votedyay.length(); i++)
      {
        updatespyish(votedyay.charAt(i), ff.get("votesForUnsuccessfulTeam"));
      }
      //who voted against it
      //votesAgainstUnsuccessfulTeam
      for(int i = 0; i < votednay.length(); i++)
      {
        updatespyish(votednay.charAt(i), ff.get("votesAgainstUnsuccessfulTeam"));
      }
      //who was on the team
      //teamOnIsUnsuccessful
      for(int i = 0; i < mishTeam.length(); i++)
      {
        updatespyish(mishTeam.charAt(i), ff.get("teamOnIsUnsuccessful"));
      }
    } else {
      //who selected it (leader)
      //selectsSuccessfulTeam
      updatespyish(leader, ff.get("selectsSuccessfulTeam"));
      //who voted for it
      //votesForSuccessfulTeam
      for(int i = 0; i < votedyay.length(); i++)
      {
        updatespyish(votedyay.charAt(i), ff.get("votesForSuccessfulTeam"));
      }
      //who voted against it
      //votesAgainstSuccessfulTeam
      for(int i = 0; i < votednay.length(); i++)
      {
        updatespyish(votednay.charAt(i), ff.get("votesAgainstSuccessfulTeam"));
      }
      //who was on the team
      //teamOnIsSuccessful
      for(int i = 0; i < mishTeam.length(); i++)
      {
        updatespyish(mishTeam.charAt(i), ff.get("teamOnIsSuccessful"));
      }
    }
  }
  static <K,V extends Comparable<? super V>> List<Map.Entry<K, V>> entriesSortedByValues(Map<K,V> map) {

      List<Map.Entry<K,V>> sortedEntries = new ArrayList<Map.Entry<K,V>>(map.entrySet());

      Collections.sort(sortedEntries,
              new Comparator<Map.Entry<K,V>>() {
                  @Override
                  public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                      return e1.getValue().compareTo(e2.getValue());
                  }
              }
      );

      return sortedEntries;
  }

  private void setupff(){
    ff = new HashMap();
    Random r = new Random();

    ff.put("selectsSuccessfulTeam", xs[2] * -1.0);
    ff.put("selectsUnsuccessfulTeam", xs[2]);

    ff.put("votesForSuccessfulTeam",xs[4] * -1.0);
    ff.put("votesForUnsuccessfulTeam", xs[4]);

    ff.put("votesAgainstSuccessfulTeam", xs[6] );
    ff.put("votesAgainstUnsuccessfulTeam", xs[6] * -1.0);

    ff.put("teamOnIsUnsuccessful", xs[8] );
    ff.put("teamOnIsSuccessful", xs[8] * -1.0);

    ff.put("votesForTeamFeaturingSelf", xs[11] * -1.0);
    ff.put("votesForTeamNotFeaturingSelf", xs[11]);
    ff.put("votesAgainstTeamOnFifthAttempt", 1.0);

    ff.put("selectsTeamFeaturingSelf", xs[13] * -1.0);

    //ff.put("selectsTeamIHate", x[14]);
    //ff.put("selectsTeamILike", x[15]);
  }

}
