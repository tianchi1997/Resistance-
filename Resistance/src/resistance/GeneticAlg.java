package resistance;

import java.util.*;


/**
 * A java class to run the genetic algorithm. 
 * 
 *  For each new generation, the threshold values are altered according to genetic algorithm policies (done at the end of do_generation), 
 *  and new threshold values are given to agents via the get_thresholds method for the agent. 
 *  
 *   For each game, the win and loss record is updated for each agent, which is stored in a local variable of the do_generation method. 
 *   
 * @author Jelyn Thong, Tianchi Ren
 * 
 *
 */
public class GeneticAlg {

/** 
 *  Global variables
 **/
 GAgent[] agents; 
 double[][] globalthresholds; //global variable holding threshold values for all agents. 
 int finalsurvivor; 
 int numbgens;
 int numbgames; 
 int numbswaps;
 double range;
 Random rand; 
 double bestwinloss; 
 final int numberofagents; //the number of agents to use 
 final double survivalrate;
 /**
  * Constructor for randomised threshold values (30 +- 0.2) +- 0.2 for subsequent alterations. 
  * @param generations number of generations to run the algorithm for.
  * @param games number of games to play per generation
  * @param swaps number of swaps to do for sexual reproduction
  */
 public GeneticAlg(int generations, int games, int swaps) {
	 numberofagents = 100;
	 survivalrate = 0.1; 
	 bestwinloss = 0;
	 rand = new Random(); 
	 agents = new GAgent[numberofagents]; 
	 numbgens = generations;
	 numbgames = games; 
	 numbswaps = swaps;
	 globalthresholds = new double[numberofagents][7];
	 double[] temp = new double[7]; 
	 Arrays.fill(temp, 0.2);
	 generatethresholds(temp,0.2); 
	 range = 0.2; 
	 
 }
 /**
  * A constructor allowing specifications of the range of initial threshold values and also range of modification
  * @param generation generations number of generations to run the algorithm for.
  * @param games number of games to play per generation
  * @param swaps number of swaps to do for sexual reproduction
  * @param initthresholds initial threshold values 
  * @param initrange	initial range for the randomised threshold values
  * @param range	range for each subsequent generation as a percentage of their current value; 
  */
 public GeneticAlg(int generations, int games, int swaps, double[] initthresholds, double initrange, double range){
	 numberofagents = 200;
	 survivalrate = 0.1;
	 bestwinloss = 0; 
	 rand = new Random(); 
	 numbgens = generations;
	 agents = new GAgent[numberofagents]; 
	 numbgames = games; 
	 numbswaps = swaps;
	 globalthresholds = new double[numberofagents][7];
	 generatethresholds(initthresholds,initrange); 
	 this.range = range; 
	 
 }
 /**
  * A method to generate threshold values to be used by this generation of agents. 
  * @param initthresholds initial base threshold values
  * @param initrange the range by which the base threshold values can vary
  */
private void generatethresholds(double[] initthresholds, double initrange){
	for(int i = 0 ;i < globalthresholds.length; i++){
		for(int j = 0; j < globalthresholds[0].length; j++){
			globalthresholds[i][j] = initthresholds[j] - initrange*globalthresholds[i][j] + initrange * 2 * rand.nextDouble()*globalthresholds[i][j]; //initialises a range of initial thresholds based on the given parameters. 
		} 
	}
}
/**
 * A method to generate a new population based on the best survivors from the previous population. 
 * For each of the survivors, new agents(thresholds) will be made to refill the population to the same value. 
 * @param survivors hashset containing the IDs of the agents that survived
 * @param nonsurvivors hashset containing the IDs of the agents that didn't survive
 * @param coolingfactor as the generation progress we want to limit randomness of new child agents. 
 */

private void doReproduce(HashSet<Integer> survivors,HashSet<Integer> nonsurvivors, double coolingfactor ){
	Iterator<Integer> it = nonsurvivors.iterator(); 
	for(Integer ID : survivors){
		for(int i = 1 ; i < (1/survivalrate)-1;i++){
		int nonsurvivor = it.next(); 
		for(int j = 0; j < globalthresholds[0].length; j++){	
		double newval = globalthresholds[ID][j] - coolingfactor*range*globalthresholds[ID][j] + coolingfactor * range * 2 * rand.nextDouble() * globalthresholds[ID][j];
		globalthresholds[nonsurvivor][j] = newval; 
		}
	}
	}
}
/**
 * randomly swaps values of two agents for the number of times specified by the constructor
 */
private void doSwaps(){
	for(int iteration = 0 ; iteration < numbswaps ; iteration++){
		int agent1 = rand.nextInt(numberofagents); 
		int agent2 = rand.nextInt(numberofagents); 
		int parameter = rand.nextInt(7); 
		double temp = globalthresholds[agent1][parameter]; 
		globalthresholds[agent1][parameter] = globalthresholds[agent2][parameter]; 
		globalthresholds[agent2][parameter] = temp; 
	}
}
/**
 * Performs the genetic algorithm by running multiple generations and seeing which agent turns out the best. 
 * @return returns the array of threshold values of the most successful agent. 
 */
public double[] doGeneticAlg(){
	for(int agentnumb = 0; agentnumb < numberofagents; agentnumb++){
		agents[agentnumb] = new GAgent(agentnumb); 
	}
	//creates instances of new agents
	
	int bestofgen = 0; 
for(int i = 0; i < numbgens; i++){
	double coolingfactor =  Math.exp(-i/numbgens); 
			
			
			
	if(i%500 == 0){
		System.out.println("Generation" + i);
		System.out.println("Bestwinloss" + bestwinloss);
		for (int a = 0 ; a < globalthresholds[0].length; a++){
			System.out.println(a + ": " + globalthresholds[finalsurvivor][a]); 
	}
	bestofgen = doGeneration(coolingfactor); 
	}
	finalsurvivor = bestofgen; //the final survivor is the best agent from the last generation played
	
		
}
System.out.println("Best agent was agent" + finalsurvivor);
for (int a = 0 ; a < globalthresholds[0].length; a++){
	System.out.println(globalthresholds[finalsurvivor][a]); 
	
	
}
System.out.println(this.bestwinloss);
return globalthresholds[finalsurvivor]; 
}
/**
 * A method for performing one generation of the algorithm.
 * 
 * @param coolingfactor the cooling factor value for this generation.
 * @return returns the ID of the best agent of the generation. 
 */
private int doGeneration(double coolingfactor){
	double[] wins = new double[numberofagents];
	double[] losses = new double[numberofagents];
	double[] winloss = new double[numberofagents]; 
	Arrays.fill(wins, 0); Arrays.fill(losses, 0); Arrays.fill(winloss, 0);
	
	
	for(int game = 0; game < numbgames; game++){
		boolean resistancewin; 
		Game g = new Game(); 
		
	
		for(int agent = 0; agent < numberofagents; agent++){
			agents[agent].init();
			agents[agent].get_thresholds(globalthresholds[agent]);
								}
		
		
		//this block generates a random number of players between 5 and 10 and adds them to the game. 
		int numberofplayers = rand.nextInt(6) + 5; 
		HashSet<Integer> players = new HashSet<Integer>();
		
		//half the players are expert agents.
		int numberofexperts = numberofplayers/2; 
		for(int e = 0; e < numberofexperts;e++){
			g.addPlayer(new PinguBot());
		}
		for(int n = 0; n < numberofplayers-numberofexperts; n++){
			int p = rand.nextInt(numberofagents); 
			while(players.contains(p)){
				p = rand.nextInt(numberofagents); 
			}
			players.add(p); 
			g.addPlayer(agents[p]);
		}
		g.setup(); 
		resistancewin = g.play();
		
		  
		
	
		// at the end of each game determines whether resistance has won, and updates win/loss record accordingly. 
		if(resistancewin){
			for(int j : players){
				if(agents[j].isspy()){
					losses[j] ++; 
				}
				else wins[j]++; 
			}
		}
		else {
			for(int j : players){
				if(agents[j].isspy()) wins[j]++;
				else losses[j]++; 	
				
			}
		}
	}
	//calculates winloss for every agent
	int bestagent = 0;
	double bestwinloss = 0; 
	for(int wl = 0; wl < numberofagents; wl++){
		winloss[wl] = wins[wl]/(wins[wl]+losses[wl]);
		if(winloss[wl] > bestwinloss) {
			bestwinloss = winloss[wl]; 
			bestagent = wl; 
		}
	}
	this.bestwinloss = bestwinloss; 
	
	//searches through winloss for the top 1/10 agents; 
	HashSet<Integer> survivors = new HashSet<Integer>(); 
	for(int count = 0; count < (int) numberofagents*survivalrate; count++){
		int best = 0; 
		double bestwl = 0;
		for(int i = 0; i < numberofagents; i ++){
			if(winloss[i] > bestwl && !survivors.contains(i)){
				best = i;
				bestwl = winloss[i]; 
			}
		}
		survivors.add(best);
	}
	//fills the hashset of nonsurvivors; 
	HashSet<Integer> nonsurvivors = new HashSet<Integer>(); 
	for(int i = 0; i < numberofagents; i++){
		if(!survivors.contains(i)){
			nonsurvivors.add(i); 
		}
	}
	//generates the new generation of thresholds. 
	doReproduce(survivors,nonsurvivors,coolingfactor);
	doSwaps(); 
	
	return bestagent; 
}
public static void main(String[] args){
	
		
		double[] initialparam = {20,0.4,0.3,0.3,35,50,70};
		GeneticAlg gen = new GeneticAlg(1000,2000,2000,initialparam,0.3,0.1); 
		gen.doGeneticAlg();
	
	
}
}  
