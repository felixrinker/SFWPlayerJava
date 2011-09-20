package is.ru.gapl.model;

import java.io.Serializable;

import org.eclipse.palamedes.gdl.core.model.IMove;

public class ActionNodePair implements Serializable{

	private IMove action;
	private Node node;
	
	/**
	 * 
	 * @param action
	 * @param node
	 */
	
	public ActionNodePair() {
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
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActionNodePair other = (ActionNodePair) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}
	
	
}
