package is.ru.gapl.search;

import is.ru.gapl.model.ActionNodePair;
import is.ru.gapl.model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.eclipse.palamedes.gdl.core.model.IGameState;
import org.eclipse.palamedes.gdl.core.model.IMove;

public class IterativeDeepening implements ISearch {

	private HashMap<IGameState, Node> nodeCache;

	public IterativeDeepening() {
		
	}
	/**
	 * 
	 * @param node
	 * @return
	 */
	public IMove bestMove(Random random, HashMap<IGameState, Node> nodeCache,
			Node node) {
		
		this.nodeCache = nodeCache;
		int maxScore = 0;
		HashMap<IGameState, IMove> actionList = node.getNextStates();

		Set<IGameState> actionNode = actionList.keySet();
		Iterator<IGameState> it = actionNode.iterator();
		
		IMove bestAction = actionList.get(it.next());;
			
		int depth = 0;
		long endTime = System.currentTimeMillis()+14900;
		while(System.currentTimeMillis() < endTime) {
			
			for(IGameState state : actionList.keySet()) {
				
				Node cacheNode = nodeCache.get(state);
				
				int score = maxScore(cacheNode, depth, 1, endTime);
				if(score == 100) { return actionList.get(state); }
				if(score > maxScore) { 
					maxScore = score;
					bestAction = actionList.get(state);	
				}
			}
			depth++;
			
		}	
		System.out.println("depth:"+depth);
		System.out.println("MaxScore:"+maxScore);	
		return bestAction;
	}
	
	/**
	 * 
	 * @param node
	 * @param endTime 
	 * @return
	 */
	private int maxScore( Node node, int depth, int count, long endTime ) {
		
		int c = count;
		if(System.currentTimeMillis() >= endTime) { return -1; }
		if( node.getScore() > -1 ) { return node.getScore(); }
		if( node.getNextStates().isEmpty() ) { return -1; }
		if( count >= depth ) { return -1; }
		
		int maxScore = 0;
		//increase depth counter
		c++;
		for(IGameState state : node.getNextStates().keySet()) {
			
			Node cacheNode = nodeCache.get(state);
			
			int score = maxScore(cacheNode, depth, c, endTime);
			if(score == 100 || score == -1) { return score;}
			if(score > maxScore) { maxScore = score;}
		}
		
		return maxScore;
	}
}
