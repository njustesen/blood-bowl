package models.actions;

import models.Player;

public class Foul {

	Player fouler;
	Player target;
	
	public Foul(Player fouler, Player target) {
		super();
		this.fouler = fouler;
		this.target = target;
	}

	public Player getFouler() {
		return fouler;
	}

	public void setFouler(Player fouler) {
		this.fouler = fouler;
	}

	public Player getTarget() {
		return target;
	}

	public void setTarget(Player target) {
		this.target = target;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fouler == null) ? 0 : fouler.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		Foul other = (Foul) obj;
		if (fouler == null) {
			if (other.fouler != null)
				return false;
		} else if (!fouler.equals(other.fouler))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
	
}
