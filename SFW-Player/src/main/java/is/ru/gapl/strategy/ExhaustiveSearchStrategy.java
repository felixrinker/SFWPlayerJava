package is.ru.gapl.strategy;

import is.ru.gapl.model.ActionNodePair;
import is.ru.gapl.model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.palamedes.gdl.core.model.IGameNode;
import org.eclipse.palamedes.gdl.core.model.IGameState;
import org.eclipse.palamedes.gdl.core.model.IMove;
import org.eclipse.palamedes.gdl.core.simulation.strategies.AbstractStrategy;

public class ExhaustiveSearchStrategy extends AbstractStrategy {

	private Node searchTreeRoot;
	private ArrayList<Node> fringe;
	private String roleName;
	private HashMap<IGameState, Node> nodeCache;
	
    /**
	 * @param searchTreeRoot
	 * @param fringe
	 */
	public ExhaustiveSearchStrategy() {
		
		super();
		this.searchTreeRoot = null;
		this.fringe			= new ArrayList<Node>();
		this.nodeCache		= new HashMap<IGameState, Node>();
	}
	
	@Override
	public IMove getMove(IGameNode currentNode) {
		
		IMove[] previousMoves = currentNode.getMoves();
		if(previousMoves != null) {
				this.searchTreeRoot = new Node();
				this.searchTreeRoot.setState(currentNode.getState());
				this.searchTreeRoot.setParentNode(null);
				this.searchTreeRoot.setScore(-1);
				this.fringe.add(this.searchTreeRoot);
				
		}else {
			
			// first time we need a new node 
			Node startNode = new Node();
			startNode.setState(currentNode.getState());
			startNode.setParentNode(null);
			startNode.setScore(-1);
			this.fringe.add(startNode);
			this.searchTreeRoot = startNode;
			this.roleName = game.getRoleNames()[playerNumber];
		}
		// TODO Auto-generated method stub
		return solve(searchTreeRoot);
	}

	/**
	 * 
	 * @param searchTR
	 * @return
	 */
	private IMove solve(Node searchTR) {
		
		expandTree(fringe, match.getPlayTime());
		return bestMove(searchTR);
	} 
	
	
	/**
	 * 
	 * @param fringe
	 * @param playTime
	 */
	private void expandTree(ArrayList<Node> fringe, int playTime) {
		
		long endTime = System.currentTimeMillis()+ (1000 * playTime -2500);

		while(System.currentTimeMillis() < endTime && !fringe.isEmpty()) {
			
			// take the head
			Node node = fringe.remove(0);
			if(node.getScore() == -1) {
				List<Node> expandedNodes = expand(node);
				fringe.addAll(expandedNodes);
			}
		}
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	private IMove bestMove(Node node) {
		
		int maxScore = 0;
		ArrayList<ActionNodePair> actionList = node.getActionList();

		ActionNodePair actionNode = actionList.remove(0);
		IMove bestAction = actionNode.getAction();
        maxScore		 = 0;
			
			for(ActionNodePair aNP : actionList) {
				
				long endTime = System.currentTimeMillis()+ 2500;
				while(System.currentTimeMillis() < endTime) {
				
					int depth = 0;
					int score = maxScore(aNP.getNode(), depth, 0);
					if(score == 100) { return aNP.getAction(); }
					if(score > maxScore) { 
						maxScore = score;
						bestAction = aNP.getAction();
					}
				}
			}
		
		return bestAction;
	}
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	private int maxScore( Node node, int depth, int count ) {
		
		if( node.getScore() > -1 ) { return node.getScore(); }
		if( node.getActionList().isEmpty() ) { return -1; }
		if( count == depth ) { return -1; }
		
		int maxScore = 0;
		for( ActionNodePair aNP : node.getActionList() ) {
			
			int score = maxScore(aNP.getNode(), depth, count);
			if(score == 100 || score == -1) { return score;}
			if(score > maxScore) { maxScore = score;}
		}
		//increase depth counter
		count++;
		return maxScore;
	}

	/**
	 * 
	 * 
	 * @param node
	 * @return
	 */
	private ArrayList<Node> expand(Node node) {
		
		ArrayList<Node> nodeList				= new ArrayList<Node>();
		ArrayList<ActionNodePair> actionList	= new ArrayList<ActionNodePair>();
		
		try {
			IMove[] moves = game.getReasoner().getLegalMoves(roleName, node.getState());
			
			for (IMove move : moves) {
				
				IMove[] singleMove = new IMove[1];
				singleMove[0] = move;
			
				IGameState newState	= game.getReasoner().getNextState(node.getState(), singleMove);
				
				// check if there exists a node for this state
				if(!nodeCache.containsKey(newState)) {
					Node childNode		= new Node();
					childNode.setState(newState);
					childNode.setParentNode(null);
					childNode.setScore(-1);

					if(newState.isTerminal()) { 
						childNode.setScore(game.getReasoner().getGoalValue(roleName, newState));
					}
					
					nodeList.add(childNode);
					ActionNodePair newACP = new ActionNodePair();
					newACP.setAction(singleMove[0]);
					newACP.setNode(childNode);
					actionList.add(newACP);
					
					nodeCache.put(newState, childNode);
				}else {
					
					// use the existing node and add it to the actionList
					ActionNodePair newACP = new ActionNodePair();
					newACP.setAction(singleMove[0]);
					newACP.setNode(nodeCache.get(newState));
					actionList.add(newACP);
				}
			}	
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		node.setActionList(actionList);
		
		return nodeList;
	}
}
