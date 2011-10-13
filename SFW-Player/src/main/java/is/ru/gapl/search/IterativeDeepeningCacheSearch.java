package is.ru.gapl.search;

import java.util.HashMap;
import java.util.Set;

import is.ru.gapl.exception.PlayTimeOverException;
import is.ru.gapl.exception.SearchMethodException;
import is.ru.gapl.model.StateValue;
import is.ru.gapl.strategy.MyExhaustiveSearchStrategy;

import org.eclipse.palamedes.gdl.core.model.IGameNode;
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
public class IterativeDeepeningCacheSearch implements ISearch {

	private MyExhaustiveSearchStrategy strategy;
	private String roleName;
	private IReasoner reasoner;
	private ReferenceMap statesCache;
	private IGameState gameState;
	
	
	@Override
	public void init(AbstractStrategy strategy) {
		this.statesCache = new ReferenceMap(SOFT, SOFT);
	}
	
	@Override
	public void search(IGameNode gameNode, AbstractStrategy strategy) throws SearchMethodException, PlayTimeOverException {
		
		this.gameState = gameNode.getState();
		
		// do nothing if we are in a terminal state
		if(gameState.isTerminal()) throw new SearchMethodException("Current state is a terminal state");
		
		HashMap<IMove, IGameState> nextMoves = null;
		IMove[] moves = null;
		if(statesCache.containsKey(gameState)) {
			StateValue cacheState = ((StateValue) statesCache.get(gameState));
			int maxScore = cacheState.getScore();
			
			//this.strategy.setBestMove(cacheState.getBestMove());
			this.strategy.setBestMove(cacheState.getBestMove());
			
			nextMoves = cacheState.getNextMoves();
			if(nextMoves != null) {
				Set<IMove> mov = nextMoves.keySet();
				moves =  mov.toArray(new IMove[0]);
			}
		}
			
		/**
		 *@TODO FIX ME - unsafe cast. Solution introduce abstract strategy class or interface 
		 */	
		this.strategy	= (MyExhaustiveSearchStrategy) strategy;
		this.roleName	= this.strategy.getOwnRoleName();
		this.reasoner	= this.strategy.getReasoner();
		
		try {
			// calculate the moves if they not set
			if(moves == null) moves = this.reasoner.getLegalMoves(roleName, gameState);
			System.out.println("LEGAL MOVES:");
			for (IMove move : moves) {
			System.out.println(" - "+move.toString());
			}	
			// set the first move as the default best move
			
			if(nextMoves == null) {
				this.strategy.setBestMove(moves[0]);
			}
			this.strategy.setBestMove(moves[0]);
			
			IMove[] singleMove = new IMove[1];
			int maxScore = -1;
			int newScore = -1;
			int depth	= 10;
			
			while(true) {
				HashMap<IMove, IGameState> newNextMoves= new HashMap<IMove, IGameState>();
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
					newScore = maxScore(newState, depth);
					
					newNextMoves.put(move, newState);
					
					if (newScore > maxScore) {
						this.strategy.setBestMove(move);
						System.out.println(" - MOVE: "+move+" :: MAX_SCORE: "+maxScore+" NEW-SCORE: "+newScore);
						maxScore = newScore;
					}
					// stop searching if we found the best solution
					if (newScore == 100) throw new SearchMethodException("We found the best solution");
				}
				
				// checks if state exists in cache
				if(statesCache.containsKey(gameState)) {
					StateValue cacheValue = (StateValue) statesCache.get(gameState);
					
					// 
					if(cacheValue.getDepth() < depth) {
						this.statesCache.put(gameState, new StateValue(maxScore, depth, this.strategy.getBestMove(), newNextMoves));
						//System.out.println("UPDATE ROOT STATE: "+gameState+" DEPTH: "+depth+" SCORE: "+maxScore+" MOVE: "+this.strategy.getBestMove());
					}
					
				}else {
					// add new cache entry if nothing exists
					this.statesCache.put(gameState, new StateValue(maxScore, depth, this.strategy.getBestMove(), newNextMoves));
					System.out.println("NEW ROOT STATE: "+gameState+" DEPTH: "+depth+" SCORE: "+maxScore+" MOVE: "+this.strategy.getBestMove());
				}
				// increase depth
				depth++;
			}	
			
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
	private int maxScore( IGameState nextState, int depth) throws InterruptedException, PlayTimeOverException { 

		int maxScore = -1;
		IMove[] moves = null;
		HashMap<IMove, IGameState> nextMoves=null;
		
		if(statesCache.containsKey(nextState)) {
			//System.out.println("CACHE HIT");
			StateValue cacheState = (StateValue) statesCache.get(nextState);
			maxScore = cacheState.getScore();
			
			// return the cache value if terminal
			if(nextState.isTerminal()) return maxScore;
			
			// If the state is already expanded to the needed depth
			if(cacheState.getDepth() >= depth) return maxScore;
			//System.out.println("state depth:"+cacheState.getDepth()+" Search Depth: "+depth);
			
			nextMoves = cacheState.getNextMoves();
			if(nextMoves != null) {
				Set<IMove> mov = nextMoves.keySet();
				moves =  mov.toArray(new IMove[0]);
			}
		}
		
		// if the state is a terminal state return the calculated value
		if(nextState.isTerminal()) {
			maxScore = reasoner.getGoalValue(roleName, nextState);
			this.statesCache.put(nextState, new StateValue(maxScore));
			System.out.println("PUT TERMINAL STATE: "+nextState+" DEPTH: "+depth+" SCORE: "+maxScore);
			
			return maxScore;
		}
		
		// if the current depth count is equals to the specified depth return the maxscore
		if(depth == 0) {
			
			maxScore = reasoner.getGoalValue(roleName, nextState);
			return maxScore;
		}
		
		IMove bestMove	= null;
		int newScore	= -1;
		int newDepth = depth;
		newDepth--;
		// calculate the moves if they not set
		if(moves == null) moves = this.reasoner.getLegalMoves(roleName, nextState);
		
		HashMap<IMove, IGameState> newNextMoves= new HashMap<IMove, IGameState>();
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
			newScore = maxScore(newState, newDepth);
			
			newNextMoves.put(move, newState);
			
			if (newScore > maxScore) {
				maxScore = newScore;
				bestMove = move;
			}
			// stop searching if we found the best solution
			if (newScore == 100) {
				System.out.println("RETURN 100");
				break;
			}
		}
		
		// checks if state exists in cache
		if(statesCache.containsKey(nextState)) {
			StateValue cacheValue = (StateValue)statesCache.get(nextState);
			//System.out.println("CACHE HIT");
			
			// 
			if(cacheValue.getDepth() < depth) {
				this.statesCache.put(nextState, new StateValue(maxScore, newDepth, bestMove, newNextMoves));
				//System.out.println("UPDATE STATE: "+nextState+" DEPTH: "+newDepth+" SCORE: "+maxScore);
			}
			
		}else {
			// add new cache entry if nothing exists
			this.statesCache.put(nextState, new StateValue(maxScore, newDepth, bestMove, newNextMoves));
			//System.out.println("NEW STATE: "+nextState+" DEPTH: "+newDepth+" SCORE: "+maxScore);
		
			//System.out.println("NEW STATE: "+nextState+" DEPTH: "+newDepth+" SCORE: "+maxScore+" nextMoves: "+newNextMoves);
		}
		
		return maxScore;
	}
	
	private void checkTime() throws PlayTimeOverException {
		
		if(strategy.isTimeUp()) throw new PlayTimeOverException();
	}
}
