package is.ru.gapl.strategy;

import is.ru.gapl.model.ActionNodePair;
import is.ru.gapl.model.Node;
//import is.ru.gapl.search.DepthFirst;
import is.ru.gapl.search.ISearch;
import is.ru.gapl.search.IterativeDeepening;

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
	private ISearch searchAlog;
	
    /**
	 * @param searchTreeRoot
	 * @param fringe
	 */
	public ExhaustiveSearchStrategy() {
		
		super();
		this.searchTreeRoot = null;
		this.fringe			= new ArrayList<Node>();
		this.nodeCache		= new HashMap<IGameState, Node>();
		//this.searchAlog		= new DepthFirst();
		this.searchAlog		= new IterativeDeepening();
	}
	
	@Override
	public IMove getMove(IGameNode currentNode) {
		
		IMove[] previousMoves = currentNode.getMoves();
		if(nodeCache.containsKey(currentNode)) {
			this.searchTreeRoot = nodeCache.get(currentNode);
				
				this.fringe.add(this.searchTreeRoot);
				
		}else {
			
			// first time we need a new node 
			Node startNode = new Node();
			startNode.setState(currentNode.getState());
			startNode.setNextStates(null);
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
		
		this.fringe = new ArrayList<Node>();
		
		return searchAlog.bestMove(random, nodeCache, searchTR);
	} 

	

	/**
	 * 
	 * @param fringe
	 * @param playTime
	 */
	private void expandTree(ArrayList<Node> fringe, int playTime) {
		
		System.out.println("Node: "+fringe.size());
		
		long startTime = System.currentTimeMillis();
		long endTime = startTime + (1000 * playTime -15000);

		while(System.currentTimeMillis() < endTime && !fringe.isEmpty()) {
			
			// take the head
			Node node = fringe.remove(0);
			if(node.getScore() == -1) {
				List<Node> expandedNodes = expand(node);
				fringe.addAll(expandedNodes);
				node.setExpanded(true);
			}
			
		}
		System.out.println("Used time: "+(System.currentTimeMillis()-startTime));
		
		
	}
	
	/**
	 * 
	 * 
	 * @param node
	 * @return
	 */
	private ArrayList<Node> expand(Node node) {
		
		ArrayList<Node> nodeList				= new ArrayList<Node>();
		HashMap<IGameState, IMove> nextState			= new HashMap<IGameState, IMove>();
		
		try {
			IMove[] moves = game.getReasoner().getLegalMoves(roleName, node.getState());
			
			for (IMove move : moves) {
				
				IMove[] singleMove = new IMove[1];
				singleMove[0] = move;
			
				IGameState newState	= game.getReasoner().getNextState(node.getState(), singleMove);
				
				// check if there exists a node for this state
				if(!nodeCache.containsKey(newState)) {
					Node childNode	= new Node();
					childNode.setState(newState);
					//childNode.setParentState(node.getState());
					childNode.setScore(-1);

					if(newState.isTerminal()) { 
						childNode.setScore(game.getReasoner().getGoalValue(roleName, newState));
						System.out.println("Terminal");
					}
					
				
					
				//	System.out.println("CREATE NEW NODE");
					
					nodeCache.put(newState, childNode);
					nodeList.add(childNode);
					nextState.put(newState, singleMove[0]);
				}
				
				else {
					
					// use the existing node and add it to the actionList
					Node cacheNode = nodeCache.get(newState);
					
					nextState.put(cacheNode.getState(), singleMove[0]);
					//System.out.println("USE CACHE NODE");
				if(!cacheNode.isExpanded()){
						//System.out.println("NODE IS EXP");
						nodeList.add(cacheNode);
					}
				}
			}	
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		node.setNextStates(nextState);
		
		return nodeList;
	}
}
