package is.ru.gapl.search;

import java.util.HashMap;
import java.util.Random;

import org.eclipse.palamedes.gdl.core.model.IGameState;
import org.eclipse.palamedes.gdl.core.model.IMove;

import is.ru.gapl.model.Node;

public interface ISearch {

	public IMove bestMove(Random random, HashMap<IGameState, Node> nodeCache, Node node);
}
