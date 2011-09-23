package is.ru.gapl.search;

import is.ru.gapl.model.ActionNodePair;
import is.ru.gapl.model.Node;

import java.util.ArrayList;

import org.eclipse.palamedes.gdl.core.model.IMove;

public class IterativeDeepening implements ISearch {

	public IterativeDeepening() {
		
	}
	/**
	 * 
	 * @param node
	 * @return
	 */
	public IMove bestMove(Node node) {
		
		int maxScore = 0;
		ArrayList<ActionNodePair> actionList = node.getActionList();

		ActionNodePair actionNode = actionList.get(0);
		IMove bestAction = actionNode.getAction();
			
		int depth = 0;
		long endTime = System.currentTimeMillis()+ 8000;
		while(System.currentTimeMillis() < endTime) {
			
			for(ActionNodePair aNP : actionList) {
				
				int score = maxScore(aNP.getNode(), depth, 1, endTime);
				if(score == 100) { return aNP.getAction(); }
				if(score > maxScore) { 
					maxScore = score;
					bestAction = aNP.getAction();	
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
		
		if(System.currentTimeMillis() >= endTime) { return -1; }
		if( node.getScore() > -1 ) { return node.getScore(); }
		if( node.getActionList().isEmpty() ) { return -1; }
		if( count >= depth ) { return -1; }
		
		int maxScore = 0;
		//increase depth counter
		count++;
		for( ActionNodePair aNP : node.getActionList() ) {
			
			int score = maxScore(aNP.getNode(), depth, count, endTime);
			if(score == 100 || score == -1) { return score;}
			if(score > maxScore) { maxScore = score;}
		}
		
		return maxScore;
	}
}
