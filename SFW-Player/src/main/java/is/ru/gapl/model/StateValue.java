package is.ru.gapl.model;

import java.util.HashMap;

import org.eclipse.palamedes.gdl.core.model.IMove;
import org.eclipse.palamedes.gdl.core.model.IGameState;
/**
 * 
 * @author SFW GROUP
 *
 */
public class StateValue {

	private int score;
	private IMove bestMove;
	private int depth;
	private HashMap<IMove, IGameState> nextMoves;
	private boolean expanded;
	
	
	/**
	 * Constructor for terminal state
	 * 
	 * @param score the terminal score
	 */
	public StateValue(int score) {
		super();
		this.score = score;
		this.depth = 0;
		this.bestMove = null;
		this.nextMoves = null;
	}
	
	/**
	 * Constructs a usual state value
	 * 
	 * @param score the best score
	 * @param bestMove the best move
	 */
	public StateValue(int score, IMove bestMove) {
		super();
		this.score = score;
		this.depth = 0;
		this.bestMove = bestMove;
		this.nextMoves = null;
	}
	
	/**
	 * 
	 * @param score
	 * @param depth
	 * @param bestMove
	 */
	public StateValue(int score, int depth, IMove bestMove) {
		super();
		this.score = score;
		this.depth = depth;
		this.bestMove = bestMove;
		this.nextMoves = null;
	}
	
	/**
	 * 
	 * @param score
	 * @param depth
	 * @param bestMove
	 */
	public StateValue(int score, int depth, IMove bestMove, HashMap<IMove, IGameState> nextMoves) {
		super();
		this.score = score;
		this.depth = depth;
		this.bestMove = bestMove;
		this.nextMoves = nextMoves;
	}

	/*************************** GETTER / SETTER *******************************/
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public IMove getBestMove() {
		return bestMove;
	}

	public void setBestMove(IMove bestMove) {
		this.bestMove = bestMove;
	}

	public HashMap<IMove, IGameState> getNextMoves() {
		return nextMoves;
	}

	public void setNextMoves(HashMap<IMove, IGameState> nextMoves) {
		this.nextMoves = nextMoves;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	@Override
	public String toString() {
		return "StateValue [score=" + score + ", bestMove=" + bestMove
				+ ", depth=" + depth + ", nextMoves=" + nextMoves
				+ ", expanded=" + expanded + "]";
	}
}
