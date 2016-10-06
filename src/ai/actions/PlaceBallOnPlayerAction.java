package ai.actions;

import models.Player;

public class PlaceBallOnPlayerAction extends PlayerAction {

	private Player player;

	public PlaceBallOnPlayerAction(Player player) {
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
