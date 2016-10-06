package models;

public class Coach {
	String name;
	Team team;
	
	public Coach(String name, Team team) {
		super();
		this.name = name;
		this.team = team;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}
	
}
