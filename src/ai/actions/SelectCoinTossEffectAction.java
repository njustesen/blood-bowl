package ai.actions;

public class SelectCoinTossEffectAction implements Action {
	
	private boolean receive;

	public SelectCoinTossEffectAction(boolean receive) {
		super();
		this.receive = receive;
	}

	public boolean isReceive() {
		return receive;
	}

	public void setReceive(boolean receive) {
		this.receive = receive;
	}
	
}
