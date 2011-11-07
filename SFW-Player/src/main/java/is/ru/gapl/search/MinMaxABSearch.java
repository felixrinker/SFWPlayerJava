package is.ru.gapl.search;

import is.ru.gapl.exception.PlayTimeOverException;
import is.ru.gapl.exception.SearchMethodException;
import is.ru.gapl.strategy.MyExhaustiveSearchStrategy;

import java.util.List;
import java.util.Random;

import org.eclipse.palamedes.gdl.core.model.IGame;
import org.eclipse.palamedes.gdl.core.model.IGameNode;
import org.eclipse.palamedes.gdl.core.model.IGameState;
import org.eclipse.palamedes.gdl.core.model.IMove;
import org.eclipse.palamedes.gdl.core.simulation.Match;
import org.eclipse.palamedes.gdl.core.simulation.strategies.AbstractStrategy;
import org.eclipse.palamedes.gdl.core.simulation.strategies.FringeFrame;

public class MinMaxABSearch implements ISearch {

	private MyExhaustiveSearchStrategy strategy;
	private IGame game;
	private Match match;
	private int ownRoleNum;
	
	
	private IMove[][] bestPath; 
	private IMove[][] bestPathTemp; 
	private IMove returnMove;
	private Random randomGen;
	
	private boolean debug = true;
	
	

	@Override
	public void init(AbstractStrategy strategy) {
		
		/**
		 *@TODO FIX ME - unsafe cast. Solution introduce abstract strategy class or interface 
		 */	
		this.strategy	= (MyExhaustiveSearchStrategy) strategy;
		this.match			= this.strategy.getCurrentMatch();
		this.game			= this.match.getGame();
		this.ownRoleNum		= this.strategy.getOwnRoleNum();
		
		// init best path array
		this.bestPath		= new IMove[this.strategy.getMaxSteps()][match.getGame().getRoleCount()];
        this.bestPathTemp	= new IMove[this.strategy.getMaxSteps()][match.getGame().getRoleCount()];
        this.randomGen 		= new Random();
	}
	
	
	
	@Override
	public void search(IGameNode currentNode, AbstractStrategy strategy) throws SearchMethodException, PlayTimeOverException {
		
		try {
			double bestValue = 0;
			
			System.out.println( "Start Alpha-Beta Pruning");
			bestValue = alphaBetaPruning(currentNode ,-100 ,100);

			debug( "bestGoalValue[" + (int)bestValue + "], move[" + bestPath[currentNode.getDepth()][this.ownRoleNum] + "]");
			
			debug("Current depth: "+currentNode.getDepth() +" //BestPathBeni: ");
    		
    		//DEBUG
    		for(IMove[] move: bestPath) {
    			
    			System.out.print(move[this.ownRoleNum] + " | ");
    	
    		}
			
    		returnMove = bestPath[currentNode.getDepth()][this.ownRoleNum];
    		
    		System.out.println("return move : "+returnMove);
    		
            if(returnMove == null) {
            	// play random move
            	List<IMove[]> moves = game.getCombinedMoves(currentNode);
            	returnMove = moves.get( randomGen.nextInt( moves.size() ) )[this.ownRoleNum];
            }
            
            System.out.println( "bestGoalValue[" + (int)bestValue + "], move[" + returnMove + "]");
            
			this.strategy.setBestMove(returnMove);
		}
        catch (InterruptedException e) {
        	System.out.println("ERROR!!!!");
        	e.printStackTrace();
        	this.strategy.setBestMove(null);
        }
	}
	
	/**
	 * 
	 * @param gameNode the current gameNode
	 * @param alpha the alpha value
	 * @param beta the beta value
	 * 
	 * @return
	 * 
	 * @throws PlayTimeOverException
	 * @throws InterruptedException
	 */
	private double alphaBetaPruning (IGameNode gameNode, double alpha, double beta) throws PlayTimeOverException, InterruptedException {
		
		// check the remaining palyTime
		this.strategy.checkTime();
		
		// get currentPlayer
		int roleNum = getCurrentRole(gameNode);
		
		//debug("current role: "+roleNum);
		
		// if terminal return evaluate state
		if( gameNode.getState().isTerminal() ) {
			debug("Found terminal state");
			return evaluateState( gameNode.getState(), this.ownRoleNum );
		}
		
		
		// initial nodes
        game.getCombinedMoves(gameNode);
        FringeFrame fringe = new FringeFrame(gameNode);
        
        // if its our turn, then MAX
	    if( roleNum == this.ownRoleNum ) {
	    	//debug("Its our turn");
	    	while ( fringe.hasUnexpandedMove() ) {
	    		
	        	IMove[] nextMove = fringe.getRandomUnexpandedMove();
	        	
	        	bestPathTemp[gameNode.getDepth()] = nextMove;
	        	IGameNode  newChildNode  = game.getNextNode(gameNode, nextMove);
	        	
	        	double score = alphaBetaPruning(newChildNode, alpha, beta);
	        	
	        	if( score > alpha) {
	        		//debug("new alpha value: "+ score);
		        	alpha = score;
		        	this.strategy.setBestMove(nextMove[this.ownRoleNum]);
		        	bestPath = bestPathTemp.clone();
		        }
	        	if (alpha >= beta){     
	        		return beta;
	        	}
		        
	        }
	    	// our best move
	    	return alpha;
	    	
	    }else{ 
	    	//debug("Its oponents turn");
	    	while ( fringe.hasUnexpandedMove() ) {
	    		
	        	IMove[] nextMove = fringe.getRandomUnexpandedMove();
	        	
	        	bestPathTemp[gameNode.getDepth()] = nextMove;
	        	
	        	IGameNode  newChildNode  = game.getNextNode(gameNode, nextMove);
	        	
	        	double score = alphaBetaPruning(newChildNode, alpha, beta);
	        	
	        	if( score < beta){
	        		beta = score;
	        		
	        	}if (alpha >= beta){  
		        	
		        	return alpha;
		        }
	    	}
	    	// the opponent bets move
		    return beta;
		    
	    }
	}
	
	/**
	 * 
	 * @param gameNode
	 * @return
	 */
	private int getCurrentRole(IGameNode gameNode) {
	
		int roleCount = game.getRoleCount();
		for( int roleNum = 0; roleNum < roleCount; roleNum++) {
			
			if( checkTurn( gameNode, roleNum ) ) return roleNum;
		}
		return 0;
	}
	
	/**
	 * 
	 * @param roleNum
	 * @param gameNode
	 * @return
	 */
	private boolean checkTurn (IGameNode gameNode, int roleNum) {
		try {
			
			IMove[] moves = game.getLegalMoves(gameNode)[roleNum];	
			
			for( IMove move: moves ) {
				
				// if moves contain a noop its not our turn
				int index = move.toString().indexOf("noop");
				if( index > -1 ) return false;
			}
			
		} catch (InterruptedException e) {	
			return false;
		}	
		// if no noop containing its our turn
		return true;
	}	
		
	/**
	 * 
	 * @param gameNode
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
	
	private void debug(String txt) {
		
		if(debug) {
			
			System.out.println("DEBUG: "+txt);
		}
	}
}