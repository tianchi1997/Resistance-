package resistance;

/**
 * An Expert Rules agent
 * 
 * @author Jelyn Thong, Tianchi Ren
 */

import java.util.*; 
public class PinguBot implements Agent{
	
	//Global variables 
	  private int ID;
	  private String name;
	  private char leader; // the name of the leader of the current proposed mission. 
	  //if an agent is listed under defspy, it will never be nominated, and votes with defspy on the team will be accepted with 10% probability, given that our role is resistance. 
	  private HashSet<Character> defspy; 
	  private boolean onteam; // a boolean indicating whether the agent is on the current proposed mission
	  private String players;
	  private String spies; 
	  private String votes; //stores a string containing the players who voted yes
	  private String team; //the proposed team embarking on the mission. The latest instance will be the actual team going on the mission. 
	  private int numbfailed; // current number of missions failed. 
	  private boolean spy;
	  private Random random;
	  private int missionnumb;  
	  private int votenumb; //the number of votes that have been made this round
	  
	  public PinguBot() {
		  
		 init(); 
		
	  }

	  /** Initialising the global variables of the agent for each new game. 
	   * 
	   */
	  public void init(){
		  random = new Random();
		  numbfailed = 0; 
		  votenumb = 0; 
		  missionnumb = 1; 
		  defspy = new HashSet<Character>(); 
		  onteam = false; 
	  }

	  /**
	   * Reports the current status, including players name, the name of all players, the names of the spies (if known), the mission number and the number of failed missions
	   * @param name a string consisting of a single letter, the agent's names.
	   * @param players a string consisting of one letter for everyone in the game.
	   * @param spies a String consisting of the latter name of each spy, if the agent is a spy, or n questions marks where n is the number of spies allocated; this should be sufficient for the agent to determine if they are a spy or not. 
	   * @param mission the next mission to be launched
	   * @param failures the number of failed missions
	   * @return within 100ms
	   * */
	  public void get_status(String name, String players, String spies, int mission, int failures){
		  this.name = name; 
		  this.players = players; 
		  spy = spies.indexOf(name)!=-1;
		  if(spy){
			  this.spies = spies;
		  }
		  numbfailed = failures; 
		  missionnumb = mission; 
		  
	  }
	  
	  /**
	   * Nominates a group of agents to go on a mission.
	   * If the String does not correspond to a legitimate mission (<i>number</i> of distinct agents, in a String), 
	   * a default nomination of the first <i>number</i> agents (in alphabetical order) will be reported, as if this was what the agent nominated.
	   * @param number the number of agents to be sent on the mission
	   * @return a String containing the names of all the agents in a mission, within 1sec
	   * */
	  public String do_Nominate(int number){
		  /* Spies nominate themselves and then only resistance members so there is 
		  *  no chance of a double betrayal. 
		  */
		  HashSet<Character> team = new HashSet<Character>();
		  team.add(this.name.charAt(0));
		  		
		  		if(spy){
		  			HashSet<Character> spies = new HashSet<Character>(); 
		  			for(int i = 0; i < this.spies.length();i++){
		  				spies.add(this.spies.charAt(i));
		  			}
			      for(int i = 1; i<number; i++){
			      char c = players.charAt(random.nextInt(players.length()));
			      while(team.contains(c)||spies.contains(c)) c = players.charAt(random.nextInt(players.length()));
			      team.add(c);
			      }
		  		}
		  	//Resistance nominates randomly, but always nominate self. 
				  
		  		else
		  		{
		  		for(int i = 1; i<number; i++){
		  			char c = players.charAt(random.nextInt(players.length()));
		  			// we don't want a situation where there are too many players in defspy and we don't have enough agents left to nominate. 
					while(team.contains(c) || (defspy.contains(c) && number >= defspy.size() + players.length())) c = players.charAt(random.nextInt(players.length()));
					team.add(c);
		  		}  	
	  }
		  		String tm = "";
			    for(Character c: team)tm+=c;
			    return tm;
	  }

	  /**
	   * Provides information of a given mission.
	   * @param leader the leader who proposed the mission
	   * @param mission a String containing the names of all the agents in the mission within 1sec
	   **/
	  public void get_ProposedMission(String leader, String mission){
		  this.leader  = leader.charAt(0); 
		  team = mission; 
		  onteam = false; 
		  for(int i = 0;i < mission.length();i++){
			  if(mission.charAt(i) == name.charAt(0)) onteam = true; 
			  
		  }
		  
		  
	  }

