package ai.actions;

import models.Player;

public class SelectInterceptionAction extends PlayerAction {

	Player intercepter;

	public SelectInterceptionAction(Player intercepter) {
		super();
		this.intercepter = intercepter;
	}
	
	public Player getPlayer() {
		return intercepter;
	}

	public Player getIntercepter() {
		return intercepter;
	}

	public void setIntercepter(Player intercepter) {
		this.intercepter = intercepter;
	}

	@Override
	public void setPlayer(Player player) {
		intercepter = player;
	}
	
}
