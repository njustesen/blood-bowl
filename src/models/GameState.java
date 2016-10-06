package models;
import game.GameLog;
import models.actions.Block;
import models.actions.Catch;
import models.actions.Dodge;
import models.actions.Foul;
import models.actions.GoingForIt;
import models.actions.HandOff;
import models.actions.Pass;
import models.actions.PickUp;
import models.dice.DiceRoll;

public class GameState {
	
	private Team homeTeam;
	private Team awayTeam;
	private Pitch pitch;
	private int half;
	private int awayTurn;
	private int homeTurn;
	private boolean refAgainstHomeTeam;
	private boolean refAgainstAwayTeam;
	private Weather weather;
	private boolean gust;
	private DiceRoll currentDiceRoll;
	private CoinToss coinToss;
	private Team kickingTeam;
	private Team receivingTeam;
	private GameStage gameStage;
	private Block currentBlock;
	private Dodge currentDodge;
	private GoingForIt currentGoingForIt;
	private PickUp currentPickUp;
	private Catch currentCatch;
	private Pass currentPass;
	private HandOff currentHandOff;
	private Foul currentFoul;
	private boolean awaitReroll;
	private boolean playerPlaced;
	private boolean awaitPush;
	private boolean awaitFollowUp;
	
	public GameState(Team homeTeam, Team awayTeam, Pitch pitch) {
		super();
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.pitch = pitch;
		this.half = 1;
		this.awayTurn = 0;
		this.homeTurn = 0;
		this.refAgainstAwayTeam = true;
		this.refAgainstHomeTeam = true;
		this.gameStage = GameStage.START_UP;
		this.weather = Weather.NICE;
		this.gust = false;
		this.coinToss = new CoinToss();
		this.currentDodge = null;
		this.awaitReroll = false;
		this.awaitPush = false;
		this.awaitFollowUp = false;
		this.currentGoingForIt = null;
		this.currentPickUp = null;
		this.currentCatch = null;
		this.currentPass = null;
		this.currentDiceRoll = null;
		this.currentFoul = null;
		this.playerPlaced = false;
	}
	
	public Pitch getPitch() {
		return pitch;
	}

	public void setPitch(Pitch pitch) {
		this.pitch = pitch;
	}

	public int getHalf() {
		return half;
	}

	public void setHalf(int half) {
		this.half = half;
	}

	public int getAwayTurn() {
		return awayTurn;
	}

	public void setAwayTurn(int awayTurn) {
		this.awayTurn = awayTurn;
	}

	public int getHomeTurn() {
		return homeTurn;
	}

	public void setHomeTurn(int homeTurn) {
		this.homeTurn = homeTurn;
	}

	public boolean isRefAgainstHomeTeam() {
		return refAgainstHomeTeam;
	}

	public void setRefAgainstHomeTeam(boolean refAgainstHomeTeam) {
		this.refAgainstHomeTeam = refAgainstHomeTeam;
	}

	public boolean isRefAgainstAwayTeam() {
		return refAgainstAwayTeam;
	}

	public void setRefAgainstAwayTeam(boolean refAgainstAwayTeam) {
		this.refAgainstAwayTeam = refAgainstAwayTeam;
	}

	public Weather getWeather() {
		return weather;
	}

	public void setWeather(Weather weather) {
		
		this.weather = weather;
	}
	
	public boolean isGust() {
		return gust;
	}

	public void setGust(boolean gust) {
		this.gust = gust;
	}

	public CoinToss getCoinToss() {
		return coinToss;
	}

	public void setCoinToss(CoinToss coinToss) {
		this.coinToss = coinToss;
	}

	public GameStage getGameStage() {
		return gameStage;
	}

	public void setGameStage(GameStage gameStage) {
		this.gameStage = gameStage;
	}

	public Team getHomeTeam() {
		return homeTeam;
	}

	public Team getAwayTeam() {
		return awayTeam;
	}

	public Team getKickingTeam() {
		return kickingTeam;
	}

	public void setKickingTeam(Team kickingTeam) {
		this.kickingTeam = kickingTeam;
	}

