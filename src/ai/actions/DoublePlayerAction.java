package ai.actions;

import models.Player;

public abstract class DoublePlayerAction implements Action {

	public abstract Player getPlayerA();
	public abstract Player getPlayerB();
	
	public abstract void setPlayerA(Player player);
	public abstract void setPlayerB(Player player);
	
}
