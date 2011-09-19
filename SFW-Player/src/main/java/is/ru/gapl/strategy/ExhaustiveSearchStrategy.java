package is.ru.gapl.strategy;

import is.ru.gapl.model.Node;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.palamedes.gdl.core.model.IGameNode;
import org.eclipse.palamedes.gdl.core.model.IMove;
import org.eclipse.palamedes.gdl.core.simulation.strategies.AbstractStrategy;

public class ExhaustiveSearchStrategy extends AbstractStrategy {

	private Node searchTreeRoot;
	private List<Node> fringe;
	
    /**
	 * @param searchTreeRoot
	 * @param fringe
	 */
	public ExhaustiveSearchStrategy() {
		
		super();
		this.searchTreeRoot = null;
		this.fringe			= new ArrayList<Node>();
	}
	
	@Override
	public IMove getMove(IGameNode currentNode) {
		
		
		IMove[] previousMoves = currentNode.getMoves();
		if(previousMoves.length > 0) {
			
		}
		// TODO Auto-generated method stub
		return null;
	}

	
}