	public Team getReceivingTeam() {
		return receivingTeam;
	}

	public void setReceivingTeam(Team receivingTeam) {
		this.receivingTeam = receivingTeam;
	}

	public DiceRoll getCurrentDiceRoll() {
		return currentDiceRoll;
	}

	public Block getCurrentBlock() {
		return currentBlock;
	}

	public void setCurrentBlock(Block currentBlock) {
		this.currentBlock = currentBlock;
	}

	public void setCurrentDiceRoll(DiceRoll diceRoll) {
		this.currentDiceRoll = diceRoll;
	}

	public void incAwayTurn() {
		awayTurn++;
		
	}
	
	public void incHomeTurn() {
		homeTurn++;
		
	}

	public void setCurrentDodge(Dodge dodge) {
		this.currentDodge = dodge;
		
	}
	
	public Dodge getCurrentDodge(){
		
		return this.currentDodge;
	}

	public void setAwaitReroll(boolean b) {
		this.awaitReroll = b;
		
	}
	
	public boolean isAwaitingReroll(){
		return this.awaitReroll;
	}

	public void setCurrentGoingForIt(GoingForIt goingForIt) {
		this.currentGoingForIt = goingForIt;
	}
	
	public GoingForIt getCurrentGoingForIt(){
		
		return currentGoingForIt;
	}

	public void setCurrentPickUp(PickUp pickUp) {
		this.currentPickUp = pickUp;
		
	}
	
	public PickUp getCurrentPickUp(){
		return currentPickUp;
	}

	public void setCurrentCatch(Catch c) {
		this.currentCatch = c;
		
	}
	
	public Catch getCurrentCatch(){
		
		return currentCatch;
	}

	public boolean isPlayerPlaced() {
		return playerPlaced;
	}

	public void setPlayerPlaced(boolean playerPlaced) {
		this.playerPlaced = playerPlaced;
	}

	public void setAwaitPush(boolean b) {
		awaitPush = b;
		
	}
	
	public boolean isAwaitingPush(){
		
		return awaitPush;
	}

	public void setAwaitFollowUp(boolean b) {
		this.awaitFollowUp = b;
		
	}

	public boolean isAwaitingFollowUp() {
		return awaitFollowUp;
	}

	public Pass getCurrentPass() {
		return currentPass;
	}

	public void setCurrentPass(Pass currentPass) {
		this.currentPass = currentPass;
	}

	public HandOff getCurrentHandOff() {
		return currentHandOff;
	}

	public void setCurrentHandOff(HandOff handOff) {
		this.currentHandOff = handOff;
	}

	public Foul getCurrentFoul() {
		return currentFoul;
	}

