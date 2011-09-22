package is.ru.gapl.search;

import is.ru.gapl.model.ActionNodePair;
import is.ru.gapl.model.Node;

import java.util.ArrayList;

import org.eclipse.palamedes.gdl.core.model.IMove;

public class DepthFirst implements ISearch{

	public DepthFirst() {
		
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
	
			for(ActionNodePair aNP : actionList) {
				
				int score = maxScore(aNP.getNode());
				if(score == 100) { return aNP.getAction(); }
				if(score > maxScore) { 
					maxScore = score;
					bestAction = aNP.getAction();	
				}
			}
			
		System.out.println("MaxScore:"+maxScore);	
		return bestAction;
	}
	
	/**
	 * 
	 * @param node
	 * @param endTime 
	 * @return
	 */
	private int maxScore( Node node ) {		
		
		if( node.getScore() > -1 ) { return node.getScore(); }
		if( node.getActionList().isEmpty() ) { return -1; }
		
		int maxScore = 0;
		
		for( ActionNodePair aNP : node.getActionList() ) {
			
			int score = maxScore(aNP.getNode());
			if(score == 100 || score == -1) { return score;}
			if(score > maxScore) { maxScore = score;}
		}
		
		return maxScore;
	}
}
