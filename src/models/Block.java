package models;


public class Block {
	
	Player attacker;
	Player defender;
	
	public Block(Player attacker, Player defender) {
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

}
