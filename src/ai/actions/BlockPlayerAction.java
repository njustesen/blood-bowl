package ai.actions;

import models.Player;


public class BlockPlayerAction extends DoublePlayerAction {

	Player attacker;
	Player defender;
	
	public BlockPlayerAction(Player attacker, Player defender) {
		super();
		this.attacker = attacker;
		this.defender = defender;
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

	@Override
	public Player getPlayerA() {
		return attacker;
	}

	@Override
	public Player getPlayerB() {
		return defender;
	}

	@Override
	public void setPlayerA(Player player) {
		attacker = player;
	}

	@Override
	public void setPlayerB(Player player) {
		defender = player;
	}
	
	
	
}