	public void setCurrentFoul(Foul currentFoul) {
		this.currentFoul = currentFoul;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (awaitFollowUp ? 1231 : 1237);
		result = prime * result + (awaitPush ? 1231 : 1237);
		result = prime * result + (awaitReroll ? 1231 : 1237);
		result = prime * result
				+ ((awayTeam == null) ? 0 : awayTeam.hashCode());
		result = prime * result + awayTurn;
		result = prime * result
				+ ((coinToss == null) ? 0 : coinToss.hashCode());
		result = prime * result
				+ ((currentBlock == null) ? 0 : currentBlock.hashCode());
		result = prime * result
				+ ((currentCatch == null) ? 0 : currentCatch.hashCode());
		result = prime * result
				+ ((currentDiceRoll == null) ? 0 : currentDiceRoll.hashCode());
		result = prime * result
				+ ((currentDodge == null) ? 0 : currentDodge.hashCode());
		result = prime * result
				+ ((currentFoul == null) ? 0 : currentFoul.hashCode());
		result = prime
				* result
				+ ((currentGoingForIt == null) ? 0 : currentGoingForIt
						.hashCode());
		result = prime * result
				+ ((currentHandOff == null) ? 0 : currentHandOff.hashCode());
		result = prime * result
				+ ((currentPass == null) ? 0 : currentPass.hashCode());
		result = prime * result
				+ ((currentPickUp == null) ? 0 : currentPickUp.hashCode());
		result = prime * result
				+ ((gameStage == null) ? 0 : gameStage.hashCode());
		result = prime * result + (gust ? 1231 : 1237);
		result = prime * result + half;
		result = prime * result
				+ ((homeTeam == null) ? 0 : homeTeam.hashCode());
		result = prime * result + homeTurn;
		result = prime * result
				+ ((kickingTeam == null) ? 0 : kickingTeam.hashCode());
		result = prime * result + ((pitch == null) ? 0 : pitch.hashCode());
		result = prime * result + (playerPlaced ? 1231 : 1237);
		result = prime * result
				+ ((receivingTeam == null) ? 0 : receivingTeam.hashCode());
		result = prime * result + (refAgainstAwayTeam ? 1231 : 1237);
		result = prime * result + (refAgainstHomeTeam ? 1231 : 1237);
		result = prime * result + ((weather == null) ? 0 : weather.hashCode());
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
		GameState other = (GameState) obj;
		if (awaitFollowUp != other.awaitFollowUp)
			return false;
		if (awaitPush != other.awaitPush)
			return false;
		if (awaitReroll != other.awaitReroll)
			return false;
		if (awayTeam == null) {
			if (other.awayTeam != null)
				return false;
		} else if (!awayTeam.equals(other.awayTeam))
			return false;
		if (awayTurn != other.awayTurn)
			return false;
		if (coinToss == null) {
			if (other.coinToss != null)
				return false;
		} else if (!coinToss.equals(other.coinToss))
			return false;
		if (currentBlock == null) {
			if (other.currentBlock != null)
				return false;
		} else if (!currentBlock.equals(other.currentBlock))
			return false;
		if (currentCatch == null) {
			if (other.currentCatch != null)
				return false;
		} else if (!currentCatch.equals(other.currentCatch))
			return false;
		if (currentDiceRoll == null) {
			if (other.currentDiceRoll != null)
				return false;
		} else if (!currentDiceRoll.equals(other.currentDiceRoll))
			return false;
		if (currentDodge == null) {
			if (other.currentDodge != null)
				return false;
		} else if (!currentDodge.equals(other.currentDodge))
			return false;
		if (currentFoul == null) {
			if (other.currentFoul != null)
				return false;
		} else if (!currentFoul.equals(other.currentFoul))
			return false;
		if (currentGoingForIt == null) {
			if (other.currentGoingForIt != null)
				return false;
		} else if (!currentGoingForIt.equals(other.currentGoingForIt))
			return false;
		if (currentHandOff == null) {
			if (other.currentHandOff != null)
				return false;
		} else if (!currentHandOff.equals(other.currentHandOff))
			return false;
		if (currentPass == null) {
			if (other.currentPass != null)
				return false;
		} else if (!currentPass.equals(other.currentPass))
			return false;
		if (currentPickUp == null) {
			if (other.currentPickUp != null)
				return false;
		} else if (!currentPickUp.equals(other.currentPickUp))
			return false;
		if (gameStage != other.gameStage)
			return false;
		if (gust != other.gust)
			return false;
		if (half != other.half)
			return false;
		if (homeTeam == null) {
			if (other.homeTeam != null)
				return false;
		} else if (!homeTeam.equals(other.homeTeam))
			return false;
		if (homeTurn != other.homeTurn)
			return false;
		if (kickingTeam == null) {
			if (other.kickingTeam != null)
				return false;
		} else if (!kickingTeam.equals(other.kickingTeam))
			return false;
		if (pitch == null) {
			if (other.pitch != null)
				return false;
		} else if (!pitch.equals(other.pitch))
			return false;
		if (playerPlaced != other.playerPlaced)
			return false;
		if (receivingTeam == null) {
			if (other.receivingTeam != null)
				return false;
		} else if (!receivingTeam.equals(other.receivingTeam))
			return false;
		if (refAgainstAwayTeam != other.refAgainstAwayTeam)
			return false;
		if (refAgainstHomeTeam != other.refAgainstHomeTeam)
			return false;
		if (weather != other.weather)
			return false;
		return true;
	}
	
}