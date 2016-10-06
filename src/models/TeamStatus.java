package models;

public class TeamStatus {
	private int score;
	private int rerolls;
	private boolean rerolledThisTurn;
	private int fans;
	private int FAME;
	private int babes;
	private boolean hasBlitzed;
	private boolean hasFouled;
	private boolean hasPassed;
	private boolean hasHandedOf;
	
	public TeamStatus(int rerolls) {
		super();
		this.score = 0;
		this.rerolls = rerolls;
		this.rerolledThisTurn = false;
		this.babes = 0;
		this.fans = 0;
		this.FAME = 0;
		this.hasBlitzed = false;
		this.hasFouled = false;
		this.hasPassed = false;
		this.hasHandedOf = false;
	}
	
	public TeamStatus(int score, int rerolls, int babes) {
		super();
		this.score = score;
		this.rerolls = rerolls;
		this.babes = babes;
		this.rerolledThisTurn = false;
		this.fans = 0;
		this.FAME = 0;
		this.hasBlitzed = false;
		this.hasFouled = false;
		this.hasPassed = false;
		this.hasHandedOf = false;
	}
	
	public void reset() {
		this.rerolledThisTurn = false;
		this.hasBlitzed = false;
		this.hasFouled = false;
		this.hasPassed = false;
		this.hasHandedOf = false;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getRerollsDif() {
		return rerolls;
	}

	public void setRerolls(int rerolls) {
		this.rerolls = rerolls;
	}

	public int getBabes() {
		return babes;
	}

	public void setBabes(int babes) {
		this.babes = babes;
	}

	public int getRerolls() {
		return rerolls;
	}

	public boolean rerolledThisTurn() {
		return rerolledThisTurn;
	}
	
	public void setRerolledThisTurn(boolean b){
		rerolledThisTurn = b;
	}

	public int getFans() {
		return fans;
	}

	public void setFans(int fans) {
		this.fans = fans;
	}

	public int getFAME() {
		return FAME;
	}

	public void setFAME(int fAME) {
		FAME = fAME;
	}

	public boolean isRerolledThisTurn() {
		return rerolledThisTurn;
	}

	public boolean hasBlitzed() {
		return hasBlitzed;
	}
	
	public void setHasBlitzed(boolean b){
		hasBlitzed = b;
	}

	public void incScore() {
		score++;
		
	}

	public boolean hasFouled() {
		return hasFouled;
	}
	
	public void setHasFouled(boolean b){
		hasFouled = b;
	}

	public boolean hasPassed() {
		return hasPassed;
	}

	public void setHasPassed(boolean hasPassed) {
		this.hasPassed = hasPassed;
	}

	public boolean hasHandedOf() {
		return hasHandedOf;
	}

	public void setHasHandedOf(boolean hasHandedOf) {
		this.hasHandedOf = hasHandedOf;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + FAME;
		result = prime * result + babes;
		result = prime * result + fans;
		result = prime * result + (hasBlitzed ? 1231 : 1237);
		result = prime * result + (hasFouled ? 1231 : 1237);
		result = prime * result + (hasHandedOf ? 1231 : 1237);
		result = prime * result + (hasPassed ? 1231 : 1237);
		result = prime * result + (rerolledThisTurn ? 1231 : 1237);
		result = prime * result + rerolls;
		result = prime * result + score;
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
		TeamStatus other = (TeamStatus) obj;
		if (FAME != other.FAME)
			return false;
		if (babes != other.babes)
			return false;
		if (fans != other.fans)
			return false;
		if (hasBlitzed != other.hasBlitzed)
			return false;
		if (hasFouled != other.hasFouled)
			return false;
		if (hasHandedOf != other.hasHandedOf)
			return false;
		if (hasPassed != other.hasPassed)
			return false;
		if (rerolledThisTurn != other.rerolledThisTurn)
			return false;
		if (rerolls != other.rerolls)
			return false;
		if (score != other.score)
			return false;
		return true;
	}
	
	
	
}