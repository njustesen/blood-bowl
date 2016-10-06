package models;

import models.dice.D6;

public class CoinToss {
	
	boolean tossed;
	boolean awayPickedHeads;
	boolean resultHeads;
	boolean homeReceives;
	
	public CoinToss() {
		this.tossed = false;
		this.awayPickedHeads = false;
		this.resultHeads = false;
		this.homeReceives = false;
	}

	public boolean isTossed() {
		return tossed;
	}

	public void setTossed(boolean tossed) {
		this.tossed = tossed;
	}

	public boolean hasAwayPickedHeads() {
		return awayPickedHeads;
	}

	public void setAwayPickedHeads(boolean pick) {
		this.awayPickedHeads = pick;
	}

	public boolean isResultHeads() {
		return resultHeads;
	}

	public void setResultHeads(boolean result) {
		this.resultHeads = result;
	}

	public boolean isHomeReceives() {
		return homeReceives;
	}

	public void setHomeReceives(boolean homeReceives) {
		this.homeReceives = homeReceives;
	}

	public void Toss() {
		
		// Toss coin
		D6 d = new D6();
		d.roll();

		if(d.getResultAsInt() > 3){
			setResultHeads(true);
		} else {
			setResultHeads(false);
		}
		
		tossed = true;
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (awayPickedHeads ? 1231 : 1237);
		result = prime * result + (homeReceives ? 1231 : 1237);
		result = prime * result + (resultHeads ? 1231 : 1237);
		result = prime * result + (tossed ? 1231 : 1237);
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
		CoinToss other = (CoinToss) obj;
		if (awayPickedHeads != other.awayPickedHeads)
			return false;
		if (homeReceives != other.homeReceives)
			return false;
		if (resultHeads != other.resultHeads)
			return false;
		if (tossed != other.tossed)
			return false;
		return true;
	}
	
	
}
