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
            
			int depth = 0;
			long endTime = System.currentTimeMillis()+ 4950;
			for(ActionNodePair aNP : actionList) {

				int score = maxScore(aNP.getNode(), 1, endTime);
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
	private int maxScore( Node node, int count, long endTime ) { 

		int c = count;
		
		if(System.currentTimeMillis() >= endTime) {
			
			return -1; 
		}
		if (node.getScore() > -1) {
			
			return node.getScore();
			}
		if(node.getActionList().isEmpty()) {
			
			return -1;
			}

		int maxScore = 0;
		c++;
		for(ActionNodePair aNP : node.getActionList()) {

			int score = maxScore(aNP.getNode(), c, endTime);
			if(score == 100 || score == -1) { return score;}
			if(score > maxScore) { maxScore = score;}
		}
		return maxScore;
	}
}
