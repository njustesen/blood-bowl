package models;

public class PlayerStatus {
	
	private Standing standing;
	private PlayerTurn turn;
	private int movementUsed;
	private boolean movedToBlock;
	
	public PlayerStatus() {
		super();
		this.standing = Standing.UP;
		this.turn = PlayerTurn.UNUSED;
		this.movementUsed = 0;
		this.movedToBlock = false;
	}
	
	public void reset(){
		
		this.turn = PlayerTurn.UNUSED;
		this.movementUsed = 0;
		this.movedToBlock = false;
		
	}

	public Standing getStanding() {
		return standing;
	}

	public void setStanding(Standing standing) {
		this.standing = standing;
	}

	public PlayerTurn getTurn() {
		return turn;
	}

	public void setTurn(PlayerTurn turn) {
		this.turn = turn;
	}

	public int getMovementUsed() {
		return movementUsed;
	}

	public void setMovementUsed(int movementUsed) {
		this.movementUsed = movementUsed;
	}

	public void moveOneSquare() {
		movementUsed++;
	}

	public boolean hasMovedToBlock() {
		return movedToBlock;
	}

	public void setMovedToBlock(boolean movementUsedToBlock) {
		this.movedToBlock = movementUsedToBlock;
	}
	
	public void useMovement(int i){
		this.movementUsed += i;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (movedToBlock ? 1231 : 1237);
		result = prime * result + movementUsed;
		result = prime * result
				+ ((standing == null) ? 0 : standing.hashCode());
		result = prime * result + ((turn == null) ? 0 : turn.hashCode());
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
		PlayerStatus other = (PlayerStatus) obj;
		if (movedToBlock != other.movedToBlock)
			return false;
		if (movementUsed != other.movementUsed)
			return false;
		if (standing != other.standing)
			return false;
		if (turn != other.turn)
			return false;
		return true;
	}
	
}
