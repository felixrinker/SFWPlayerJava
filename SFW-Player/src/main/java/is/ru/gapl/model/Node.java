package is.ru.gapl.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.eclipse.palamedes.gdl.core.model.IGameState;

public class Node implements Serializable {

	private static final long serialVersionUID = -5235334727724273744L;
	private IGameState state;
	private Node parentNode;
	private ArrayList<ActionNodePair> actionList;
	private int score;
	private boolean isExpanded;
	private int depth;
	
	public Node() {
        actionList = new ArrayList<ActionNodePair>();
	}

	/*************************** GETTER / SETTER *******************************/
	
	

	/**
	 * @param score the score to set
	 */
	public void setScore(int score) {
		this.score = score;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public IGameState getState() {
		return state;
	}

	public void setState(IGameState newState) {
		this.state = newState;
	}

	public Node getParentNode() {
		return parentNode;
	}

	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}

	public ArrayList<ActionNodePair> getActionList() {
		return actionList;
	}

	public void setActionList(ArrayList<ActionNodePair> actionList) {
		this.actionList = actionList;
	}

	public int getScore() {
		return score;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((actionList == null) ? 0 : actionList.hashCode());
		result = prime * result
				+ ((parentNode == null) ? 0 : parentNode.hashCode());
		result = prime * result + score;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}
	
	

	public boolean isExpanded() {
		return isExpanded;
	}

	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Node))
			return false;
		Node other = (Node) obj;
		if (actionList == null) {
			if (other.actionList != null)
				return false;
		} else if (!actionList.equals(other.actionList))
			return false;
		if (parentNode == null) {
			if (other.parentNode != null)
				return false;
		} else if (!parentNode.equals(other.parentNode))
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
	
	@Override
	public String toString() {
		return "Node [state=" + state + ", parentNode=" + parentNode
				+ ", actionList=" + actionList + ", score=" + score + "]";
	}

	/*************************** TO STRING *******************************/
	
	
}
