package is.ru.gapl.model;

import org.eclipse.palamedes.gdl.core.model.IGameNode;
import org.eclipse.palamedes.gdl.core.model.IGameState;
import org.eclipse.palamedes.gdl.core.model.IMove;

public class NodeActionPair {

	
	public IGameNode gameState;
	public IMove[] action;
	
	public NodeActionPair(IGameNode gameState, IMove[] action) {
		super();
		this.gameState = gameState;
		this.action = action;
	}
}
