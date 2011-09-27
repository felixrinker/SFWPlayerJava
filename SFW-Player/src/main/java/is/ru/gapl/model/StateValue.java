package is.ru.gapl.model;

import org.eclipse.palamedes.gdl.core.model.IMove;
/**
 * 
 * @author SFW GROUP
 *
 */
public class StateValue {

	private int score;
	private IMove bestMove;
	
	/**
	 * Constructor for terminal state
	 * 
	 * @param score the terminal score
	 */
	public StateValue(int score) {
		super();
		this.score = score;
		this.bestMove = null;
	}
	
	/**
	 * Constructs a usual state value
	 * 
	 * @param score the best score
	 * @param bestMove teh best move
	 */
	public StateValue(int score, IMove bestMove) {
		super();
		this.score = score;
		this.bestMove = bestMove;
	}

	/*************************** GETTER / SETTER *******************************/
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public IMove getBestMove() {
		return bestMove;
	}

	public void setBestMove(IMove bestMove) {
		this.bestMove = bestMove;
	}
}
