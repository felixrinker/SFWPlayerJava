package is.ru.gapl.model;

public class ActionValue {

	
	double averageScore;
	int numberSimulations;
	
	public ActionValue() {
		super();
		this.averageScore = 1.0;
		this.numberSimulations = 0;
	}

	@Override
	public String toString() {
		return "ActionValue [averageScore=" + averageScore
				+ ", numberSimulations=" + numberSimulations + "]";
	}
}
