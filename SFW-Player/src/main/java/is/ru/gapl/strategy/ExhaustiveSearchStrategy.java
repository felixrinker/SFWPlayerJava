package is.ru.gapl.strategy;

import is.ru.gapl.model.ActionNodePair;
import is.ru.gapl.model.Node;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.palamedes.gdl.core.model.IGameNode;
import org.eclipse.palamedes.gdl.core.model.IMove;
import org.eclipse.palamedes.gdl.core.simulation.strategies.AbstractStrategy;

public class ExhaustiveSearchStrategy extends AbstractStrategy {

	private Node searchTreeRoot;
	private List<Node> fringe;
	
    /**
	 * @param searchTreeRoot
	 * @param fringe
	 */
	public ExhaustiveSearchStrategy() {
		
		super();
		this.searchTreeRoot = null;
		this.fringe			= new ArrayList<Node>();
	}
	
	@Override
	public IMove getMove(IGameNode currentNode) {
		
		IMove[] previousMoves = currentNode.getMoves();
		if(previousMoves != null) {
			
			this.searchTreeRoot = new Node(currentNode, searchTreeRoot, -1);
		}else {
			Node startNode = new Node(currentNode, searchTreeRoot, -1);
			this.fringe.add(startNode);
			this.searchTreeRoot = startNode;
		}
		// TODO Auto-generated method stub
		return solve(searchTreeRoot);
	}

	private IMove solve(Node searchTR) {
		
		expandTree(fringe, match.getPlayTime());
		return bestMove(searchTR);
	} 
	
	private void expandTree(List<Node> fringe, int playTime) {
		
		long endTime = System.currentTimeMillis()+(60 * playTime -5);
		while(System.currentTimeMillis() < endTime && !fringe.isEmpty()) {
			
			Node node = fringe.remove(fringe.size()-1);
			if(node.getScore() == -1) {
				
				List<Node> expandNodes = expand(node);
				fringe.addAll(expandNodes);
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
		List<ActionNodePair> actionList = node.getActionList();

		if(actionList.isEmpty()) { System.out.println("Emty actionlist");}
			ActionNodePair actionNode = actionList.remove(actionList.size()-1);
			IMove bestAction = actionNode.getAction();
			
			for(ActionNodePair aNP : actionList) {
				
				int score = maxScore(aNP.getNode());
				if(score == 100) { return aNP.getAction(); }
				if(score > maxScore) { 
					maxScore = score;
					bestAction = aNP.getAction();
				}
			}
		
		return bestAction;
	}
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	private int maxScore(Node node) {
		
		if (node.getScore() > -1) { return node.getScore();}
		if(node.getActionList().isEmpty()) { return -1;}
		
		int maxScore = 0;
		for(ActionNodePair aNP : node.getActionList()) {
			
			int score = maxScore(aNP.getNode());
			if(score == 100 || score == -1) { return score;}
			if(score > maxScore) { maxScore = score;}
		}
		return maxScore;
	}

	/**
	 * 
	 * 
	 * @param node
	 * @return
	 */
	private List<Node> expand(Node node) {
		
		List<Node> nodeList				= new ArrayList<Node>();
		List<ActionNodePair> actionList	= new ArrayList<ActionNodePair>();
		
		try {
			IMove[][] moves = game.getLegalMoves(node.getState());
			for (IMove[] mp : moves) {
				
				IMove[] singleMove = new IMove[1];
				singleMove[0] = mp[playerNumber];
				
				IGameNode newState	= game.getNextNode(node.getState(), singleMove);
				Node childNode		= new Node(newState, node, -1); 

				if(newState.isTerminal()) { 
					childNode.setScore(game.getGoalValues(newState)[playerNumber]);
				}
				
				nodeList.add(childNode);
				actionList.add(new ActionNodePair(singleMove[0], childNode));
			}	
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		node.addActionList(actionList);
		
		return nodeList;
	}
}
