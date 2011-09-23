package is.ru.gapl.strategy;

import is.ru.gapl.model.ActionNodePair;
import is.ru.gapl.model.Node;
import is.ru.gapl.search.DepthFirst;
import is.ru.gapl.search.ISearch;
import is.ru.gapl.search.IterativeDeepening;
import is.ru.gapl.tree.BuildTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.palamedes.gdl.core.model.IGameNode;
import org.eclipse.palamedes.gdl.core.model.IGameState;
import org.eclipse.palamedes.gdl.core.model.IMove;
import org.eclipse.palamedes.gdl.core.simulation.strategies.AbstractStrategy;

public class ExhaustiveSearchStrategy extends AbstractStrategy {

	private Node searchTreeRoot;
	private ArrayList<Node> fringe;
	private String roleName;
	private ISearch searchAlog;
	private HashMap<IGameState, Node> nodeCache;
	
    /**
	 * @param searchTreeRoot
	 * @param fringe
	 */
	public ExhaustiveSearchStrategy() {
		
		super();
		this.searchTreeRoot = null;
		this.fringe			= new ArrayList<Node>();
		//this.searchAlog		= new DepthFirst();
		this.searchAlog		= new IterativeDeepening();
		this.nodeCache		= new HashMap<IGameState, Node>();
		
	}
	
	@Override
	public IMove getMove(IGameNode currentNode) {

			
			if(!nodeCache.containsKey(currentNode.getState())) {
				this.searchTreeRoot = new Node();
				this.searchTreeRoot.setState(currentNode.getState());
				this.searchTreeRoot.setParentNode(null);
				this.searchTreeRoot.setScore(-1);
				this.fringe.add(this.searchTreeRoot);
				this.roleName		= game.getRoleNames()[playerNumber];
			} else {
				this.searchTreeRoot = nodeCache.get(currentNode.getState());
			}
			ExecutorService executor = Executors.newCachedThreadPool();
			
				Runnable buildTree = new BuildTree(this.fringe, this.nodeCache, game, roleName);
				executor.execute(buildTree);
	
			while(!searchTreeRoot.isExpanded()) {
				System.out.println("wait");
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
		
		return searchAlog.bestMove(searchTR);
	} 
}
