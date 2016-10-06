package ai.actions;

public class SelectDieAction implements Action {

	int die;

	public SelectDieAction(int die) {
		super();
		this.die = die;
	}

	public int getDie() {
		return die;
	}

	public void setDie(int die) {
		this.die = die;
	}
	
}
