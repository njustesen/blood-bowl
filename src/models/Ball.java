package models;

public class Ball {
	Square square;
	boolean inGame;
	boolean underControl;
	private boolean onGround;
	
	public Ball() {
		
	}
	
	public Ball(Square square, boolean inGame, boolean underControl) {
		this.square = square;
		this.inGame = inGame;
		this.underControl = false;
	}

	public Square getSquare() {
		return square;
	}

	public void setSquare(Square square) {
		this.square = square;
	}

	public boolean isInGame() {
		return inGame;
	}

	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}

	public boolean isUnderControl() {
		return underControl;
	}

	public void setUnderControl(boolean underControl) {
		this.underControl = underControl;
	}
	
	public boolean isOnGround(){
		
		return this.onGround;
	}

	public void setOnGround(boolean b) {
		this.onGround = true;
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (inGame ? 1231 : 1237);
		result = prime * result + (onGround ? 1231 : 1237);
		result = prime * result + ((square == null) ? 0 : square.hashCode());
		result = prime * result + (underControl ? 1231 : 1237);
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
		Ball other = (Ball) obj;
		if (inGame != other.inGame)
			return false;
		if (onGround != other.onGround)
			return false;
		if (square == null) {
			if (other.square != null)
				return false;
		} else if (!square.equals(other.square))
			return false;
		if (underControl != other.underControl)
			return false;
		return true;
	}
	
	
	
}
