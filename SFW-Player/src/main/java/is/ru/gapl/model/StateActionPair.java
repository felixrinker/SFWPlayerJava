package is.ru.gapl.model;

import org.eclipse.palamedes.gdl.core.model.IGameState;
import org.eclipse.palamedes.gdl.core.model.IMove;

public class StateActionPair {

	
	public IGameState gameState;
	public IMove[] action;
	
	public StateActionPair(IGameState gameState, IMove[] action) {
		super();
		this.gameState = gameState;
		this.action = action;
	}
}
