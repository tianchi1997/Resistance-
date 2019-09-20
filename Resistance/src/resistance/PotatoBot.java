package resistance;

/**
 * The final Genetic agent with a fixed set of thresholds obtained by running the
 * genetic algorithm
 * 
 * @author Tianchi Ren, Jelyn Thong
 */
import java.util.*; 
public class PotatoBot implements Agent{
	
	//Global variables 
	  
	  private String name;
	  private String players;
	  private String spies; 
	  private HashMap<Character,Double> suspicion; // maps the player to their suspicion level. 
	  private String votes; //stores a string containing the players who voted yes
	  private String team; //the proposed team embarking on the mission. The latest instance will be the actual team going on the mission. 
	  private int numbfailed; // current number of missions failed. 
	  private boolean spy;
	  private Random random;
	  private int missionnumb;  
	  private int votenumb; //the number of votes that have been made this round
	  
	  /*
	   * An array holding all the threshold values of the agent. 
	   *  Indices: 
	   *  Suspicion
	   *  	0 : suspicion constant
	   * 	 1 : weighting 1
	   * 	 2 : weighting 2
	   *  	3 : weighting 3
	   *  Voting
	   *  	4 : Vote threshold spy
	   *  	5 : Vote threshold resistance
	   *  Betray
	   *  	6 : Betray threshold
	   *  
	   *  
	   */
	  private double[] thresholds; 
	  
	  public PotatoBot() {
		  double[] t =  {16.514,0.3537,0.3274,0.2776,38.449,67.295,79.52};
		  thresholds = t;
		 init(); 
		
	  }
	
	  /** Initialising the global variables of the agent for each new game. 
	   * 
	   */
	  public void init()
	  {
		  random = new Random();
		  suspicion = new HashMap<Character,Double>(); 
		  numbfailed = 0; 
		  votenumb = 0; 
		  
		  
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
		  for(int i = 0; i < players.length();i++){
				if(!suspicion.containsKey(players.charAt(i))) 
						suspicion.put((Character) players.charAt(i), (double) 0);
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
		  // If it is a spy then randomly nominate, but always nominate self. 
		  HashSet<Character> team = new HashSet<Character>();
		  team.add(this.name.charAt(0));
		  if(this.spy){
			      for(int i = 1; i<number; i++){
			      char c = players.charAt(random.nextInt(players.length()));
			      while(team.contains(c)) c = players.charAt(random.nextInt(players.length()));
			      team.add(c);
			    }
			    
			  
		  }
		  // picks members with lowest suspicion. 
		  else {
			 for(int i = 1; i < number; i++){
				 double lowest = Integer.MAX_VALUE;
				 char ch = ' ';
				 for(int j = 0; j < players.length(); j++ ){
					 if((suspicion.get(players.charAt(j)) < lowest) && !team.contains(players.charAt(j))){
						 ch = players.charAt(j); 
						 lowest = suspicion.get(ch);
					 }
				 }

				 team.add(ch); 
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
		  team = mission; 
	  }

	  /**
	   * Gets an agents vote on the last reported mission
	   * @return true, if the agent votes for the mission, false, if they vote against it, within 1 sec
	   * */
	  public boolean do_Vote(){
		  votenumb++;  
		  boolean yes; 
		  double value = 0; 
		  //voting calculation for spy
		  if(spy){
		  int numberofspies = 0; 
		  for(int i = 0; i < team.length();i++){
			  String s = Character.toString(team.charAt(i)); 
			  if(spies.contains(s)) numberofspies++;  
			 	}
		  if(numberofspies > 0) return true; //always vote yes if there is a spy on the team.
		  
		  value = ( (suspicion.get(this.name.charAt(0))))/ thresholds[4]; //the lower the spy's suspicion the more likely the spy is to vote no. 
		 
		  yes = value/(random.nextDouble()+ 0.00001) > 1; 
		  
		  
		  }
		  //voting calculation for resistance
		  else {
			  double totalsuspicion = 0;
			  for(int i = 0; i < team.length(); i++){
				  totalsuspicion += suspicion.get(team.charAt(i));
			  }
			  value = (totalsuspicion/(team.length()*thresholds[5]));
			  yes = value/(random.nextDouble() + 0.00001) < 1; //simulates voting yes with a probability of totalsuspicion/(team.length()*thresholds[5])
			  
		  }
		  
		  if(votenumb == 1 && missionnumb == 1 || votenumb == 5) yes = true; 
		  return yes; 
	  }

	  /**
	   * Reports the votes for the previous mission
	   * @param yays the names of the agents who voted for the mission
	   * @return within 100ms
	   **/
	  public void get_Votes(String yays){
		  votes = yays; 
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
		  double value = 0; 
		  value = (suspicion.get(this.name.charAt(0)) + numbfailed - (team.length()/3))/thresholds[6]; 
		  if(numbfailed == 2) return true; 
		  return value/(random.nextDouble() + 0.00001) < 1; 
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
		  if(traitors > 0){
			  for(int i = 0; i < team.length(); i++){
				  //updating new suspicion values for condition: Person on a failed mission
				  double currentsus = suspicion.get(team.charAt(i));
				  double newsus = currentsus + (thresholds[0]*thresholds[1]/team.length());
				  suspicion.put(team.charAt(i), newsus); 
				  
			  }
			  for(int j = 0; j < votes.length(); j++){
				  //updating new suspicion values for condition: voted yes for a failed mission 
				  double currentsus = suspicion.get(votes.charAt(j)); 
				  double newsus = currentsus + thresholds[0] * thresholds[2]; 
				  suspicion.put(votes.charAt(j), newsus); 
				 
			  }
		  }
		  else {
			  for(int k = 0; k < players.length(); k++){
				  //updating new suspicion values for condition: Person voted no for a successful mission
				  String p = Character.toString(players.charAt(k)); 
				  if(!votes.contains(p)){
					  double currentsus = suspicion.get(players.charAt(k)); 
					  double newsus = currentsus + thresholds[0] * thresholds[3]; 
					  suspicion.put(players.charAt(k), newsus); 
				  }
			  }
		  
		  	}
		  
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
	
	public boolean isspy(){
		return spy;
	}
	 
}
