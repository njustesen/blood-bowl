package ai.actions;

import models.Square;

public class SelectPushSquareAction implements Action {

	Square square;

	public SelectPushSquareAction(Square square) {
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
