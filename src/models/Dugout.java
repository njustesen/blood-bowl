package models;

import java.util.ArrayList;

public class Dugout {
	protected Team team;
	protected ArrayList<Player> reserves;
	protected ArrayList<Player> knockedOut;
	protected ArrayList<Player> deadAndInjured;
	
	public Dugout(Team team) {
		super();
		this.team = team;
		this.reserves = new ArrayList<Player>();
		this.knockedOut = new ArrayList<Player>();
		this.deadAndInjured = new ArrayList<Player>();
		//putPlayersInReserves();
	}

	public void putPlayersInReserves() {
		
		for(Player p : team.getPlayers()){
			reserves.add(p);
			p.setPosition(null);
		}
		
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public ArrayList<Player> getReserves() {
		return reserves;
	}

	public void setReserves(ArrayList<Player> reserves) {
		this.reserves = reserves;
	}

	public ArrayList<Player> getKnockedOut() {
		return knockedOut;
	}

	public void setKnockedOut(ArrayList<Player> knockedOut) {
		this.knockedOut = knockedOut;
	}

	public ArrayList<Player> getDeadAndInjured() {
		return deadAndInjured;
	}

	public void setDeadAndInjured(ArrayList<Player> deadAndInjured) {
		this.deadAndInjured = deadAndInjured;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((deadAndInjured == null) ? 0 : deadAndInjured.hashCode());
		result = prime * result
				+ ((knockedOut == null) ? 0 : knockedOut.hashCode());
		result = prime * result
				+ ((reserves == null) ? 0 : reserves.hashCode());
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
		Dugout other = (Dugout) obj;
		if (deadAndInjured == null) {
			if (other.deadAndInjured != null)
				return false;
		} else if (!deadAndInjured.equals(other.deadAndInjured))
			return false;
		if (knockedOut == null) {
			if (other.knockedOut != null)
				return false;
		} else if (!knockedOut.equals(other.knockedOut))
			return false;
		if (reserves == null) {
			if (other.reserves != null)
				return false;
		} else if (!reserves.equals(other.reserves))
			return false;
		return true;
	}
	
	
	
}
