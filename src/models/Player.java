package models;

import java.util.ArrayList;

public abstract class Player {

	protected Race race;
	protected String title;
	protected int cost;
	protected int MA;
	protected int ST;
	protected int AG;
	protected int AV;
	private int number;
	protected ArrayList<Skill> skills = new ArrayList<Skill>();
	protected PlayerStatus playerStatus;
	private Square position;

	private String teamName;
	private Team team;

	
	public Player(Race race, String title, int number, String teamName) {
		super();
		this.race = race;
		this.title = title;
		this.number = number;
		this.playerStatus = new PlayerStatus(); 
		this.position = null;
		this.teamName = teamName;
		this.team = null;

	}

	public Race getRace() {
		return race;
	}

	public String getTeamName() {
		return teamName;
	}

	public String getTitle() {
		return title;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getMA() {
		return MA;
	}

	public void setMA(int mA) {
		MA = mA;
	}

	public int getST() {
		return ST;
	}

	public void setST(int sT) {
		ST = sT;
	}

	public int getAG() {
		return AG;
	}

	public void setAG(int aG) {
		AG = aG;
	}

	public int getAV() {
		return AV;
	}

	public void setAV(int aV) {
		AV = aV;
	}

	public ArrayList<Skill> getSkills() {
		return skills;
	}

	public void setSkills(ArrayList<Skill> skills) {
		this.skills = skills;
	}

	public PlayerStatus getPlayerStatus() {
		return playerStatus;
	}

	public void setPlayerStatus(PlayerStatus playerStatus) {
		this.playerStatus = playerStatus;
	}

	public int getNumber() {
		return number;
	}

	public Square getPosition() {
		return position;
	}
	
	public void setPosition(Square position){
		
		this.position = position;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + AG;
		result = prime * result + AV;
		result = prime * result + MA;
		result = prime * result + ST;
		result = prime * result + cost;
		result = prime * result + number;
		result = prime * result
				+ ((playerStatus == null) ? 0 : playerStatus.hashCode());
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((race == null) ? 0 : race.hashCode());
		result = prime * result + ((skills == null) ? 0 : skills.hashCode());
		result = prime * result + ((team == null) ? 0 : team.hashCode());
		result = prime * result
				+ ((teamName == null) ? 0 : teamName.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		Player other = (Player) obj;
		if (AG != other.AG)
			return false;
		if (AV != other.AV)
			return false;
		if (MA != other.MA)
			return false;
		if (ST != other.ST)
			return false;
		if (cost != other.cost)
			return false;
		if (number != other.number)
			return false;
		if (playerStatus == null) {
			if (other.playerStatus != null)
				return false;
		} else if (!playerStatus.equals(other.playerStatus))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (race != other.race)
			return false;
		if (skills == null) {
			if (other.skills != null)
				return false;
		} else if (!skills.equals(other.skills))
			return false;
		if (team == null) {
			if (other.team != null)
				return false;
		} else if (!team.equals(other.team))
			return false;
		if (teamName == null) {
			if (other.teamName != null)
				return false;
		} else if (!teamName.equals(other.teamName))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	
	
}
