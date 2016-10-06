package models.actions;

import java.util.ArrayList;

import models.Player;

public class Pass {

	private Player passer;
	private Player catcher;
	private int success;
	private boolean accurate;
	private boolean awaitingInterception;
	private ArrayList<Player> interceptionPlayers;
	
	public Pass(Player passer, Player catcher, int success) {
		super();
		this.passer = passer;
		this.catcher = catcher;
		this.success = success;
		this.accurate = false;
		this.awaitingInterception = false;
		this.interceptionPlayers = new ArrayList<Player>();
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

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public void setAccurate(boolean b) {
		this.accurate = b;
		
	}

	public boolean isAccurate() {
		return accurate;
	}

	public void setAwaitingInterception(boolean b) {
		this.awaitingInterception = b;
		
	}

	public boolean isAwaitingInterception() {
		return awaitingInterception;
	}

	public ArrayList<Player> getInterceptionPlayers() {
		return interceptionPlayers;
	}

	public void setInterceptionPlayers(ArrayList<Player> interceptionPlayers) {
		this.interceptionPlayers = interceptionPlayers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (accurate ? 1231 : 1237);
		result = prime * result + (awaitingInterception ? 1231 : 1237);
		result = prime * result + ((catcher == null) ? 0 : catcher.hashCode());
		result = prime
				* result
				+ ((interceptionPlayers == null) ? 0 : interceptionPlayers
						.hashCode());
		result = prime * result + ((passer == null) ? 0 : passer.hashCode());
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
		Pass other = (Pass) obj;
		if (accurate != other.accurate)
			return false;
		if (awaitingInterception != other.awaitingInterception)
			return false;
		if (catcher == null) {
			if (other.catcher != null)
				return false;
		} else if (!catcher.equals(other.catcher))
			return false;
		if (interceptionPlayers == null) {
			if (other.interceptionPlayers != null)
				return false;
		} else if (!playersEquals(other))
			return false;
		if (passer == null) {
			if (other.passer != null)
				return false;
		} else if (!passer.equals(other.passer))
			return false;
		if (success != other.success)
			return false;
		return true;
	}

	private boolean playersEquals(Pass other) {
		for(Player p : other.getInterceptionPlayers()){
			if (interceptionPlayers.contains(p))
				return false;
		}
		return true;
	}
	
	
	
}
