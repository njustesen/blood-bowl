package ai.actions;

import models.Player;

public class FoulPlayerAction extends DoublePlayerAction {

	Player fouler;
	Player enemy;
	
	public FoulPlayerAction(Player fouler, Player enemy) {
		super();
		this.fouler = fouler;
		this.enemy = enemy;
	}

	public Player getFouler() {
		return fouler;
	}

	public void setFouler(Player fouler) {
		this.fouler = fouler;
	}

	public Player getEnemy() {
		return enemy;
	}

	public void setEnemy(Player enemy) {
		this.enemy = enemy;
	}

	@Override
	public Player getPlayerA() {
		return fouler;
	}

	@Override
	public Player getPlayerB() {
		return enemy;
	}

	@Override
	public void setPlayerA(Player player) {
		fouler = player;
	}

	@Override
	public void setPlayerB(Player player) {
		enemy = player;
	}
	
}
