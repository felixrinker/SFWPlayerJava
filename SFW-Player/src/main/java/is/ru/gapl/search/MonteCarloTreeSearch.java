package is.ru.gapl.search;

import static org.apache.commons.collections.map.AbstractReferenceMap.SOFT;
import is.ru.gapl.exception.PlayTimeOverException;
import is.ru.gapl.exception.SearchMethodException;
import is.ru.gapl.model.StateActionPair;
import is.ru.gapl.model.StateMCTS;
import is.ru.gapl.strategy.MyExhaustiveSearchStrategy;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.collections.map.ReferenceMap;
import org.eclipse.palamedes.gdl.core.model.IGameNode;
import org.eclipse.palamedes.gdl.core.model.IGameState;
import org.eclipse.palamedes.gdl.core.model.IMove;
import org.eclipse.palamedes.gdl.core.model.IReasoner;
import org.eclipse.palamedes.gdl.core.simulation.strategies.AbstractStrategy;


/**
 * This is an attempt at implementing the Monte Carlo Search Tree
 * using the descriptions from lecture 5.
 * 
 */

public class MonteCarloTreeSearch implements ISearch {
	private MyExhaustiveSearchStrategy strategy;

	
	private Random generator;
	private boolean debug = true;
	private IReasoner reasoner;
	private String ownRoleName;
	private ReferenceMap stateTree;
	private ArrayList<StateActionPair> backTrackList;

	@Override
	public void init(AbstractStrategy strategy) {
		this.strategy	= (MyExhaustiveSearchStrategy) strategy;
		this.ownRoleName	= this.strategy.getOwnRoleName();
		this.reasoner		= this.strategy.getReasoner();
		
		this.generator = new Random();
		
		this.stateTree = new ReferenceMap(SOFT, SOFT);
	}
	
	@Override
	public void search(IGameNode currentNode, AbstractStrategy strategy)
			throws SearchMethodException, PlayTimeOverException {
		
		
		this.backTrackList = new ArrayList<StateActionPair>();
		
		this.MonteCarloTree(currentNode.getState());
	}
	
	private void MonteCarloTree(IGameState currentGameState) throws PlayTimeOverException {
		
		// if gameState is still in tree
		if(stateTree.containsKey(currentGameState)) {
			// get the state value
			StateMCTS stateMCTS = (StateMCTS) this.stateTree.get(currentGameState);
			IMove[] action = new IMove[1];
			// pick the action with the highest UCT value
			action[0] = stateMCTS.getActionWithHighesUCT();
			
			try {
				IGameState nextState = this.reasoner.getNextState(currentGameState, action);
				
				// push gameState on backtrace stack
				this.backTrackList.add(new StateActionPair(nextState, action));
				
				// call next iteration
				MonteCarloTree(nextState);
				
			} catch (InterruptedException e) {
				debug("Was unable to calculate the next state.");
			}
		}else {
			
			try {
				
				// Expand: add all direct successors of s to the tree
				IMove[] legalActions = this.reasoner.getLegalMoves(ownRoleName, currentGameState);
				this.stateTree.put(currentGameState, new StateMCTS(legalActions));
				
				// playout	
				this.playout(currentGameState, legalActions);
				
				
			} catch (InterruptedException e) {
				debug("Was unable to retrieve the legal moves.");
			}
			
		}
	}
	
	
	private void playout(IGameState gameState, IMove[] legalMoves) throws PlayTimeOverException {
		
		//While there is still time left
		while(!this.strategy.isTimeUp()) {
			//Randomly select a legal move
			int index = generator.nextInt(legalMoves.length);
			IMove[] action = new IMove[1];
			action[0] = legalMoves[index];
			
			
			if(action[0].toString().indexOf(""+this.ownRoleName) == -1) {
				debug("oponent turn");
				continue;
			}
			
			int ind1 = action[0].toString().indexOf("noop");
			
			if( ind1 == -1) {	
				//Generate the next state
				IGameState nextNode = null;
				try {
					nextNode = this.reasoner.getNextState(gameState, action);
				} catch (InterruptedException e) {
						debug("Was unable to calculate the next node.");
				}
						
				//Run a random simulation starting from the calculated node.
				int score = runPlayout(nextNode);
				
				// update parent state
				this.update(gameState, action[0], score);
				this.strategy.setBestMove(action[0]);
				
				if(!backTrackList.isEmpty())
				{//Now update all other states
					for(int i = backTrackList.size()-1; i >= 0; i--) {
						StateActionPair sap = this.backTrackList.get(i);
						this.update(sap.gameState, sap.action[0], score);
						if(i == 0 && -1 < sap.action[0].toString().indexOf("noop")) {
							this.strategy.setBestMove(sap.action[0]);
						}
					}
				}				
			//	debug("run simulation: "+N[index]+" for action: "+action[0]+" generated AVG score: "+Q[index]);
			}	
	}
	
	}
	private void update(IGameState gameState, IMove action, int score) {
		StateMCTS stateMCTS = (StateMCTS) this.stateTree.get(gameState);
		// Number of simulation with action
		stateMCTS.increaseNumberSimulationsForAction(action);
		// Number of simulation with state
		stateMCTS.increaseNumberSimulations();
		// Average score of action
		stateMCTS.updateAvgScoreForAction(action, score);
	}
	
	private int runPlayout(IGameState gameState) throws PlayTimeOverException {
		
		this.strategy.checkTime();
		//If the node is terminal, the return the value.
		if(gameState.isTerminal()) {
			int score = 0;
			try {
				score = this.reasoner.getGoalValue(ownRoleName, gameState);
			} catch (InterruptedException e) {
				System.out.println("Error calculating the goal value");
			}
			
			return score;
		}
		else {
			//Copied from the search method.
			//It is supposed to pick a random move and generate
			//the next state, so the method can be run again with
			//this next state.
			IMove[] legalMoves = null;
			try {
				legalMoves = this.reasoner.getLegalMoves(ownRoleName, gameState);
			} catch (InterruptedException e1) {
				System.out.println("Was unable to retrieve the legal moves.");
			}
			/*if(legalMoves.length == 0) return 0;
			if(legalMoves.length == 1) {
				
				if(legalMoves[0].toString().indexOf("noop") > -1) {
					debug("not our turn");
					return 0;
				}
			}*/
			
			int index = generator.nextInt(legalMoves.length);
			IMove[] action = new IMove[1];
			action[0] = legalMoves[index];
			//Generate the next state
			IGameState nextNode = null;
			try {
				nextNode = this.reasoner.getNextState(gameState, action);
			} catch (InterruptedException e) {
				System.out.println("Was unable to calculate the next node.");
			}
			return runPlayout(nextNode);
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
