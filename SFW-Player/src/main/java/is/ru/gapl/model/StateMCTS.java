package is.ru.gapl.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.palamedes.gdl.core.model.IMove;

public class StateMCTS {

	
	private int numberSimulations;
	private Map<IMove, ActionValue> actions;
	
	
	
	public StateMCTS(IMove[] moves) {
		super();
		this.actions = new HashMap<IMove, ActionValue>();
		
		// init the actions
		for(IMove move: moves) {
			this.actions.put(move, new ActionValue());
		}
	}
	
	
	public int getNumberSimulations() {
		return numberSimulations;
	}
	
	public void increaseNumberSimulations() {
		this.numberSimulations++;
	}
	
	public void increaseNumberSimulationsForAction(IMove action) {
		
		this.actions.get(action).numberSimulations++;
	}
	

	public void updateAvgScoreForAction(IMove action, int score) {
		
		
		ActionValue aV = this.actions.get(action);
		
		aV.averageScore = (aV.averageScore * aV.numberSimulations + score) /(aV.numberSimulations +1);
	}
	
	/**
	 * The mystic C in the calculation is missing :(
	 * 
	 * @return
	 */
	public IMove getActionWithHighesUCT() {
		
		IMove returnAction = null;
		double highUCT = 0.0;
		double tempUCT = 0.0;
		for(IMove action : actions.keySet()) {
			
			ActionValue aV = this.actions.get(action);
			tempUCT = aV.averageScore * Math.sqrt( Math.log(this.numberSimulations) / aV.numberSimulations);
			
			if(tempUCT > highUCT) {
				highUCT = tempUCT;
				returnAction = action;
			}
		}
		return returnAction;
	}
}
