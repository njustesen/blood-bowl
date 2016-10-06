package models.actions;

import models.Player;
import models.Square;


public class Catch {

	private Player player;
	private Square square;
	private int success;

	public Catch(Player player, Square square, int success) {
		
		this.player = player;
		this.square = square;
		this.success = success;
		
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

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		result = prime * result + ((square == null) ? 0 : square.hashCode());
		result = prime * result + success;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Catch other = (Catch) obj;
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		if (square == null) {
			if (other.square != null)
				return false;
		} else if (!square.equals(other.square))
			return false;
		if (success != other.success)
			return false;
		return true;
	}
	
	
	
}
