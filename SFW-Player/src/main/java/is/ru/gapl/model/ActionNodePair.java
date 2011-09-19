package is.ru.gapl.model;

import org.eclipse.palamedes.gdl.core.model.IMove;

public class ActionNodePair {

	private IMove action;
	private Node node;
	
	/**
	 * 
	 * @param action
	 * @param node
	 */
	
	public ActionNodePair(IMove action, Node node) {
		super();
		this.action = action;
		this.node = node;
	}

	/*************************** GETTER / SETTER *******************************/
	
	/**
	 * @return the action
	 */
	public IMove getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(IMove action) {
		this.action = action;
	}

	/**
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	/*************************** TO STRING *******************************/
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ActionNodePair [action=" + action + ", node=" + node + "]";
	}
}
