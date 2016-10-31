
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

  private Random random;

  private int nextMish;
  private int fails;
  private int votingRound;
  private int numTraitors;

  private HashMap<String, Double> spyish;

  private ArrayList<String> ncombOnMish;
  private ArrayList<String> ncombOffMish;
  private List sl;
  private boolean DEBUG;

  public ErnieBae(){
    DEBUG = true;
    notSpies = "";
    votednay = "";
    notonTeam = "";
    failedLast = false;
    spy = false;
    votingRound = 0;
    ncombOnMish = new ArrayList<String>();
    ncombOffMish = new ArrayList<String>();
  }
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


    if(nextMish == 1){
      initSpyish();
    }

    if(spy)
    {
      for(int i = 0; i < players.length(); i++)
      {
        char guy = players.charAt(i);
        if(spies.indexOf(guy) == -1){
          notSpies += guy;
        }
      }
      debug(notSpies + spy + " not Spies");
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
      sl = entriesSortedByValues(spyish);
      debug(Arrays.toString(sl.toArray()) + " sorted list");
      String team = name;
      for(int i = 0; i < number - 1; i++)
      {
        String e = (String)((Map.Entry)sl.get(i)).getKey();
        team += e;
      }
      debug(team + " team");
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

      //calculate minimum bae value for team of size.
      sl = entriesSortedByValues(spyish);
      double minbae = 0.0;
      double mishbae = 0.0;

      for(int i = 0; i < mishTeam.length(); i++)
      {
        minbae+=(double)((Map.Entry)sl.get(i)).getValue();
      }
      debug(String.valueOf(minbae) + "MinBae");
      //calculate bae value for this team
      for(int i = 0; i < mishTeam.length(); i++)
      {
        mishbae+=spyish.get(String.valueOf(mishTeam.charAt(i)));
      }
      debug(String.valueOf(mishbae) + "MishBae");
      //decide if its good enough within certain percentage
      if(mishbae > minbae + (minbae / 5)) return false;
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
    debug(votednay + "voted nay");
    //TODO:include votes into calculation.
  }

  /**
   * Reports the agents being sent on a mission.
   * Should be able to be inferred from tell_ProposedMission and tell_Votes, but included for completeness.
   * @param mission the Agents being sent on a mission
   **/
  public void get_Mission(String mission){
    votingRound = 0;
    this.mishTeam = mission;
    notonTeam = "";
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
    updateSpyish();
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
    debug(numSpiesOnMish + " numSpiesOnMish");
    return numSpiesOnMish;
  }

  private void initSpyish(){
    spyish = new HashMap<String, Double>();
    for(int i = 0; i < players.length(); i++)
    {
      String pl = String.valueOf(players.charAt(i));
      spyish.put(pl, (double)spies.length() / (double)players.length());
    }
  }

  private void updateSpyish(){
    double pA, pB, pBa, pAb;
    //update combinations
    ncombOnMish.clear();
    ncombOffMish.clear();
    debug(mishTeam + " " + notonTeam + " mishteam and not team");
    combOnMish(mishTeam, new StringBuffer(), 0, numTraitors);
    combOffMish(notonTeam,new StringBuffer(), 0, (spies.length() - numTraitors));
    for(int i = 0; i < players.length(); i++)
    {
      ArrayList<String> pointless = new ArrayList<String>();
      boolean onMish = false;
      String cur = String.valueOf(players.charAt(i));
      String notcur;
      for(int j = 0; j < mishTeam.length(); j++)
      {
        if(cur.charAt(0) == mishTeam.charAt(j))
        {
          onMish = true;
        }
      }
      if(onMish)
      {
        pA = spyish.get(cur);
        pB = calcpXX(mishTeam, numTraitors, ncombOnMish);
        notcur = mishTeam.replace(cur,"");
        pointless.add(notcur);
        pBa = calcpXX(mishTeam, (numTraitors - 1), pointless);
        pAb = doBae(pA, pB, pBa);
        debug(pA +" "+ pB +" "+ pBa +" "+ pAb +" "+ "pa,pb,pba,pab");
        spyish.put(cur, pAb);
      }
      else
      {
        pA = spyish.get(cur);
        pB = calcpXX(notonTeam, (spies.length() - numTraitors), ncombOffMish);
        notcur = notonTeam.replace(cur,"");
        pointless.add(notcur);
        pBa = calcpXX(notonTeam, ((spies.length() - numTraitors) - 1), pointless);
        pAb = doBae(pA, pB, pBa);
        spyish.put(cur, pAb);
        debug(pA +" "+ pB +" "+ pBa +" "+ pAb +" "+ "pa,pb,pba,pab");
      }
    }
  }

  private double doBae(double pA, double pB, double pBa){
    return (pBa * pA) / pB;
  }

  //adds all the combinations of a string input, with length choose, to an arraylist
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

  private double calcpXX(String mTeam, int numTraitors, ArrayList<String> combinations){
    if(numTraitors == 0)
    {
      return 1.0;
    }
    else{
      double pB = 0.0;
      for(int k = 0; k < combinations.size(); k++)
      {
        String curcomb = combinations.get(k);
        String[] currents = new String[curcomb.length()];
        for(int j = 0; j < curcomb.length(); j++)
        {
          currents[j] = String.valueOf(curcomb.charAt(j));
        }
        double pBk = 1.0;
        for(int m = 0; m < currents.length; m++)
        {
          pBk *= spyish.get(currents[m]);
        }
        for(int i = 0; i < mTeam.length(); i++)
        {
          String cur = String.valueOf(mTeam.charAt(i));
          boolean notincomb = true;
          for(int z = 0; z < currents.length; z++)
          {
            if(cur == currents[z])
            {
              notincomb = false;
            }
          }
          if(notincomb)
          {
            pBk *= (1 - spyish.get(cur));
          }
        }
        pB += pBk;
      }
      return pB;
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
}
