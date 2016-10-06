package ai.actions;

import models.Player;

public class PassPlayerAction extends DoublePlayerAction {

	Player passer;
	Player catcher;
	
	public PassPlayerAction(Player passer, Player catcher) {
		super();
		this.passer = passer;
		this.catcher = catcher;
	}

	public Player getPasser() {
		return passer;
	}

	public void setPasser(Player passer) {
		this.passer = passer;
	}

	public Player getCatcher() {
		return catcher;
	}

	public void setCatcher(Player catcher) {
		this.catcher = catcher;
	}

	@Override
	public Player getPlayerA() {
		return passer;
	}

	@Override
	public Player getPlayerB() {
		return catcher;
	}

	@Override
	public void setPlayerA(Player player) {
		passer = player;
	}

	@Override
	public void setPlayerB(Player player) {
		catcher = player;
	}
	
	
	
}