	  /**
	   * Gets an agents vote on the last reported mission
	   * @return true, if the agent votes for the mission, false, if they vote against it, within 1 sec
	   * */
	  public boolean do_Vote(){
		  votenumb++;  
		  boolean yes = true;
		  
		  //If it is the first vote of the first mission, vote yes. If it's the fifth round of voting, vote yes. 
		  if((votenumb == 1 && missionnumb == 1) || votenumb == 5) return true;
		  
		  if(name.charAt(0) == leader) return true; 
		  
		  for(int i = 0; i < team.length(); i++){
			  if(name.charAt(0) == team.charAt(i)) onteam = true; 
		  }
		  //If there are three members on the mission and the agent(resistance) is not on the team, vote no. 
		  if(!spy && team.length() == 3 && !onteam) return false;  
		  
		  else if(!spy){
			  boolean containsdefspy = false;
		  for(char c : defspy){
			  for(int i = 0; i < team.length(); i++){
				  if(c == team.charAt(i)) {
					containsdefspy = true;   
				  }
			  }
		  }
		  //if the team contains a defspy, only accept team with 10% probability. 
		  if(containsdefspy == true){
			  Double r = random.nextDouble() + 0.0001 ;
			  yes = r < 0.2; 
		  }
		  }
		  
		  //approve missions with atleast one spy. 
		  if(spy){
		  int numbspies = 0;
		  for(int i = 0; i < spies.length();i++){
			  for(int t = 0; t < team.length();t++){
				  if(spies.charAt(i) == team.charAt(t)) numbspies++; 
			  }
		  }
		  if(numbspies >= 1) return true; 
		  }
		  
		  //If all these tests do not apply, then default vote is yes. 
		  return yes; 
	  }

	  /**
	   * Reports the votes for the previous mission
	   * @param yays the names of the agents who voted for the mission
	   * @return within 100ms
	   **/
	  public void get_Votes(String yays){
		  votes = yays; 
		  if(!spy && votenumb == 5){
			  for(int i = 0; i < votes.length();i++){
				  defspy.add(votes.charAt(i)); 
			  }
		  }
		
	  }

	  /**
	   * Reports the agents being sent on a mission.
	   * Should be able to be infered from tell_ProposedMission and tell_Votes, but incldued for completeness.
	   * @param mission the Agents being sent on a mission
	   * @return within 100ms
	   **/
	  public void get_Mission(String mission){
		  team = mission; 
		  votenumb = 0; 
	  }

	  /**
	   * Agent chooses to betray or not.
	   * @return true if agent betrays, false otherwise, within 1 sec
	   **/
	  public boolean do_Betray(){
		  if(spy){
			  boolean betray = false;
			  
			  if(numbfailed == 2) return true; 
			  
			  
			 // Betray with 80% likelihood if we are the only spy.
			  int numbspies = 0;
			  for(int i = 0; i < spies.length();i++){
				  for(int t = 0; t < team.length();t++){
					  if(spies.charAt(i) == team.charAt(t)) numbspies++; 
				  }
			  }
			  //don't betray if everyone is a spy
			 if(numbspies == team.length()) return false;  
			 
			 //betray if agent is the only spy. 
			 else if(numbspies == 1){
				  betray = 0.8 > random.nextDouble(); 
				  return betray;
			  }
			  // Otherwise betray with 50% likelihood
			  else{
				  return (spy?random.nextInt(2)!=0:false);
			  }
		  }
		  else return false; 
	  }

	  /**
	   * Reports the number of people who betrayed the mission
	   * All the information for the round can be gathered at this step as all steps (other than accusations) have been made. 
	   * Suspicion values are updated here. 
	   * @param traitors the number of people on the mission who chose to betray (0 for success, greater than 0 for failure)
	   * @return within 100ms
	   **/
	  //suspicion usually between 1-10
	  public void get_Traitors(int traitors){
		  
		  
	  }


	  /**
	   * Optional method to accuse other Agents of being spies. 
	   * Default action should return the empty String. 
	   * Convention suggests that this method only return a non-empty string when the accuser is sure that the accused is a spy.
	   * Of course convention can be ignored.
	   * @return a string containing the name of each accused agent, within 1 sec 
	   * */
	  public String do_Accuse(){
		  return ""; 
	  }

	  /**
	   * Optional method to process an accusation.
	   * @param accuser the name of the agent making the accusation.
	   * @param accused the names of the Agents being Accused, concatenated in a String.
	   * @return within 100ms
	   * */
	  public void get_Accusation(String accuser, String accused){
		  

	}
	  /**
	   * Gets the agent's ID
	   * @return returns the ID of the agent
	   */
	  public int getID(){
		  return ID; 
	  }
	  /**
	   * Gets the agent's spy status
	   * @return returns the spy status of the agent as a boolean. 
	   */
	  public boolean isspy(){
		  return spy;
	  }
	 
	 
}
