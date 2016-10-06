package models.actions;

import models.Player;

public class HandOff {
	
	private Player passer;
	private Player catcher;
	
	public HandOff(Player passer, Player catcher) {
		super();
		this.passer = passer;
		this.catcher = catcher;
	}

	public Player getPasser() {
		return passer;
	}

	public void setPasser(Player passer) {
		this.passer = passer;
	}

	public Player getCatcher() {
		return catcher;
	}

	public void setCatcher(Player catcher) {
		this.catcher = catcher;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((catcher == null) ? 0 : catcher.hashCode());
		result = prime * result + ((passer == null) ? 0 : passer.hashCode());
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
		HandOff other = (HandOff) obj;
		if (catcher == null) {
			if (other.catcher != null)
				return false;
		} else if (!catcher.equals(other.catcher))
			return false;
		if (passer == null) {
			if (other.passer != null)
				return false;
		} else if (!passer.equals(other.passer))
			return false;
		return true;
	}
	
}
