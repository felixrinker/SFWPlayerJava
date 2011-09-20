package is.ru.gapl.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.palamedes.gdl.core.model.IGameNode;

public class Node {

	private IGameNode state;
	private Node parentNode;
	private List<ActionNodePair> actionList;
	private int score;
	
	public Node(IGameNode state, Node parentNode, int score) {
		
		super();
		this.state		= state;
		this.parentNode = parentNode;
		this.actionList = new ArrayList<ActionNodePair>();
		this.score		= score;
	}
	
	/**
	 * @param state
	 * @param parentNode
	 * @param actionCollection
	 */
	public Node(IGameNode state, Node parentNode,
			List<ActionNodePair> actionList, int score) {
		
		super();
		this.state		= state;
		this.parentNode = parentNode;
		this.actionList = actionList;
		this.score		= score;
	}

	/*************************** GETTER / SETTER *******************************/
	
	/**
	 * @return the state
	 */
	public IGameNode getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(IGameNode state) {
		this.state = state;
	}

	/**
	 * @return the parentNode
	 */
	public Node getParentNode() {
		return parentNode;
	}

	/**
	 * @param parentNode the parentNode to set
	 */
	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}

	/**
	 * @return the actionList
	 */
	public List<ActionNodePair> getActionList() {
		return actionList;
	}

	public void addActionList(List<ActionNodePair> actionList) {
		this.actionList.addAll(actionList);
	}
	
	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Node [state=" + state + ", parentNode=" + parentNode
				+ ", actionList=" + actionList + ", score=" + score + "]";
	}

	/*************************** TO STRING *******************************/
	
	
}
