package is.ru.gapl.tree;

import is.ru.gapl.model.ActionNodePair;
import is.ru.gapl.model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.palamedes.gdl.core.model.IGame;
import org.eclipse.palamedes.gdl.core.model.IGameState;
import org.eclipse.palamedes.gdl.core.model.IMove;

public class BuildTree implements Runnable {

	private ArrayList<Node> fringe;
	private HashMap<IGameState, Node> nodeCache;
	private String roleName;
	private IGame game;
	
	public BuildTree(ArrayList<Node> fringe, HashMap<IGameState, Node> nodeCache, IGame game, String roleName) {
		super();
		this.fringe = fringe;
		this.game = game;
		this. nodeCache = nodeCache;
		this.roleName = roleName;	
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		
		while(!fringe.isEmpty()) {
			
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
	 * @param fringe
	 * @param playTime
	 */
/*	private void expandTree(ArrayList<Node> fringe, int playTime) {
		
		System.out.println("Node: "+fringe.size());
		
		long startTime = System.currentTimeMillis();
		long endTime = startTime + (1000 * playTime -5000);

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
*/	
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
					Node childNode	= new Node();
					childNode.setState(newState);
					childNode.setParentNode(null);
					childNode.setScore(-1);

					if(newState.isTerminal()) { 
						childNode.setScore(game.getReasoner().getGoalValue(roleName, newState));
						//System.out.println("Terminal");
					}
					
					
					ActionNodePair newACP = new ActionNodePair();
					newACP.setAction(singleMove[0]);
					newACP.setNode(childNode);
					actionList.add(newACP);
					
				//	System.out.println("CREATE NEW NODE");
					
					nodeCache.put(newState, childNode);
					nodeList.add(childNode);
				}else {
					
					// use the existing node and add it to the actionList
					Node cacheNode = nodeCache.get(newState);
					ActionNodePair newACP = new ActionNodePair();
					newACP.setAction(singleMove[0]);
					newACP.setNode(cacheNode);
					actionList.add(newACP);
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
		
		node.setActionList(actionList);
		
		return nodeList;
	}

}
