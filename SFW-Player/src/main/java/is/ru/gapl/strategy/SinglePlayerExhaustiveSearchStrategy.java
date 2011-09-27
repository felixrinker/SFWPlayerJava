package is.ru.gapl.strategy;

import is.ru.gapl.exception.PlayTimeOverException;
import is.ru.gapl.exception.SearchMethodException;
import is.ru.gapl.search.IterativeDeepeningSearch;
import is.ru.gapl.search.ISearch;
import is.ru.gapl.search.SearchFactory;

import org.eclipse.palamedes.gdl.core.model.IGameNode;
import org.eclipse.palamedes.gdl.core.model.IMove;
import org.eclipse.palamedes.gdl.core.model.IReasoner;
import org.eclipse.palamedes.gdl.core.simulation.Match;
import org.eclipse.palamedes.gdl.core.simulation.strategies.AbstractStrategy;

public class SinglePlayerExhaustiveSearchStrategy extends AbstractStrategy {

	private IReasoner	reasoner;
	private String		roleName;
	private long		endTime;
	private long		startTime;
	private IMove		bestMove;
	private SearchFactory searchFactory;
	private ISearch searchMethod;

	/**
	 * Constructs the SinglePlayerExhaustiveSearchStrategy class
	 */
	public SinglePlayerExhaustiveSearchStrategy() {
	
		
		this.reasoner	= null;
		this.roleName	= null;
		this.endTime	= 0;
		this.startTime	= 0;
		this.bestMove	= null;
		
		// get instance of the search factory
		this.searchFactory = SearchFactory.getInstance();
		
		// add a new search method to the factory
		this.searchFactory.addSearchMethod("DeepeningFirstSearch", IterativeDeepeningSearch.class.getCanonicalName());
	}
	
	
	@Override
	public void initMatch(Match initMatch) {
		
		super.initMatch(initMatch);
		
		if(match.getGame().getRoleCount()>1){
			System.err.println(this.getClass().getName() + " only works for single player games!");
			return;
		}
		this.reasoner = match.getGame().getReasoner();
		this.roleName = match.getGame().getRoleNames()[0];
		
		this.bestMove = null;
		
		// set the timeout
		this.setTimeout(match.getStartTime());
		
		try {
			// try to create the search method
			this.searchMethod = this.searchFactory.createSearchMethod("DeepeningFirstSearch");
		} catch (SearchMethodException e) {
			System.out.println(e.getMessage());
		}
		
		/////////////////// START SEARCHING /////////////////////
		
		try {
			// start searching
			this.searchMethod.search(match.getCurrentNode().getState(), this);
			
        } catch (PlayTimeOverException e) {
        	System.out.println("initMatch() stopped by time.");
		} catch (SearchMethodException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public IMove getMove(IGameNode currentNode) {
		
		// reset the best move var
		this.bestMove = null;
		
		try {
			// set the timeout
			setTimeout(match.getPlayTime());
			// start searching
			this.searchMethod.search(currentNode.getState(), this);
			
        } catch (PlayTimeOverException e) {
        	System.out.println("initMatch() stopped by time.");
		} catch (SearchMethodException e) {
			System.out.println(e.getMessage());
		}
		
		return bestMove;
	}

/*********************** PRIVATE METHODS **********************************/
	
	/**
	 * 
	 * @param palyTime
	 */
	private void setTimeout(int palyTime) {
		
		this.startTime	= System.currentTimeMillis();
		this.endTime		= System.currentTimeMillis() + 1000 * palyTime;
	}
	
	/**
	 * 
	 * @return
	 */
	
/*********************** PUBLIC GETTER METHODS *****************************/	
	public boolean isTimeUp() {
		return System.currentTimeMillis() >= this.endTime;
	}

	public IReasoner getReasoner() {
		return reasoner;
	}

	public String getRoleName() {
		return roleName;
	}

	public long getEndTime() {
		return endTime;
	}


	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getStartTime() {
		return startTime;
	}
	

	public IMove getBestMove() {
		return bestMove;
	}


	public void setBestMove(IMove bestMove) {
		this.bestMove = bestMove;
	}
}
