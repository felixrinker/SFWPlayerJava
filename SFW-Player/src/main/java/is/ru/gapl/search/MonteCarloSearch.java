package is.ru.gapl.search;

import is.ru.gapl.exception.PlayTimeOverException;
import is.ru.gapl.exception.SearchMethodException;
import is.ru.gapl.strategy.MyExhaustiveSearchStrategy;

import java.util.Random;

import org.eclipse.palamedes.gdl.core.model.IGame;
import org.eclipse.palamedes.gdl.core.model.IGameState;
import org.eclipse.palamedes.gdl.core.model.IMove;
import org.eclipse.palamedes.gdl.core.model.IGameNode;
import org.eclipse.palamedes.gdl.core.model.IReasoner;
import org.eclipse.palamedes.gdl.core.simulation.Match;
import org.eclipse.palamedes.gdl.core.simulation.strategies.AbstractStrategy;


/**
 * This is an attempt at implementing the Monte Carlo Search
 * using the descriptions from lecture 5.
 * 
 * What still needs to be done:
 * - Debugging!!
 * - Correctly integrating it in the strategy class
 * 	(I tried, but honestly wasn't sure how to go about it.)
 */

public class MonteCarloSearch implements ISearch {
	private MyExhaustiveSearchStrategy strategy;
	private IGame game;
	private Match match;
	private int ownRoleNum;
	
	private Random generator;
	private Random generator2;
	private boolean debug = true;
	private IReasoner reasoner;
	private IGameState gameState;
	private String ownRoleName;

	@Override
	public void init(AbstractStrategy strategy) {
		this.strategy	= (MyExhaustiveSearchStrategy) strategy;
		this.match			= this.strategy.getCurrentMatch();
		this.game			= this.match.getGame();
		this.ownRoleNum		= this.strategy.getOwnRoleNum();
		this.ownRoleName	= this.strategy.getOwnRoleName();
		this.reasoner		= this.strategy.getReasoner();
		
		this.generator = new Random();
	}
	
	@Override
	public void search(IGameNode currentNode, AbstractStrategy strategy)
			throws SearchMethodException, PlayTimeOverException {
		
		
		
		
			 MonteCarlo(currentNode);
		
	}
	
	private void MonteCarlo(IGameNode currentNode) throws PlayTimeOverException {
		// Retrieves all legalMoves for own role
		// from the current Node
		IMove[] legalMoves = null;
		try {
			legalMoves	= game.getLegalMoves(currentNode)[this.ownRoleNum];
		} catch (InterruptedException e1) {
				debug("Was unable to retrieve the legal moves.");
		}

		// Monte Carlo uses two arrays of values:
		// Q stores the values of the possible actions
		// N stores the number of simulations which have been run for the possible actions
		double[] Q	= new double[legalMoves.length];
		int[]  N	= new int[legalMoves.length];
				
		//Both arrays are initially filled with 0's
		for(int i=0; i<legalMoves.length; i++) {
			Q[i]=0;
			N[i]=0;
		}
				
		double returnMoveValue	= Q[0];
		
		//While there is still time left
		while(!this.strategy.isTimeUp()) {
			//Randomly select a legal move
			int index = generator.nextInt(legalMoves.length);
			IMove[] action = new IMove[1];
			action[0] = legalMoves[index];
					
			int ind = action[0].toString().indexOf("noop");
			if( ind == -1 ) {	
				//Generate the next state
				IGameNode nextNode = null;
				try {
					nextNode = this.game.getNextNode(currentNode, action);
				} catch (InterruptedException e) {
						debug("Was unable to calculate the next node.");
				}
				
				//Run a random simulation starting from the calculated node.
				double score = runSimulation(nextNode);
				//Now update the two arrays. The formula is in the slides of lecture 5.
				Q[index] = (((Q[index]*N[index])+score)/(N[index]+1));
				N[index] = N[index]+1;
					
				returnMoveValue = 0.0;
				//Return the action which has the highest value at the moment.
				for(int j = 0; j < legalMoves.length; j++) {
					if(Q[j]>returnMoveValue) {
						this.strategy.setBestMove(legalMoves[j]);
						returnMoveValue = Q[j];
					}
				}
					
				debug("run simulation: "+N[index]+" for action: "+action[0]+" generated AVG score: "+Q[index]);
			}	
		}
	}
	
	
	/**
	 * Needs to run a random simulation until terminal state.
	 * The returned value will then be used to calculate the value of the
	 * starting node.
	 * 
	 * @param gameNode
	 * 
	 * @return
	 * @throws PlayTimeOverException 
	 */
	private double runSimulation(IGameNode gameNode) throws PlayTimeOverException {
		
		this.strategy.checkTime();
		//If the node is terminal, the return the value.
		if(gameNode.isTerminal()) {
			int score = 0;
			try {
				score = this.reasoner.getGoalValue(ownRoleName, gameNode.getState());
			} catch (InterruptedException e) {
				System.out.println("Error calculating the goal value");
			}
			
			return score;
		//	return evaluateState( gameNode.getState(), this.ownRoleNum );
		}
		else {
			//Copied from the search method.
			//It is supposed to pick a random move and generate
			//the next state, so the method can be run again with
			//this next state.
			IMove[] legalMoves = null;
			try {
				legalMoves = game.getLegalMoves(gameNode)[this.ownRoleNum];
			} catch (InterruptedException e1) {
				System.out.println("Was unable to retrieve the legal moves.");
			}
			
			int index = generator.nextInt(legalMoves.length);
			IMove[] action = new IMove[1];
			action[0] = legalMoves[index];
			//Generate the next state
			IGameNode nextNode = null;
			try {
				nextNode = this.game.getNextNode(gameNode, action);
			} catch (InterruptedException e) {
				System.out.println("Was unable to calculate the next node.");
			}
			return runSimulation(nextNode);
		}
	}
	
	/**
	 * Copied this method from the Minimax, because it should
	 * hopefully be the same.
	 * 
	 * @param gameState
	 * @param roleNum
	 * @return
	 */
	private double evaluateState( IGameState gameState, int roleNum ) {
		
		if(gameState.isTerminal()) {
			
			double sumOponent	= 0;
			double ourGoalValue	= 0;
			int[] goalValues	= gameState.getGoalValues();
			
			for( int rCt=0; rCt < goalValues.length; rCt++ ) {
				if( roleNum == rCt ) {
					// set out goal value
					ourGoalValue = goalValues[rCt]; 	
				}else {
					// set opponent goal value
					sumOponent += goalValues[rCt];
				}
			}
			return ourGoalValue - sumOponent;
			
		}else{
			
			return (-100/game.getRoleCount());
		}
	}
	
	
	/**
	 * Prints the debug messages
	 * 
	 * @param txt
	 */
	private void debug(String txt) {
		
		if(debug) {
			
			System.out.println("DEBUG: "+txt);
		}
	}
}
