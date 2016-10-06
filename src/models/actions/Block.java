package models.actions;

import java.util.ArrayList;

import models.Player;
import models.Square;
import models.Team;
import models.dice.DiceFace;


public class Block {
	
	private Player attacker;
	private Player defender;
	private Team selectTeam;
	private Push push;
	private DiceFace result;
	private Square followUpSquare;
	
	public Block(Player attacker, Player defender, Team selectTeam) {
		super();
		this.attacker = attacker;
		this.defender = defender;
		this.selectTeam = selectTeam;
		this.push = null;
	}

	public Player getAttacker() {
		return attacker;
	}

	public void setAttacker(Player attacker) {
		this.attacker = attacker;
	}

	public Player getDefender() {
		return defender;
	}

	public void setDefender(Player defender) {
		this.defender = defender;
	}
	
	public Team getSelectTeam(){
		return selectTeam;
	}

	public Push getPush() {
		return push;
	}

	public void setPush(Push push) {
		this.push = push;
		this.followUpSquare = push.getTo();
	}
	
	public ArrayList<Square> getCurrentPushSquares(){
		
		Push currentPush = getCurrentPush();
		
		if (currentPush != null){
			return currentPush.getPushSquares();
		}
		
		return new ArrayList<Square>(); 

	}

	public Push getCurrentPush() {
		
		if (push == null){
			return null;
		}
		
		return getEndPush(push);
	}
	
	private Push getEndPush(Push p){
		
		if (p.getFollowingPush() == null){
			return p;
		}
		
		return getEndPush(p.getFollowingPush());
		
	}
	
	private Push getSecondEndPush(Push p){
		
		if (p.getFollowingPush() == null || p.getFollowingPush().getFollowingPush() == null){
			return p;
		}
		
		return getSecondEndPush(p.getFollowingPush());
		
	}

	public void removeCurrentPush() {
		
		if (push == null){
			return;
		}
		
		if (push.getFollowingPush() == null){
			push = null;
			return;
		}
		
		getSecondEndPush(push).setFollowingPush(null);
		
	}

	public int getNumberOfPushes() {
		if (push == null){
			return 0;
		}
		
		return countPushes(push);
	}
	
	private int countPushes(Push p){
		
		if (p.getFollowingPush() == null){
			return 1;
		}
		
		return 1 + countPushes(p.getFollowingPush());
		
	}

	public boolean isAmongPlayers(Player player) {
		if (push == null){
			return false;
		}
	
		return amongPlayers(player, push);
	}
	
	private boolean amongPlayers(Player player, Push p){

		if (p.getPushedPlayer() == player){
			return true;
		}
		
		if (p.getFollowingPush() == null){
			return false;
		}
		
		return amongPlayers(player, p.getFollowingPush());
	}

	public void setResult(DiceFace face) {
		this.result = face;
		
	}

	public DiceFace getResult() {
		return result;
	}

	public Square getFollowUpSquare() {
		return followUpSquare;
	}

	public void setFollowUpSquare(Square followUpSquare) {
		this.followUpSquare = followUpSquare;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attacker == null) ? 0 : attacker.hashCode());
		result = prime * result
				+ ((defender == null) ? 0 : defender.hashCode());
		result = prime * result
				+ ((followUpSquare == null) ? 0 : followUpSquare.hashCode());
		result = prime * result + ((push == null) ? 0 : push.hashCode());
		result = prime * result
				+ ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result
				+ ((selectTeam == null) ? 0 : selectTeam.hashCode());
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
		Block other = (Block) obj;
		if (attacker == null) {
			if (other.attacker != null)
				return false;
		} else if (!attacker.equals(other.attacker))
			return false;
		if (defender == null) {
			if (other.defender != null)
				return false;
		} else if (!defender.equals(other.defender))
			return false;
		if (followUpSquare == null) {
			if (other.followUpSquare != null)
				return false;
		} else if (!followUpSquare.equals(other.followUpSquare))
			return false;
		if (push == null) {
			if (other.push != null)
				return false;
		} else if (!push.equals(other.push))
			return false;
		if (result != other.result)
			return false;
		if (selectTeam == null) {
			if (other.selectTeam != null)
				return false;
		} else if (!selectTeam.equals(other.selectTeam))
			return false;
		return true;
	}
	
	
	
}
