package is.ru.gapl.search;

import java.util.HashMap;

import is.ru.gapl.exception.PlayTimeOverException;
import is.ru.gapl.exception.SearchMethodException;
import is.ru.gapl.model.StateValue;
import is.ru.gapl.strategy.SinglePlayerExhaustiveSearchStrategy;

import org.eclipse.palamedes.gdl.core.model.IGameState;
import org.eclipse.palamedes.gdl.core.model.IMove;
import org.eclipse.palamedes.gdl.core.model.IReasoner;
import org.eclipse.palamedes.gdl.core.simulation.strategies.AbstractStrategy;

/**
 * 
 * @author SFW GROUP
 *
 */
public class IterativeDeepeningSearch implements ISearch {

	private SinglePlayerExhaustiveSearchStrategy strategy;
	private String roleName;
	private IReasoner reasoner;
	private HashMap<IGameState,StateValue> statesCache;
	
	public IterativeDeepeningSearch() {
		
		this.statesCache = new HashMap<IGameState, StateValue>();
	}
	
	@Override
	public void search(IGameState gameState, AbstractStrategy strategy) throws SearchMethodException, PlayTimeOverException {
		
		// do nothing if we are in a terminal state
		if(gameState.isTerminal()) throw new SearchMethodException("Current state is a terminal state");
		
		if(statesCache.containsKey(gameState)) {
			this.strategy.setBestMove((statesCache.get(gameState)).getBestMove());
			throw new SearchMethodException("Found state in the cache");
		}
			
		/**
		 *@TODO FIX ME - unsafe cast. Solution introduce abstract strategy class or interface 
		 */	
		this.strategy	= (SinglePlayerExhaustiveSearchStrategy) strategy;
		this.roleName	= this.strategy.getRoleName();
		this.reasoner	= this.strategy.getReasoner();
		
		try {
			IMove[] moves = this.reasoner.getLegalMoves(roleName, gameState);
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
			while(true) {
				
				for (IMove move : moves) {
					
					singleMove[0] = move;
					IGameState newState	= reasoner.getNextState(gameState, singleMove);
					System.out.println("NEW-STATE: "+newState+" :: MOVE: "+move);
					// try to find the max score
					newScore = maxScore(newState, depth, 0);
					if (newScore > maxScore) {
						this.strategy.setBestMove(move);
						maxScore = newScore;
						System.out.println(" - MOVE: "+move+" :: NEW-SCORE: "+newScore);
					}
					// stop searching if we found the best solution
					if (newScore == 100) throw new SearchMethodException("We found the best solution");
				}
				statesCache.put(gameState, new StateValue(maxScore, depth, this.strategy.getBestMove()));
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
	private int maxScore( IGameState nextState, int depth, int count) throws InterruptedException, PlayTimeOverException { 

		int maxScore = -1;
		if(statesCache.containsKey(nextState)) {
			maxScore = (statesCache.get(nextState)).getScore();
			
			// if the state contains the max score
			if(maxScore == 100) return maxScore;
			// return the cache value if terminal
			if(nextState.isTerminal()) return maxScore;
		}
		
		// if the state is a terminal state return the calculated value
		if(nextState.isTerminal()) {
			maxScore = reasoner.getGoalValue(roleName, nextState);
			this.statesCache.put(nextState, new StateValue(maxScore));
			System.out.println("PUT TERMINAL STATE: "+nextState+" DEPTH: "+count+" SCORE: "+maxScore);
			
			return maxScore;
		}
		
		// if the current depth is equals to the specified depth return the maxscore
		if(count >= depth) {
			return maxScore;
		}
		
		IMove bestMove	= null;
		int newScore	= -1;
		int c = count;
		c++;
		
		IMove[] moves = this.reasoner.getLegalMoves(roleName, nextState);
		IMove[] singleMove = new IMove[1];
		for (IMove move : moves) {
			
			checkTime();
			
			singleMove[0] = move;
			IGameState newState	= reasoner.getNextState(nextState, singleMove);
			
			// try to find the max score
			newScore = maxScore(newState, depth, c);
			if (newScore > maxScore) {
				maxScore = newScore;
				bestMove = move;
			}
			// stop searching if we found the best solution
			if (newScore == 100) return maxScore;
		}
		
		return maxScore;
	}
	
	private void checkTime() throws PlayTimeOverException {
		
		if(strategy.isTimeUp()) throw new PlayTimeOverException();
	}
}
