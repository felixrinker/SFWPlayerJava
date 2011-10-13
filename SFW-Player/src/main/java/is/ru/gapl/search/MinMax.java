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

public class MinMax implements ISearch {

	private MyExhaustiveSearchStrategy strategy;
	private IGame game;
	private Match match;
	private int ownRoleNum;
	
	
	private IMove[][] bestPathBeni; 
	private IMove[][] bestPathTempBeni; 
	private IMove returnMove;
	private Random randomGen;
	
	

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
		this.bestPathBeni		= new IMove[this.strategy.getMaxSteps()][match.getGame().getRoleCount()];//maxSteps = maximale Tiefe und anzahl Spieler
        this.bestPathTempBeni	= new IMove[this.strategy.getMaxSteps()][match.getGame().getRoleCount()];//maxSteps = maximale Tiefe und anzahl Spieler
        
        this.randomGen = new Random();
	}
	
	
	
	@Override
	public void search(IGameNode currentNode, AbstractStrategy strategy) throws SearchMethodException, PlayTimeOverException {
		
		try {
			double bestValue = 0;
			
			System.out.println( "Start Alpha-Beta Pruning");
			bestValue = alphaBetaPruning(currentNode ,-100 ,100);

    		System.out.println("Current depth: "+currentNode.getDepth() +" //BestPathBeni: ");
    		
    		//DEBUG
    		for(IMove[] move: bestPathBeni) {
    			
    			System.out.print(move[this.ownRoleNum] + " | ");
    	
    		}
			
    		returnMove = bestPathBeni[currentNode.getDepth()][this.ownRoleNum];
    		
    		System.out.println("return move : "+returnMove);
    		
    		// delete noops
    		for(int i = 0; returnMove.toString().indexOf("noop") !=-1 ; i++) {
    			returnMove = bestPathBeni[currentNode.getDepth()+i][this.ownRoleNum];
	    	}
    		
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
		
		// if terminal return evaluate state
		if( gameNode.getState().isTerminal() ) return evaluateState( gameNode.getState(), this.ownRoleNum );
		
		
		// initial nodes
        game.getCombinedMoves(gameNode);
        FringeFrame fringe = new FringeFrame(gameNode);
        
        // if its our turn, then MAX
	    if( roleNum == this.ownRoleNum ) {
	    	
	    	while ( fringe.hasUnexpandedMove() ) {
	    		
	        	IMove[] moves = fringe.getRandomUnexpandedMove();
	        	
	        	bestPathTempBeni[gameNode.getDepth()] = moves;
	        	IGameNode  newChildNode  = game.getNextNode(gameNode, moves);
	        	
	        	double score = alphaBetaPruning(newChildNode, alpha, beta);
	        	
	        	if( score > alpha) {
		        	alpha = score;
		        	bestPathBeni = bestPathTempBeni.clone();
		        }
	        	if (alpha >= beta){     
	        		return beta;
	        	}
		        
	        }
	    	// our best move
	    	return alpha;
	    	
	    }else{ 
	    	
	    	while ( fringe.hasUnexpandedMove() ) {
	    		
	        	IMove[] moves = fringe.getRandomUnexpandedMove();
	        	
	        	bestPathTempBeni[gameNode.getDepth()] = moves;
	        	
	        	IGameNode  newChildNode  = game.getNextNode(gameNode, moves);
	        	
	        	double score = alphaBetaPruning(newChildNode,alpha,beta);
	        	
	        	if( score < beta){
	        		beta = score;
	        	}
		        if (alpha >= beta){  
		        	
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
			
			if( checkTurn(roleNum,gameNode) ) return roleNum;
		}
		return 0;
	}
	
	/**
	 * 
	 * @param roleNum
	 * @param gameNode
	 * @return
	 */
	private boolean checkTurn (int roleNum, IGameNode gameNode) {
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
			int[] goalsAllRoles = gameState.getGoalValues();
			
			for(int i=0;i<goalsAllRoles.length;i++) {
				
				if( roleNum != i ) {
					sumOponent += goalsAllRoles[i];
				}else{
					ourGoalValue = goalsAllRoles[i]; 
				}
			}
			return ourGoalValue-sumOponent;
			
		}else{
			
			return (-100/game.getRoleCount());
		}
	}
}