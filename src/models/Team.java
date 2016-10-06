package models;

import java.util.ArrayList;

public class Team {
	
	protected ArrayList<Player> players;
	protected int rerolls;
	protected int fanFactor;
	protected int assistantCoaches;
	protected int cheerleaders;
	protected TeamStatus teamStatus;
	protected String teamName;
	
	public Team(ArrayList<Player> players, int rerolls,
			int fanFactor, int assistantCoaches, String teamName) {
		super();
		this.players = players;
		this.rerolls = rerolls;
		this.fanFactor = fanFactor;
		this.assistantCoaches = assistantCoaches;
		this.teamStatus = new TeamStatus(4);
		this.teamName = teamName;
		this.cheerleaders = 0;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public int getRerolls() {
		return rerolls;
	}

	public String getTeamName(){
		return teamName;
	}
	public void setRerolls(int rerolls) {
		this.rerolls = rerolls;
	}

	public int getFanFactor() {
		return fanFactor;
	}

	public void setFanFactor(int fanFactor) {
		this.fanFactor = fanFactor;
	}

	public int getAssistantCoaches() {
		return assistantCoaches;
	}

	public void setAssistantCoaches(int assistantCoaches) {
		this.assistantCoaches = assistantCoaches;
	}

	public int getCheerleaders() {
		return cheerleaders;
	}

	public void setCheerleaders(int cheerleaders) {
		this.cheerleaders = cheerleaders;
	}

	public TeamStatus getTeamStatus() {
		return teamStatus;
	}

	public void setTeamStatus(TeamStatus gameStatus) {
		this.teamStatus = gameStatus;
	}

	public void useReroll() {
		this.teamStatus.setRerolledThisTurn(true);
		this.teamStatus.setRerolls(this.teamStatus.getRerolls() - 1);
		
	}

	public void reset() {
		teamStatus.reset();
		this.teamStatus.setRerolls(rerolls);
		
	}

	public Player getPlayer(int number) {
		for (Player p : players){
			if (number == p.getNumber())
				return p;
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + assistantCoaches;
		result = prime * result + cheerleaders;
		result = prime * result + fanFactor;
		result = prime * result + ((players == null) ? 0 : players.hashCode());
		result = prime * result + rerolls;
		result = prime * result
				+ ((teamName == null) ? 0 : teamName.hashCode());
		result = prime * result
				+ ((teamStatus == null) ? 0 : teamStatus.hashCode());
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
		Team other = (Team) obj;
		if (assistantCoaches != other.assistantCoaches)
			return false;
		if (cheerleaders != other.cheerleaders)
			return false;
		if (fanFactor != other.fanFactor)
			return false;
		if (players == null) {
			if (other.players != null)
				return false;
		} else if (!playersEquals(other))
			return false;
		if (rerolls != other.rerolls)
			return false;
		if (teamName == null) {
			if (other.teamName != null)
				return false;
		} else if (!teamName.equals(other.teamName))
			return false;
		if (teamStatus == null) {
			if (other.teamStatus != null)
				return false;
		} else if (!teamStatus.equals(other.teamStatus))
			return false;
		return true;
	}

	private boolean playersEquals(Team other) {
		for(Player p : other.getPlayers()){
			if (!players.contains(p)){
				return false;
			}
		}
		return true;
	}
	
}
