package ai.actions;

import models.Square;

public class PlaceBallAction implements Action {

	private Square square;

	public PlaceBallAction(Square square) {
		super();
		this.square = square;
	}

	public Square getSquare() {
		return square;
	}

	public void setSquare(Square square) {
		this.square = square;
	}
	
}
