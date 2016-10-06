package ai.actions;

import models.Player;

public abstract class PlayerAction implements Action {

	public abstract Player getPlayer();
	public abstract void setPlayer(Player player);
	
}
