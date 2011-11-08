package is.ru.gapl.search;

import static org.apache.commons.collections.map.AbstractReferenceMap.SOFT;
import is.ru.gapl.exception.PlayTimeOverException;
import is.ru.gapl.exception.SearchMethodException;
import is.ru.gapl.model.NodeActionPair;
import is.ru.gapl.model.StateMCTS;
import is.ru.gapl.strategy.MyExhaustiveSearchStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections.map.ReferenceMap;
import org.eclipse.palamedes.gdl.core.model.IGame;
import org.eclipse.palamedes.gdl.core.model.IGameNode;
import org.eclipse.palamedes.gdl.core.model.IMove;
import org.eclipse.palamedes.gdl.core.model.IReasoner;
import org.eclipse.palamedes.gdl.core.simulation.Match;
import org.eclipse.palamedes.gdl.core.simulation.strategies.AbstractStrategy;
import org.eclipse.palamedes.gdl.core.simulation.strategies.FringeFrame;


/**
 * This is an attempt at implementing the Monte Carlo Search Tree
 * using the descriptions from lecture 5.
 * 
 */

public class MonteCarloTreeSearchCombined implements ISearch {
	private MyExhaustiveSearchStrategy strategy;

	
	private Random generator;
	private boolean debug = true;
	private IReasoner reasoner;
	private String ownRoleName;
	private ReferenceMap stateTree;
	private ArrayList<NodeActionPair> backTrackList;


	private Match match;


	private IGame game;


	private int ownRoleNum;

	@Override
	public void init(AbstractStrategy strategy) {
		this.strategy	= (MyExhaustiveSearchStrategy) strategy;
		this.ownRoleName	= this.strategy.getOwnRoleName();
		this.reasoner		= this.strategy.getReasoner();
		this.match			= this.strategy.getCurrentMatch();
		this.game			= this.match.getGame();
		this.ownRoleNum		= this.strategy.getOwnRoleNum();
		this.generator = new Random();
		
		this.stateTree = new ReferenceMap(SOFT, SOFT);
	}
	
	@Override
	public void search(IGameNode currentNode, AbstractStrategy strategy)
			throws SearchMethodException, PlayTimeOverException {
		
		
		this.backTrackList = new ArrayList<NodeActionPair>();
		
		this.MonteCarloTree(currentNode);
	}

	private void MonteCarloTree(IGameNode currentNode) throws PlayTimeOverException {
		
		// if gameState is still in tree
		if(stateTree.containsKey(currentNode)) {
			// get the state value
			StateMCTS stateMCTS = (StateMCTS) this.stateTree.get(currentNode);
			IMove[] action = new IMove[1];
			// pick the action with the highest UCT value
			action[0] = stateMCTS.getActionWithHighesUCT();
			
			try {
				IGameNode nextState = this.game.getNextNode(currentNode, action);
				// push gameState on backtrace stack
				this.backTrackList.add(new NodeActionPair(nextState, action));
				
				// call next iteration
				MonteCarloTree(nextState);
				
			} catch (InterruptedException e) {
				debug("Was unable to calculate the next state.");
			}
		}else {
			try {
				
				// Expand: add all direct successors of s to the tree
				List<IMove[]> legalActions = this.game.getCombinedMoves(currentNode);
				
				if(legalActions.isEmpty())  {
					
					IMove[] mo = currentNode.getMoves();
					this.strategy.setBestMove(mo[0]);
					return;
				}
				FringeFrame fringe = new FringeFrame(currentNode);
				
				this.stateTree.put(currentNode, new StateMCTS());
				
				while ( fringe.hasUnexpandedMove() ) {
		    		
					this.strategy.checkTime();
					
		        	IMove[] nextMove = fringe.getRandomUnexpandedMove();
		        	IGameNode  newChildNode  = game.getNextNode(currentNode, nextMove);
				
		        	// run playout
		        	int score = runPlayout(newChildNode);
				
		        	// update parent state
					this.update(currentNode, nextMove[this.ownRoleNum], score);
					this.strategy.setBestMove(nextMove[this.ownRoleNum]);
					
					if(!backTrackList.isEmpty())
					{//Now update all other states
						for(int y = backTrackList.size()-1; y >= 0; y--) {
							NodeActionPair sap = this.backTrackList.get(y);
							this.update(sap.gameState, sap.action[0], score);
							if(y == 0 && -1 < sap.action[0].toString().indexOf("noop")) {
								this.strategy.setBestMove(sap.action[0]);
							}
						}
					}				
				
				}
				
				
			} catch (InterruptedException e) {
				debug("Was unable to retrieve the legal moves.");
			}
			
		}
	}
	
	
	private void update(IGameNode currentNode, IMove action, int score) {
		StateMCTS stateMCTS = (StateMCTS) this.stateTree.get(currentNode);
		// Number of simulation with action
		stateMCTS.increaseNumberSimulationsForAction(action);
		// Number of simulation with state
		stateMCTS.increaseNumberSimulations();
		// Average score of action
		stateMCTS.updateAvgScoreForAction(action, score);
	}
	
	private int runPlayout(IGameNode nextNode) throws PlayTimeOverException, InterruptedException {
		
		this.strategy.checkTime();
		//If the node is terminal, the return the value.
		if(nextNode.isTerminal()) {
			int score = 0;
			score = nextNode.getState().getGoalValue(ownRoleNum);
			
			return score;
		}
		else {
			//Copied from the search method.
			//It is supposed to pick a random move and generate
			//the next state, so the method can be run again with
			//this next state.
			
			try {
				this.game.getCombinedMoves(nextNode);
			} catch (InterruptedException e1) {
				System.out.println("Was unable to retrieve the legal moves.");
			}
			
			FringeFrame fringe 			= new FringeFrame(nextNode);
			IMove[] 	nextMove 		= fringe.getRandomUnexpandedMove();
        	IGameNode  newChildNode 	= game.getNextNode(nextNode, nextMove);
			
			return runPlayout(newChildNode);
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
