package is.ru.gapl.search;

import is.ru.gapl.exception.PlayTimeOverException;
import is.ru.gapl.exception.SearchMethodException;
import is.ru.gapl.strategy.MyExhaustiveSearchStrategy;

import java.util.Random;

import org.eclipse.palamedes.gdl.core.model.IGame;
import org.eclipse.palamedes.gdl.core.model.IGameState;
import org.eclipse.palamedes.gdl.core.model.IMove;
import org.eclipse.palamedes.gdl.core.model.IGameNode;
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

	@Override
	public void init(AbstractStrategy strategy) {
		this.strategy	= (MyExhaustiveSearchStrategy) strategy;
		this.match			= this.strategy.getCurrentMatch();
		this.game			= this.match.getGame();
		this.ownRoleNum		= this.strategy.getOwnRoleNum();
	}
	
	@Override
	public void search(IGameNode currentNode, AbstractStrategy strategy)
			throws SearchMethodException, PlayTimeOverException {
		
		this.generator = new Random(currentNode.getMoves().length);
		//Monte Carlo uses two arrays of values:
		//Q stores the values of the possible actions
		//N stores the number of simulations which have been run for the possible actions
		double[] Q = new double[currentNode.getMoves().length];
		int[] N = new int[currentNode.getMoves().length];
		//Both arrays are initially filled with 0's
		for(int i=0; i<currentNode.getMoves().length; i++) {
			Q[i]=0;
			N[i]=0;
		}
		
		//While there is still time left
		while(!this.strategy.isTimeUp()) {
			//Randomly select a legal move
			int index = generator.nextInt();
			IMove[] action = new IMove[1];
			action[0] = currentNode.getMoves()[index];
			//Generate the next state
			IGameNode nextNode = currentNode;
			try {
				nextNode = this.game.getNextNode(currentNode, action);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				debug("Was unable to calculate the next node.");
			}
			//Run a random simulation starting from the calculated node.
			double score = runSimulation(nextNode);
			//Now update the two arrays. The formula is in the slides of lecture 5.
			Q[index] = (((Q[index]*N[index])+score)/(N[index]+1));
			N[index] = N[index]+1;
		}
		
		//Return the action which has the highest value at the moment.
		IMove returnMove = currentNode.getMoves()[0];
		
		this.strategy.setBestMove(returnMove);
		
		double returnMoveValue = Q[0];
		for(int j=1; j<currentNode.getMoves().length; j++) {
			if(Q[j]>returnMoveValue) {
				returnMove = currentNode.getMoves()[j];
				returnMoveValue = Q[j];
			}
		}
		
		this.strategy.setBestMove(returnMove);
	}
	
	/**
	 * Needs to run a random simulation until terminal state.
	 * The returned value will then be used to calculate the value of the
	 * starting node.
	 * 
	 * @param gameNode
	 * 
	 * @return
	 */
	private double runSimulation(IGameNode gameNode) {
		//If the node is terminal, the return the value.
		if(gameNode.isTerminal()) {
			return evaluateState( gameNode.getState(), this.ownRoleNum );
		}
		else {
			//Copied from the search method.
			//It is supposed to pick a random move and generate
			//the next state, so the method can be run again with
			//this next state.
			this.generator2 = new Random(gameNode.getMoves().length);
			int index = generator2.nextInt();
			IMove[] action = new IMove[1];
			action[0] = gameNode.getMoves()[index];
			//Generate the next state
			IGameNode nextNode = gameNode;
			try {
				nextNode = this.game.getNextNode(gameNode, action);
			} catch (InterruptedException e) {
				debug("Was unable to calculate the next node.");
			}
			return runSimulation(nextNode);
		}
	}
	
	/**
	 * opied this method from the Minimax, because it should
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
