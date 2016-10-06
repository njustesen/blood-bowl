package ai.actions;

import models.Player;
import models.PlayerTurn;

public class SelectPlayerTurnAction extends PlayerAction {

	PlayerTurn turn;
	Player player;
	
	public SelectPlayerTurnAction(PlayerTurn turn, Player player) {
		super();
		this.turn = turn;
		this.player = player;
	}

	public PlayerTurn getTurn() {
		return turn;
	}

	public void setTurn(PlayerTurn turn) {
		this.turn = turn;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
}
