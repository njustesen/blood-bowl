package ai.actions;

public class SelectCoinSideAction implements Action {

	boolean heads;
	
	public SelectCoinSideAction(boolean heads){
		this.heads = heads;
	}

	public boolean isHeads() {
		return heads;
	}

	public void setHeads(boolean heads) {
		this.heads = heads;
	}
	
}
