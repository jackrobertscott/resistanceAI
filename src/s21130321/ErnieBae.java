
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

public class ErnieBae implements cits3001_2016s2.Agent{

  private String name;
  private String players;
  private String spies;
  private String notSpies;
  private char leader;
  private String mishTeam;
  private String notonTeam;
  private String votedyay;
  private String votednay;
  private boolean spy;
  private boolean failedLast;
  private int nextMish;
  private int fails;
  private int votingRound;
  private int numTraitors;
  private HashMap<String, Double> spyish;
  private ArrayList<String> ncombOnMish;
  private ArrayList<String> ncombOffMish;
  private List sl;
  private boolean DEBUG;

  /**
  * constructor
  * initialises a few global variables for global use;
  */
  public ErnieBae(){
    DEBUG = false;
    notSpies = "";
    votednay = "";
    notonTeam = "";
    failedLast = false;
    spy = false;
    votingRound = 0;
    ncombOnMish = new ArrayList<String>();
    ncombOffMish = new ArrayList<String>();
  }
  /**
  * prints out debugging messages if debug is enabled
  * @param a is a String for the message
  */
  public void debug(String a){
    if(DEBUG)
    {
      System.out.println("DE~BUG: " + a);
    }

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

    if(spies.indexOf(name) != -1)
    {
      spy = true;
    }
    //if its the first mission and not a spy initilaise the spy probabilities.
    if(nextMish == 1 && !spy){
      initSpyish();
    }
    //if you are a spy fill a string with the other non spy agents
    if(spy && nextMish == 1)
    {
      for(int i = 0; i < players.length(); i++)
      {
        char guy = players.charAt(i);
        if(spies.indexOf(guy) == -1){
          notSpies += guy;
        }
      }
      debug(notSpies + " not Spies");
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


    //if you are a spy then add youself to the team and two random other non spies.
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
    //if you are not the spy then
    //get a sorted list of the spy probabilities
    //and pick the number of lowest entries for the team
    else{
      sl = entriesSortedByValues(spyish);
      debug(Arrays.toString(sl.toArray()) + " sorted list");

      String team = "";
      for(int i = 0; i < number; i++)
      {
        String e = (String)((Map.Entry)sl.get(i)).getKey();
        team += e;
      }
      debug(team + " team");
      return team;
    }
  }

  /**
   * Provides information of a given mission.
   * @param leader the leader who proposed the mission
   * @param mission a String containing the names of all the agents in the mission
   **/
  public void get_ProposedMission(String leader, String mission){
    //increment the vote round
    votingRound++;
    this.leader = leader.charAt(0);;
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
    if(leader == name.charAt(0)) return true;

    if(!spy){
      //(Voting) Reject teams of three not featuring self.
      if(players.length() == 5 || (mishTeam.indexOf(name) == -1)) return false;

      //(Voting) Reject missions with known spies on the team.

      //get that sorted list
      sl = entriesSortedByValues(spyish);
      double minbae = 0.0;
      double mishbae = 0.0;

      //calculate minimum bay value for team of size.
      for(int i = 0; i < mishTeam.length(); i++)
      {
        minbae+=(double)((Map.Entry)sl.get(i)).getValue();
      }
      debug(String.valueOf(minbae) + "MinBae");

      //calculate bay value for this team
      for(int i = 0; i < mishTeam.length(); i++)
      {
        mishbae+=spyish.get(String.valueOf(mishTeam.charAt(i)));
      }
      debug(String.valueOf(mishbae) + "MishBae");

      //decide if its good enough within certain percentage
      if(mishbae > minbae + (minbae / 10)) return false;
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
    //get all the people who voted nay and yay and put them into separate strings
    votedyay = yays;
    votednay = "";
    for(int i = 0; i < players.length(); i++)
    {
      if(votedyay.indexOf(players.charAt(i)) == -1) votednay += players.charAt(i);

    }
    debug(votednay + "voted nay");
    //TODO:include votes into calculation.
  }

  /**
   * Reports the agents being sent on a mission.
   * Should be able to be inferred from tell_ProposedMission and tell_Votes, but included for completeness.
   * @param mission the Agents being sent on a mission
   **/
  public void get_Mission(String mission){
    //add people on mission to String
    votingRound = 0;
    this.mishTeam = mission;
    notonTeam = "";
    //add people not on mission to string
    for(int i = 0; i < players.length(); i++)
    {
      if(!mishTeam.contains(String.valueOf(players.charAt(i))))
      {
        notonTeam += players.charAt(i);
      }
    }
    debug(notonTeam + " not on team");
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
      if (fails == 2) return true; //if there are two failed missions then betray
      if (fails == 1 && nextMish == 4) return true; //if you need to fail the last two missions then betray
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
    if(!spy)
    {
      //at the end of a round we update the spyish probabilities
      updateSpyish();
    }
    return "";
  }

  /**
   * Optional method to process an accusation.
   * @param accuser the name of the agent making the accusation.
   * @param accused the names of the Agents being Accused, concatenated in a String.
   * */
  public void get_Accusation(String accuser, String accused){}

  /**
  * method that calculates the number of spies that are on a mission.
  * @return the number of spies
  * */
  private int numSpiesOnMish(){
    int numSpiesOnMish = 0;
    for(int i = 0; i < mishTeam.length(); i++)
    {
      if(spies.indexOf(mishTeam.charAt(i)) != -1) numSpiesOnMish++;
    }
    debug(numSpiesOnMish + " numSpiesOnMish");
    return numSpiesOnMish;
  }
  /**
  * this initilaises the probabilities that each other agent is a spyish
  * not including yourself there is a numspies/numplayers chance anyone is a spy
  * sets your own probability to 0.0 becasue we arent a spy
  **/
  private void initSpyish(){
    spyish = new HashMap<String, Double>();
    for(int i = 0; i < players.length(); i++)
    {
      String pl = String.valueOf(players.charAt(i));
      spyish.put(pl, (double)spies.length() / ((double)players.length() - 1));
    }
    spyish.put(name, 0.0);
  }

  /**
  * this is the main method that runs the bayesian statistics.
  * it updates the probability of each Agent being a spyish
  **/

  private void updateSpyish(){
    double pA, pB, pBa, pAb;
    //clear combinations

    ncombOnMish.clear();
    ncombOffMish.clear();

    debug(mishTeam + " " + notonTeam + " mishteam and not team");

    //need these combinations to calculate p(B)the probability of the mission outcome given the specific team
    combOnMish(mishTeam, new StringBuffer(), 0, numTraitors);
    combOffMish(notonTeam,new StringBuffer(), 0, (spies.length() - numTraitors));

    //for each player we need to caluclate P(A|B) this is the probability they are a spy given the last mission.
    for(int i = 0; i < players.length(); i++)
    {
      //poinless array so that i can use the same function. for calculating pBa
      ArrayList<String> pointless = new ArrayList<String>();
      String cur = String.valueOf(players.charAt(i));
      //was the current player on the last mission
      boolean onMish = (mishTeam.contains(cur));
      String notcur;
      //get the current agents prior probability
      pA = spyish.get(cur);

      if(onMish)
      {
        //get the people on the mission that are not the current agent
        notcur = mishTeam.replace(cur,"");
        pointless.add(notcur);
        //calculate pBa and pB
        pBa = calcpXX(mishTeam, pointless);
        pB = calcpXX(mishTeam, ncombOnMish);
      }
      else
      {
        //get the people on the mission that is not the current agent
        notcur = notonTeam.replace(cur,"");
        pointless.add(notcur);
        //calculate pBa and pB
        pB = calcpXX(notonTeam, pointless);
        pBa = calcpXX(notonTeam, pointless);
      }
      //this just applies bays rule for the current agent
      pAb = doBae(pA, pB, pBa);
      debug(spyish.toString() + " spyish");
      //update its probability
      spyish.put(cur, pAb);
    }
  }

  /**
  * does Bayes rule
  * @param pA is the probability A is a spy
  * @param pB the probability of getting a mission outcome with a team
  * @param pBa the probability of getting the mission outcome assuming A is a spy
  * @return pAb the probability a is a spy given the mission
  */
  private double doBae(double pA, double pB, double pBa){
    if (pB == 0.0) return 0.0;
    return (pBa * pA) / pB;
  }

  /*
  * calculates all the combinations of an input string that have length == choose
  * this is an NP+Hard problem but it shoudl be fine as the string inputs are small
  * @param the input string to pull the combinations for
  * @param x space to build the string
  * @param index where to continue from with the recursive call just call the function with 0
  * @param choose the length of the combinations you want outputted.
  */
  private void combOnMish(String input, StringBuffer x, int index, int choose)
  {

    for(int i = index; i < input.length(); i++)
    {
      x.append(input.charAt(i));
      if(x.length() == choose)
      {
        ncombOnMish.add(x.toString());
        debug(x.toString() + " combon");
      }
      combOnMish(input, x, i + 1, choose);
      x.deleteCharAt(x.length() - 1);
    }
  }
  //easier to have two functions that do the same thing to update the global variables.. becasue it was weird with recursiveness
  private void combOffMish(String input, StringBuffer y, int index, int choose)
  {
    for(int i = index; i < input.length(); i++)
    {
      y.append(input.charAt(i));
      if(y.length() == choose)
      {
        ncombOffMish.add(y.toString());
        debug(y.toString() + " comboff");
      }
      combOffMish(input, y, i + 1, choose);
      y.deleteCharAt(y.length() - 1);
    }
  }

  /*
  * used to calculate both pB and pBa
  * @param String that is the Team on the mission
  * @param all the combinations that could have been spies on this mission.
  * @return the probability
  */
  private double calcpXX(String mTeam, ArrayList<String> combinations){
    double pB = 0.0;
    //for each combination.
    for(int i = 0; i < combinations.size(); i++)
    {
      pB = 0.0;
      String current = combinations.get(i);
      double pbk = 1.0;
      String inTeamnotCurrent = "";
      //get the agents that were in the team and are not in the current combination
      for(int j = 0; j < mTeam.length(); j++)
      {
        if(!current.contains(String.valueOf(mTeam.charAt(j))))
        {
          inTeamnotCurrent += mTeam.charAt(j);
        }
      }
      //multiply the probability that all the agents we asssme to be spies, actually are spies
      for(int j = 0; j < current.length(); j++)
      {
        pbk *= spyish.get(String.valueOf(current.charAt(j)));
      }
      //multiple the probablilty that the agents we assume arent spies actually arent spuies
      for(int j = 0; j < inTeamnotCurrent.length(); j++)
      {
        pbk *= (1 - spyish.get(String.valueOf(inTeamnotCurrent.charAt(j))));
      }
      //sum all the combinations
      pB += pbk;
    }
    if(pB == 0.0) pB = 1.0;
    return (double)pB;
  }

  /*
  * a custom sorting to order a map by lowest value.
  * @param map is the input map containing the key value pairs to be sortedEntries
  * @retrun returns a list of the map entries sorted by lowst value
  */
  private  List<Map.Entry<String, Double>> entriesSortedByValues(Map<String,Double> map) {
      List<Map.Entry<String,Double>> sortedEntries = new ArrayList<Map.Entry<String,Double>>(map.entrySet());
      Collections.sort(sortedEntries,
        new Comparator<Map.Entry<String,Double>>() {
          @Override
          public int compare(Map.Entry<String,Double> e1, Map.Entry<String,Double> e2) {
            return e1.getValue().compareTo(e2.getValue());
          }
        }
      );
      return sortedEntries;
  }
}
