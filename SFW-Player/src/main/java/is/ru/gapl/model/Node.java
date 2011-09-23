package is.ru.gapl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.palamedes.gdl.core.model.IGameState;
import org.eclipse.palamedes.gdl.core.model.IMove;

public class Node implements Serializable {

	private static final long serialVersionUID = -5235334727724273744L;
	private IGameState state;
	
	private HashMap<IGameState, IMove> nextStates;
	
	private int score;
	private boolean isExpanded;
	private int depth;
	
	public Node() {
		nextStates = new HashMap<IGameState, IMove>();
        this.isExpanded = false;
	}

	public IGameState getState() {
		return state;
	}

	public void setState(IGameState state) {
		this.state = state;
	}

	public HashMap<IGameState, IMove> getNextStates() {
		return nextStates;
	}

	public void setNextStates(HashMap<IGameState, IMove> nextStates) {
		this.nextStates = nextStates;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	@Override
	public String toString() {
		return "Node [state=" + state + ", nextStates=" + nextStates
				+ ", score=" + score + ", isExpanded=" + isExpanded
				+ ", depth=" + depth + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + depth;
		result = prime * result + (isExpanded ? 1231 : 1237);
		result = prime * result
				+ ((nextStates == null) ? 0 : nextStates.hashCode());
		result = prime * result + score;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (depth != other.depth)
			return false;
		if (isExpanded != other.isExpanded)
			return false;
		if (nextStates == null) {
			if (other.nextStates != null)
				return false;
		} else if (!nextStates.equals(other.nextStates))
			return false;
		if (score != other.score)
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

	/*************************** GETTER / SETTER *******************************/
	
	


	/*************************** TO STRING *******************************/
	

	
	
}
