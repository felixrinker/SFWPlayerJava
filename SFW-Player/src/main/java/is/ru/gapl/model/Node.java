package is.ru.gapl.model;

import java.util.List;

import org.eclipse.palamedes.gdl.core.model.IGameNode;

public class Node {

	private IGameNode state;
	private Node parentNode;
	private List<ActionNodePair> actionList;
	
	/**
	 * @param state
	 * @param parentNode
	 * @param actionList
	 */
	public Node(IGameNode state, Node parentNode,
			List<ActionNodePair> actionList) {
		
		super();
		this.state = state;
		this.parentNode = parentNode;
		this.actionList = actionList;
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

	/**
	 * @param actionList the actionList to set
	 */
	public void setActionList(List<ActionNodePair> actionList) {
		this.actionList = actionList;
	}

	/*************************** TO STRING *******************************/
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Node [state=" + state + ", parentNode=" + parentNode
				+ ", actionList=" + actionList + "]";
	}
}
