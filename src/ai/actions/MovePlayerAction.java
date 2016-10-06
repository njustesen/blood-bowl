package ai.actions;

import models.Player;
import models.Square;

public class MovePlayerAction extends PlayerAction {

	private Player player;
	private Square square;

	public MovePlayerAction(Player player, Square square) {
		super();
		this.player = player;
		this.square = square;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Square getSquare() {
		return square;
	}

	public void setSquare(Square square) {
		this.square = square;
	}
	
}
