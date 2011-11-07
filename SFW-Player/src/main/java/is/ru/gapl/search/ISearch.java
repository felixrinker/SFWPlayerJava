package is.ru.gapl.search;

import is.ru.gapl.exception.PlayTimeOverException;
import is.ru.gapl.exception.SearchMethodException;

import org.eclipse.palamedes.gdl.core.model.IGameNode;
import org.eclipse.palamedes.gdl.core.simulation.strategies.AbstractStrategy;

/**
 * 
 * @author SFW GROUP
 *
 */
public interface ISearch {

	/**
	 * 
	 * @param strategy
	 */
	public void init(AbstractStrategy strategy);
	
	/**
	 * Provides the search
	 * 
	 * @param currentNode the game state to start from
	 * @param strategy the strategy object
	 * 
	 * @throws PlayTimeOverException is thrown if the specified playtime is over
	 * @throws SearchMethodException 
	 */
	public void search(IGameNode currentNode, AbstractStrategy strategy) throws SearchMethodException, PlayTimeOverException;
}
