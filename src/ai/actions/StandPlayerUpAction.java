package ai.actions;

import models.Player;

public class StandPlayerUpAction extends PlayerAction {

	private Player player;

	public StandPlayerUpAction(Player player) {
		super();
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
}
