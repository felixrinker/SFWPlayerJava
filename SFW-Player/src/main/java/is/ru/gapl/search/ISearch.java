package is.ru.gapl.search;

import org.eclipse.palamedes.gdl.core.model.IMove;

import is.ru.gapl.model.Node;

public interface ISearch {

	public IMove bestMove(Node node);
}
