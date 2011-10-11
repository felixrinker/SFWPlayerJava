package is.ru.gapl.search;

import java.util.HashMap;

import is.ru.gapl.exception.PlayTimeOverException;
import is.ru.gapl.exception.SearchMethodException;
import is.ru.gapl.model.StateValue;
import is.ru.gapl.strategy.MyExhaustiveSearchStrategy;

import org.eclipse.palamedes.gdl.core.model.IGameState;
import org.eclipse.palamedes.gdl.core.model.IMove;
import org.eclipse.palamedes.gdl.core.model.IReasoner;
import org.eclipse.palamedes.gdl.core.simulation.strategies.AbstractStrategy;
import static org.apache.commons.collections.map.AbstractReferenceMap.SOFT;
import org.apache.commons.collections.map.ReferenceMap;


/**
 * 
 * @author SFW GROUP
 *
 */
public class IterativeDeepeningSearch implements ISearch {

	private MyExhaustiveSearchStrategy strategy;
	private String roleName;
	private IReasoner reasoner;
	private ReferenceMap statesCache;
	private IMove bestMove;
	
	
	public IterativeDeepeningSearch() {
		
		this.statesCache = new ReferenceMap(SOFT, SOFT);
	}
	
	@Override
	public void search(IGameState gameState, AbstractStrategy strategy) throws SearchMethodException, PlayTimeOverException {
		
		// do nothing if we are in a terminal state
		if(gameState.isTerminal()) throw new SearchMethodException("Current state is a terminal state");
		
		HashMap<IMove, IGameState> nextMoves = null;
		IMove[] moves = null;
		
		if(statesCache.containsKey(gameState)) {
			StateValue cacheState = ((StateValue) statesCache.get(gameState));
			
				this.strategy.setBestMove(cacheState.getBestMove());
				throw new SearchMethodException("Found state in the cache");
		}
		
		/**
		 *@TODO FIX ME - unsafe cast. Solution introduce abstract strategy class or interface 
		 */	
		this.strategy	= (MyExhaustiveSearchStrategy) strategy;
		this.roleName	= this.strategy.getRoleName();
		this.reasoner	= this.strategy.getReasoner();
		
		try {
			// calculate the moves if they not set
			moves = this.reasoner.getLegalMoves(roleName, gameState);
			System.out.println("LEGAL MOVES:");
			for (IMove move : moves) {
			System.out.println(" - "+move.toString());
			}	
			// set the first move as the default best move
			this.strategy.setBestMove(moves[0]);
			
			IMove[] singleMove = new IMove[1];
			int maxScore = -1;
			int newScore = -1;
			int depth	= 1;
		//	while(true) {
				
				for (IMove move : moves) {
					
					checkTime();
					
					singleMove[0] = move;
					IGameState newState = null;
					// check if cache values can be used
					if(nextMoves == null) {
						newState = reasoner.getNextState(gameState, singleMove);
					}else {
						// use cache value
						newState = nextMoves.get(move);
					}	
					//System.out.println("NEW-STATE: "+newState+" :: MOVE: "+move);
					// try to find the max score
					newScore = maxScore(newState, depth, 0);
					
					
					
					if (newScore > maxScore) {
						this.bestMove = move;
						this.strategy.setBestMove(move);
						maxScore = newScore;
						System.out.println(" - MOVE: "+move+" :: NEW-SCORE: "+newScore);
					}
					// stop searching if we found the best solution
					if (newScore == 100) break;//throw new SearchMethodException("We found the best solution");
				}
				
				statesCache.put(gameState, new StateValue(maxScore, bestMove));
				// increase depth
				depth++;
			//}	
			
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}
/*************************************** PRIVATE METHODS *******************************************/	
	/**
	 * 
	 * @param node
	 * @return
	 * @throws InterruptedException 
	 * @throws PlayTimeOverException 
	 */
	private int maxScore( IGameState nextState, int depth, int count) throws InterruptedException, PlayTimeOverException { 

		int maxScore = -1;
		IMove[] moves = null;
		HashMap<IMove, IGameState> nextMoves=null;
		
		if(statesCache.containsKey(nextState)) {
			StateValue cacheState = (StateValue) statesCache.get(nextState);
			maxScore = cacheState.getScore();
			
			return maxScore;
		}
		
		// if the state is a terminal state return the calculated value
		if(nextState.isTerminal()) {
			maxScore = reasoner.getGoalValue(roleName, nextState);
			this.statesCache.put(nextState, new StateValue(maxScore));
			//System.out.println("PUT TERMINAL STATE: "+nextState+" DEPTH: "+count+" SCORE: "+maxScore);
			
			return maxScore;
		}
		
		// if the current depth count is equals to the specified depth return the maxscore
		/*if(count >= depth) {
			return maxScore;
		}*/
		
		IMove bestMove	= null;
		int newScore	= -1;
		int c = count;
		c++;
		// calculate the moves if they not set
		moves = this.reasoner.getLegalMoves(roleName, nextState);
		
		IMove[] singleMove = new IMove[1];
		for (IMove move : moves) {
			
			checkTime();
			
			singleMove[0] = move;
			IGameState newState = null;
			// check if cache values can be used
			if(nextMoves == null) {
				newState = reasoner.getNextState(nextState, singleMove);
			}else {
				// use cache value
				newState = nextMoves.get(move);
			}	
			
			// try to find the max score
			newScore = maxScore(newState, depth, c);
			
			if (newScore > maxScore) {
				maxScore = newScore;
				bestMove = move;
			}
			// stop searching if we found the best solution
			if (newScore == 100) {
				//System.out.println("RETURN 100");
				break;
			}
		}
		
		statesCache.put(nextState, new StateValue(maxScore, bestMove));
		
		return maxScore;
	}
	
	private void checkTime() throws PlayTimeOverException {
		
		if(strategy.isTimeUp()) throw new PlayTimeOverException();
	}
}
