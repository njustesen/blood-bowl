package game;

import java.util.ArrayList;
import java.util.Date;

import Statistics.StatisticManager;
import ai.AIAgent;
import ai.actions.Action;
import ai.actions.BlockPlayerAction;
import ai.actions.DoublePlayerAction;
import ai.actions.EndPhaseAction;
import ai.actions.EndPlayerTurnAction;
import ai.actions.EndSetupAction;
import ai.actions.FollowUpAction;
import ai.actions.FoulPlayerAction;
import ai.actions.HandOffPlayerAction;
import ai.actions.PlayerAction;
import ai.actions.SelectInterceptionAction;
import ai.actions.MovePlayerAction;
import ai.actions.PassPlayerAction;
import ai.actions.PlaceBallAction;
import ai.actions.PlaceBallOnPlayerAction;
import ai.actions.PlacePlayerAction;
import ai.actions.RerollAction;
import ai.actions.SelectCoinSideAction;
import ai.actions.SelectCoinTossEffectAction;
import ai.actions.SelectDieAction;
import ai.actions.SelectPlayerAction;
import ai.actions.SelectPlayerTurnAction;
import ai.actions.SelectPushSquareAction;
import ai.actions.StandPlayerUpAction;
import ai.util.GameStateCloner;

import sound.Sound;
import sound.SoundManager;
import models.BlockSum;
import models.GameStage;
import models.GameState;
import models.PassRange;
import models.Pitch;
import models.Player;
import models.PlayerTurn;
import models.RangeRuler;
import models.Skill;
import models.Square;
import models.Standing;
import models.Team;
import models.TeamFactory;
import models.Weather;
import models.actions.Block;
import models.actions.Catch;
import models.actions.Dodge;
import models.actions.GoingForIt;
import models.actions.HandOff;
import models.actions.Pass;
import models.actions.PickUp;
import models.actions.Push;
import models.dice.BB;
import models.dice.D3;
import models.dice.D6;
import models.dice.D8;
import models.dice.DiceFace;
import models.dice.DiceRoll;
import models.dice.IDice;

public class GameMaster {
	
	private static final boolean AUTO_SETUP = true;
	private GameState state;
	private Player selectedPlayer;
	private Player blockTarget;
	private SoundManager soundManager;
	private Player passTarget;
	private Player handOffTarget;
	private AIAgent homeAgent;
	private AIAgent awayAgent;
	private Player foulTarget;
	private Date time;
	private boolean restart = false;
	private boolean rerollsAllowed = false;
	private boolean fast = false;
	private boolean logging = false;
	
	public GameMaster(GameState gameState, AIAgent homeAgent, AIAgent awayAgent, boolean fast, boolean restart) {
		super();
		this.state = gameState;
		this.homeAgent = homeAgent;
		this.awayAgent = awayAgent;
		this.fast  = fast;
		this.restart = restart;
	}
	
	public void setSoundManager(SoundManager soundManager){
		
		this.soundManager = soundManager;
		
	}
	
	public void enableLogging(){
		
		this.logging  = true;
		
	}
	
	public void update(){
		
		if (!fast){
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (state.getGameStage() == GameStage.START_UP){
			StatisticManager.stopped = false;
			StatisticManager.games++;
			startGame();
			return;
		}
		if (state.getGameStage() == GameStage.GAME_ENDED && restart){
			if (state.getHomeTeam().getTeamStatus().getScore() == state.getAwayTeam().getTeamStatus().getScore()){
				StatisticManager.draws++;
				if (state.getHomeTeam().getTeamStatus().getScore() == 0){
					StatisticManager.zerozero++;
				}
			} else if (state.getHomeTeam().getTeamStatus().getScore() > state.getAwayTeam().getTeamStatus().getScore()){
				StatisticManager.homeWon++;
			} else {
				StatisticManager.awayWon++;
			}
			StatisticManager.print(time);
			StatisticManager.stop();
			Team home = TeamFactory.getHumanTeam();
			Team away = TeamFactory.getOrcTeam();
			Pitch pitch = new Pitch(home, away);
			state = new GameState(home, away, pitch);
		}
		
		long time = System.nanoTime();
		
		boolean home = false;
		
		if (state.getGameStage() == GameStage.HOME_TURN){
			home = true;
			if (state.getCurrentPass() != null &&
					state.getCurrentPass().getInterceptionPlayers() != null &&
					state.getCurrentPass().getInterceptionPlayers().size() > 0){
				home = false;
			}
			if (state.isAwaitingReroll() && state.getCurrentDiceRoll() != null && state.getCurrentBlock() != null && state.getCurrentGoingForIt() == null){
				if (!state.isAwaitingFollowUp() && !state.isAwaitingPush()){
					home = (state.getHomeTeam() == state.getCurrentBlock().getSelectTeam());
				}
			}
		} else if (state.getGameStage() == GameStage.AWAY_TURN){
			home = false;
			if (state.getCurrentPass() != null &&
					state.getCurrentPass().getInterceptionPlayers() != null &&
					state.getCurrentPass().getInterceptionPlayers().size() > 0){
				home = true;
			}
			if (state.isAwaitingReroll() && state.getCurrentDiceRoll() != null && state.getCurrentBlock() != null && state.getCurrentGoingForIt() == null){
				if (!state.isAwaitingFollowUp() && !state.isAwaitingPush()){
					home = (state.getHomeTeam() == state.getCurrentBlock().getSelectTeam());
				}
			}
		} else if (state.getGameStage() == GameStage.COIN_TOSS){
			home = false;
		} else if (state.getGameStage() == GameStage.PICK_COIN_TOSS_EFFECT){
			if (state.getCoinToss().hasAwayPickedHeads() == state.getCoinToss().isResultHeads()){
				home = false;
			} else {
				home = true;
			}
		} else if (state.getGameStage() == GameStage.KICK_OFF){
			startNewTurn();
		} else if (state.getGameStage() == GameStage.KICKING_SETUP){
			if (state.getKickingTeam() == state.getHomeTeam()){
				home = true;
			} else if (state.getKickingTeam() == state.getAwayTeam()){
				home = false;
			}
		} else if (state.getGameStage() == GameStage.RECEIVING_SETUP){
			if (state.getReceivingTeam() == state.getHomeTeam()){
				home = true;
			} else if (state.getReceivingTeam() == state.getAwayTeam()){
				home = false;
			}
		} else if (state.getGameStage() == GameStage.KICK_PLACEMENT){
			if (state.getKickingTeam() == state.getHomeTeam()){
				home = true;
			} else if (state.getKickingTeam() == state.getAwayTeam()){
				home = false;
			}
		} else if (state.getGameStage() == GameStage.PLACE_BALL_ON_PLAYER){
			if (state.getReceivingTeam() == state.getHomeTeam()){
				home = true;
			} else if (state.getReceivingTeam() == state.getAwayTeam()){
				home = false;
			}
		} else if (state.getGameStage() == GameStage.BLITZ){
			if (state.getKickingTeam() == state.getHomeTeam()){
				home = true;
			} else if (state.getKickingTeam() == state.getAwayTeam()){
				home = false;
			}
		} else if (state.getGameStage() == GameStage.QUICK_SNAP){
			if (state.getReceivingTeam() == state.getHomeTeam()){
				home = true;
			} else if (state.getReceivingTeam() == state.getAwayTeam()){
				home = false;
			}
		} else if (state.getGameStage() == GameStage.HIGH_KICK){
			if (state.getReceivingTeam() == state.getHomeTeam()){
				home = true;
			} else if (state.getReceivingTeam() == state.getAwayTeam()){
				home = false;
			}
		} else if (state.getGameStage() == GameStage.PERFECT_DEFENSE){
			if (state.getKickingTeam() == state.getHomeTeam()){
				home = true;
			} else if (state.getKickingTeam() == state.getAwayTeam()){
				home = false;
			}
		}
		
		StatisticManager.timeSpendByGameMaster += System.nanoTime() - time;
		
		long agentTime = System.nanoTime();
		Action action = null;
		//GameState clone = new GameStateCloner().clone(state);
		GameState clone = state;
		if (home && homeAgent != null){
			action = homeAgent.takeAction(this, clone);
		} else if (!home && awayAgent != null){
			action = awayAgent.takeAction(this, clone);
		}
		StatisticManager.timeSpendByAIAgent += System.nanoTime() - agentTime;
		performAIAction(action);
		
	}
	
	public void performAIAction(Action action) {

		if (action == null)
			return;

		long time = System.nanoTime();
		
		// Extract players
		Player playerA = null;
		Player playerB = null;
		
		if (action instanceof PlayerAction){
			playerA = identifyPlayer(((PlayerAction) action).getPlayer());
		} else if (action instanceof DoublePlayerAction){
			playerA = identifyPlayer(((DoublePlayerAction) action).getPlayerA());
			playerB = identifyPlayer(((DoublePlayerAction) action).getPlayerB());
		}
		
		if(action instanceof RerollAction){
			
			reroll();
			
		} else if(action instanceof SelectDieAction){
			
			selectDie(((SelectDieAction) action).getDie());
			
		} else if(action instanceof PlacePlayerAction){
			
			placePlayerIfAllowed(playerA, ((PlacePlayerAction)action).getSquare());
			
		} else if(action instanceof SelectPlayerAction){
			
			selectedPlayer = playerA;
			
			if (state.getGameStage() == GameStage.HIGH_KICK){
				
				endPhase();
				
			}
			
		} else if(action instanceof SelectPlayerTurnAction){
			
			selectedPlayer = playerA;
			
			selectAction(((SelectPlayerTurnAction) action).getTurn());
			
		} else if(action instanceof PlaceBallOnPlayerAction){
			
			Square square = playerA.getPosition();
			state.getPitch().getBall().setSquare(square);
			state.getPitch().getBall().setOnGround(true);
			state.getPitch().getBall().setUnderControl(true);
			endPhase();
			
		} else if(action instanceof MovePlayerAction){
			
			movePlayerIfAllowed(playerA, ((MovePlayerAction) action).getSquare());
			
		} else if(action instanceof StandPlayerUpAction){
			
			standPlayerUpIfAllowed(playerA);
			
		} else if(action instanceof BlockPlayerAction){
			
			blockTarget = playerB;
			performBlock(playerA, playerB);
			
		} else if(action instanceof PassPlayerAction){
			
			passTarget = playerB;
			performPass(playerA, playerB);
			
		} else if(action instanceof HandOffPlayerAction){
			
			handOffTarget = playerB;
			performHandOff(playerA, playerB);
			
		} else if(action instanceof FoulPlayerAction){

			foulTarget = playerB;
			performFoul(playerA, playerB);
			
		} else if(action instanceof FollowUpAction){

			followUp(((FollowUpAction) action).isFollowUp());
			
		} else if(action instanceof SelectPushSquareAction){
			
			Square from = state.getCurrentBlock().getCurrentPush().getFrom();
			
			pushToSquare(from, ((SelectPushSquareAction) action).getSquare());
			
		} else if(action instanceof EndPlayerTurnAction){
			
			playerA.getPlayerStatus().setTurn(PlayerTurn.USED);
			
		} else if(action instanceof EndPhaseAction){
			
			endPhase();
			
		} else if(action instanceof SelectInterceptionAction){
			
			continueInterception(playerA);
			
		} else if(action instanceof SelectCoinSideAction){
			
			pickCoinSide(((SelectCoinSideAction)action).isHeads());
			
		} else if(action instanceof SelectCoinTossEffectAction){
			
			pickCoinTossEffect(((SelectCoinTossEffectAction)action).isReceive());
			
		} else if(action instanceof EndSetupAction){
			
			endSetup();
			
		} else if(action instanceof PlaceBallAction){
			
			placeBall(((PlaceBallAction)action).getSquare());
			
			endPhase();
			
		}
		
		StatisticManager.timeSpendByGameMaster += System.nanoTime() - time;
		
	}

	private Player identifyPlayer(Player player) {
		
		String teamName = player.getTeamName();
		Team team = null;
		
		if (teamName.equals(state.getHomeTeam().getTeamName())){
			team = state.getHomeTeam();
		} else if (teamName.equals(state.getAwayTeam().getTeamName())){
			team = state.getAwayTeam();
		}
		
		return team.getPlayer(player.getNumber());
	}

	/**
	 * Ends the current phase.
	 * Should only be called during the following phases:
	 * - KICKING_SETUP
	 * - RECEIVING_SETUP
	 * - HOME_TURN
	 * - AWAY_TURN
	 * - KICK_PLACEMENT
	 * - QUICK_SNAP
	 */
	public void endPhase(){
		
		// Check if reroll?
		if (state.isAwaitingReroll()){
			if (logging)
				GameLog.push("You cannot end your turn during a dice roll.");
			return;
		}
		
		switch(state.getGameStage()){
			case KICKING_SETUP : endSetup(); break;
			case RECEIVING_SETUP : endSetup(); break;
			case HOME_TURN : endTurn(); break;
			case AWAY_TURN : endTurn(); break;
			case KICK_PLACEMENT : kickBall(); break;
			case BLITZ : endTurn(); break;
			case QUICK_SNAP : endTurn(); break;
			case HIGH_KICK : endTurn(); break;
			case PERFECT_DEFENSE : endTurn(); break;
			case PLACE_BALL_ON_PLAYER : endTurn(); break;
			default:
			return;
		}
		
	}
	
	/**
	 * A square was clicked.
	 * @param square
	 */
	public void squareClicked(Square square) {
		
		// Awaiting follow up?
		if (state.isAwaitingFollowUp() && 
				state.getCurrentBlock() != null){
			
			Square legalSquare = state.getCurrentBlock().getFollowUpSquare();
			Square attackerSquare = state.getCurrentBlock().getAttacker().getPosition();
			
			if ((square.getX() == legalSquare.getX() && square.getY() == legalSquare.getY())){
				
				followUp(true);
				
			} else if (square.getX() == attackerSquare.getX() && square.getY() == attackerSquare.getY()){
			
				followUp(false);
				
			}
			
			return;
			
		}
		
		// Awaiting push?
		if (state.isAwaitingPush() && 
				state.getCurrentBlock() != null){
			
			if (state.getCurrentBlock().getCurrentPush().isAmongSquares(square)){
				
				Square from = state.getCurrentBlock().getCurrentPush().getTo();
				
				pushToSquare(from, square);
				
			}	
			
			return;
			
		}
		
		// Kick placement?
		if (state.getGameStage() == GameStage.KICK_PLACEMENT){
			placeBall(square);
			return;
		}
		
		Player player = state.getPitch().getPlayerAt(square);
		
		// Player?
		if (player != null){
			
			if (state.getGameStage() == GameStage.PLACE_BALL_ON_PLAYER){
				placeBallOnPlayer(square);
				return;
			}
			
			// Selected player?
			if (player == selectedPlayer){
				
				// Stand player up
				if (getMovingTeam() == playerOwner(player) && 
						player.getPlayerStatus().getStanding() == Standing.DOWN && 
						player.getPlayerStatus().getTurn() == PlayerTurn.UNUSED){
					
					player.getPlayerStatus().setStanding(Standing.UP);
					player.getPlayerStatus().setTurn(PlayerTurn.MOVE_ACTION);
					endTurnForOtherPlayers(playerOwner(player), player);
					
				} else {
				
					// Remove selection
					selectedPlayer = null;
				
				}
				
			} else {
				
				if (selectedPlayer != null){
				
					// Block?
					if (allowedToBlock(selectedPlayer) && 
							onDifferentTeams(selectedPlayer, player) &&
							selectedPlayer.getPlayerStatus().getStanding() == Standing.UP && 
							player.getPlayerStatus().getStanding() == Standing.UP && 
							nextToEachOther(selectedPlayer, player) &&
							state.getCurrentBlock() == null){
						
						performBlock(selectedPlayer, player);
						return;
						
					}
					
					// Blitz from ground?
					if (allowedToBlock(selectedPlayer) && 
							onDifferentTeams(selectedPlayer, player) &&
							selectedPlayer.getPlayerStatus().getStanding() == Standing.DOWN && 
							player.getPlayerStatus().getStanding() == Standing.UP && 
							nextToEachOther(selectedPlayer, player) &&
							state.getCurrentBlock() == null && 
							selectedPlayer.getPlayerStatus().getTurn() == PlayerTurn.BLITZ_ACTION){
						
						performBlock(selectedPlayer, player);
						return;
						
					}
					
					// Foul?
					if (onDifferentTeams(selectedPlayer, player) &&
							selectedPlayer.getPlayerStatus().getStanding() == Standing.UP && 
							player.getPlayerStatus().getStanding() != Standing.UP && 
							nextToEachOther(selectedPlayer, player) &&
							state.getCurrentFoul() == null){
						
						performFoul(selectedPlayer, player);
						return;
						
					}
					
					// Interception?
					if (state.getCurrentPass() != null &&
							state.getCurrentPass().isAwaitingInterception() &&
							state.getCurrentPass().getInterceptionPlayers().contains(player)){
						
						continueInterception(player);
						
						return;
					}
					
					// Pass?
					if (selectedPlayer.getPlayerStatus().getTurn() == PlayerTurn.PASS_ACTION && 
							!onDifferentTeams(selectedPlayer, player) &&
							isBallCarried(selectedPlayer) && 
							player.getPlayerStatus().getStanding() == Standing.UP){
						
						performPass(selectedPlayer, player);
						return;
					}
					
					// HandOff?
					if (selectedPlayer.getPlayerStatus().getTurn() == PlayerTurn.HAND_OFF_ACTION && 
							!onDifferentTeams(selectedPlayer, player) && 
							isBallCarried(selectedPlayer) && 
							nextToEachOther(selectedPlayer, player) && 
							player.getPlayerStatus().getStanding() == Standing.UP){
						
						performHandOff(selectedPlayer, player);
						return;
					}
					
				}
				
				// Select player
				selectedPlayer = player;
				
			} 
			
		} else {
			
			// Clicked on square
			emptySquareClicked(square.getX(),square.getY());
			
		}
		
	}

	/**
	 * An empty square on the pitch was clicked.
	 * @param x
	 * @param y
	 */
	public void emptySquareClicked(int x, int y) {
		
		Square square = new Square(x, y);
		//Player clickedPlayer = state.getPitch().getPlayerArr()[square.getY()][square.getX()];
		
		if (state.getGameStage() == GameStage.KICKING_SETUP){
			
			if (selectedPlayer != null && getPlayerOwner(selectedPlayer) == state.getKickingTeam()){
				
				// Places player if allowed to
				placePlayerIfAllowed(selectedPlayer, square);
				
			}
			
		} else if (state.getGameStage() == GameStage.RECEIVING_SETUP){
		
			if (selectedPlayer != null && getPlayerOwner(selectedPlayer) == state.getReceivingTeam()){
				
				// Places player if allowed to
				placePlayerIfAllowed(selectedPlayer, square);
				
			}
			
		} else if (state.getGameStage() == GameStage.HOME_TURN || 
				state.getGameStage() == GameStage.AWAY_TURN){
			
			if (selectedPlayer != null && getPlayerOwner(selectedPlayer) == getMovingTeam()){
				
				// Moves player if allowed to
				movePlayerIfAllowed(selectedPlayer, square);
				
			}
			
		} else if (state.getGameStage() == GameStage.BLITZ){
			
			if (selectedPlayer != null && getPlayerOwner(selectedPlayer) == state.getKickingTeam()){
				
				// Moves player if allowed to
				movePlayerIfAllowed(selectedPlayer, square);
				
			}
			
		} else if (state.getGameStage() == GameStage.QUICK_SNAP){
			
			if (selectedPlayer != null && getPlayerOwner(selectedPlayer) == state.getReceivingTeam()){
				
				// Moves player if allowed to
				movePlayerIfAllowed(selectedPlayer, square);
			}
			
		} else if (state.getGameStage() == GameStage.PERFECT_DEFENSE){
			
			if (selectedPlayer != null && getPlayerOwner(selectedPlayer) == state.getKickingTeam()){
				
				// Moves player if allowed to
				placePlayerIfAllowed(selectedPlayer, square);
				
			}
			
		}
		
	}
	
	public void selectAwayReserve(int reserve) {
		
		if (reserve >= state.getPitch().getAwayDogout().getReserves().size()){

			if (selectedPlayer != null){
				
				if (state.getGameStage() == GameStage.KICKING_SETUP && 
						state.getKickingTeam() == state.getAwayTeam()){
					
					movePlayerToReserves(selectedPlayer, false);
					
				} else if (state.getGameStage() == GameStage.RECEIVING_SETUP && 
						state.getReceivingTeam() == state.getAwayTeam()){
					
					movePlayerToReserves(selectedPlayer, false);
					
				}
				
			}
			
		} else if (selectedPlayer != state.getPitch().getAwayDogout().getReserves().get(reserve)){
			
			Player clicked = state.getPitch().getAwayDogout().getReserves().get(reserve);
			
			if (clicked.getPlayerStatus().getStanding() == Standing.UP){
				selectedPlayer = clicked;
			}
		
		} else {
			
			selectedPlayer = null;
			
		}
		
	}

	public void selectHomeReserve(int reserve) {
		
		if (reserve >= state.getPitch().getHomeDogout().getReserves().size()){
			
			if (selectedPlayer != null){
				
				if (state.getGameStage() == GameStage.KICKING_SETUP && 
						state.getKickingTeam() == state.getHomeTeam()){
					
					movePlayerToReserves(selectedPlayer, true);
					
				} else if (state.getGameStage() == GameStage.RECEIVING_SETUP && 
						state.getReceivingTeam() == state.getHomeTeam()){
					
					movePlayerToReserves(selectedPlayer, true);
					
				}
				
			}
			
		} else if (selectedPlayer != state.getPitch().getHomeDogout().getReserves().get(reserve)){
			
			Player clicked = state.getPitch().getHomeDogout().getReserves().get(reserve);
			
			if (clicked.getPlayerStatus().getStanding() == Standing.UP){
				selectedPlayer = clicked;
			}
			
		}  else {
			
			selectedPlayer = null;
			
		}
		
	}

	/**
	 * Start the game!
	 * This will initiate the coin toss game stage.
	 */
	public void startGame(){
		
		// Legal action?
		if (state.getGameStage() == GameStage.START_UP){
			
			time = new Date();
			
			// Roll for fans and FAME
			rollForFans();
			
			// Roll for weather
			rollForWeather();
			
			// Go to coin toss
			state.setGameStage(GameStage.COIN_TOSS);
			
			// Move all players to reserve
			state.getPitch().getHomeDogout().putPlayersInReserves();
			state.getPitch().getAwayDogout().putPlayersInReserves();
			
			if (logging)
				GameLog.push("The game has started!");
			
		}
		
	}

	/**
	 * Away team picks a coin side.
	 * @param heads
	 * 		True if heads, false if tails.
	 */
	public void pickCoinSide(boolean heads){
		
		// Legal action?
		if (state.getGameStage() == GameStage.COIN_TOSS){
			
			// Set coin side pick
			state.getCoinToss().setAwayPickedHeads(heads);
			
			if (heads){
				if (logging)
					GameLog.push(state.getAwayTeam().getTeamName() + " picked heads.");
			} else {
				if (logging)
					GameLog.push(state.getAwayTeam().getTeamName() + " picked tails.");
			}
			
			// Toss the coin
			state.getCoinToss().Toss();
			
			if (state.getCoinToss().isResultHeads() == state.getCoinToss().hasAwayPickedHeads()){
				if (logging)
					GameLog.push(state.getAwayTeam().getTeamName() + " won the coin toss and will select to kick or receive.");
			} else {
				if (logging)
					GameLog.push(state.getHomeTeam().getTeamName() + " won the coin toss and will select to kick or receive.");
			}
			
			// Go to pick coin toss effect
			state.setGameStage(GameStage.PICK_COIN_TOSS_EFFECT);
			
		}
		
	}
	
	/**
	 * Coin toss winner picks coin toss effect.
	 * @param receive
	 * 		True if winner receives, false if winner kicks.
	 */
	public void pickCoinTossEffect(boolean receive){
		
		// Legal action?
		if (state.getGameStage() == GameStage.PICK_COIN_TOSS_EFFECT){
			
			// If away picked correct
			if (state.getCoinToss().hasAwayPickedHeads() == state.getCoinToss().isResultHeads()){
				
				// Away chooses to receive or kick
				state.getCoinToss().setHomeReceives(!receive);
				if (receive){
					state.setReceivingTeam(state.getAwayTeam());
					state.setKickingTeam(state.getHomeTeam());
					if (logging){
						GameLog.push(state.getAwayTeam().getTeamName() + " selected to recieve the ball.");
						GameLog.push(state.getHomeTeam().getTeamName() + " sets up first.");
					}
				} else {
					state.setKickingTeam(state.getAwayTeam());
					state.setReceivingTeam(state.getHomeTeam());
					if (logging){
						GameLog.push(state.getAwayTeam().getTeamName() + " selected to kick the ball.");
						GameLog.push(state.getAwayTeam().getTeamName() + " sets up first.");
					}
				}
				
			} else {
				
				// Away chooses to receive or kick
				state.getCoinToss().setHomeReceives(receive);
				if (receive){
					state.setReceivingTeam(state.getHomeTeam());
					state.setKickingTeam(state.getAwayTeam());
					if (logging){
						GameLog.push(state.getHomeTeam().getTeamName() + " selected to receive the ball.");
						GameLog.push(state.getAwayTeam().getTeamName() + " sets up first.");
					}
				} else {
					state.setKickingTeam(state.getHomeTeam());
					state.setReceivingTeam(state.getAwayTeam());
					if (logging){
						GameLog.push(state.getHomeTeam().getTeamName() + " selected to kick the ball.");
						GameLog.push(state.getHomeTeam().getTeamName() + " sets up first.");
					}
				}
				
			}
			
			state.setGameStage(GameStage.KICKING_SETUP);
			
		}
		
	}
	
	/**
	 * A team is done setting up.
	 * If the team is the kicking team, the next game stage will be receiving setup.
	 * If the team is the receiving team, the next game stage will be kick placement.
	 */
	public void endSetup(){
		
		if (state.getGameStage() == GameStage.KICKING_SETUP){
			
			// Auto setup
			if (state.getPitch().teamPlayersOnPitch(state.getKickingTeam()) == 0 &&
					AUTO_SETUP){
				
				setupTeam(state.getKickingTeam());
				
				return;
			}
			
			// Kicking team	
			if (state.getPitch().isSetupLegal(state.getKickingTeam(), state.getHalf())){
				
				state.setGameStage(GameStage.RECEIVING_SETUP);
				selectedPlayer = null;
				
				if (logging){
					GameLog.push(state.getKickingTeam().getTeamName() + " is done setting up.");
					GameLog.push(state.getReceivingTeam().getTeamName() + " now has to setup.");
				}
				
			}
			
		} else if (state.getGameStage() == GameStage.RECEIVING_SETUP){
			
			// Auto setup
			if (state.getPitch().teamPlayersOnPitch(state.getReceivingTeam()) == 0 && 
					 AUTO_SETUP){
				
				setupTeam(state.getReceivingTeam());
				return;
			}
			
			// Receiving team	
			if (state.getPitch().isSetupLegal(state.getReceivingTeam(), state.getHalf())){
					
				state.setGameStage(GameStage.KICK_PLACEMENT);
				selectedPlayer = null;
				
				if (logging){
					GameLog.push(state.getReceivingTeam().getTeamName() + " is done setting up.");
					GameLog.push(state.getKickingTeam().getTeamName() + " now has to place the ball.");
				}
				
			}
			
		}
		
	}

	/**
	 * Place a player on a square.
	 * 
	 * @param player
	 * @param square
	 */
	public void placePlayerIfAllowed(Player player, Square square){
		
		Team team = getPlayerOwner(player);
		boolean moveAllowed = false;
		
		// Setting up?
		if (state.getGameStage() == GameStage.KICKING_SETUP &&
				state.getKickingTeam() == team){
			
			moveAllowed = true;
			
		} else if (state.getGameStage() == GameStage.RECEIVING_SETUP &&
				state.getReceivingTeam() == team){
			
			moveAllowed = true;
			
		} else if (state.getGameStage() == GameStage.PERFECT_DEFENSE &&
				state.getKickingTeam() == team){
			
			moveAllowed = true;
			
		} else if (state.getGameStage() == GameStage.HIGH_KICK &&
				!state.isPlayerPlaced() && 
				state.getReceivingTeam() == team){
			
			moveAllowed = true;
			
		}
		
		// Square occupied?
		if (state.getPitch().getPlayerAt(square) != null){
			
			moveAllowed = false;
			
		}
		
		if (moveAllowed){
			removePlayerFromReserves(player);
			removePlayerFromCurrentSquare(player);
			placePlayerAt(player, square);
			
			if (state.getGameStage() == GameStage.HIGH_KICK){
				state.setPlayerPlaced(true);
			}
		}
		
	}
	
	public void performHandOff(Player passer, Player catcher) {
		
		if (passer.getPlayerStatus().getTurn() != PlayerTurn.HAND_OFF_ACTION)
			return;
			
		if (onDifferentTeams(passer, catcher))
			return;
		
		if (state.getCurrentHandOff() != null)
			return;
		
		if (!isBallCarried(passer))
			return;
		
		if (!nextToEachOther(passer, catcher))
			return;
			
		if (handOffTarget == null){
			handOffTarget = catcher;
			return;
		}
		handOffTarget = null;
		
		playerOwner(passer).getTeamStatus().setHasHandedOf(true);
		
		state.setCurrentHandOff(new HandOff(passer, catcher));
		
		if (logging)
			GameLog.push("Ball handed off.");
		
		state.getPitch().getBall().setSquare(catcher.getPosition());
		passer.getPlayerStatus().setTurn(PlayerTurn.USED);
		catchBall();
		state.setCurrentHandOff(null);
	}
	
	public void performPass(Player passer, Player catcher) {

		if (passer.getPlayerStatus().getTurn() != PlayerTurn.PASS_ACTION)
			return;
			
		if (onDifferentTeams(passer, catcher))
			return;
		
		if (state.getCurrentPass() != null)
			return;
		
		if (!isBallCarried(passer))
			return;
		
		if (passingRange(passer, catcher) == PassRange.OUT_OF_RANGE)
			return;
			
		if (passTarget == null){
			passTarget = catcher;
			return;
		}
		
		passTarget = null;
		
		playerOwner(passer).getTeamStatus().setHasPassed(true);
		int success = getPassSuccessRoll(passer, passingRange(passer, catcher));
		state.setCurrentPass(new Pass(passer, catcher, success));
		state.getPitch().getBall().setUnderControl(false);
		
		ArrayList<Player> interceptionPlayers = state.getPitch().interceptionPlayers(state.getCurrentPass());
		
		if (interceptionPlayers.size() != 0){
			state.getCurrentPass().setAwaitingInterception(true);
			state.getCurrentPass().setInterceptionPlayers(interceptionPlayers);
			return;
		}
		
		DiceRoll roll = new DiceRoll();
		D6 d = new D6();
		d.roll();
		roll.addDice(d);
		state.setCurrentDiceRoll(roll);
		int result = d.getResultAsInt();
		continuePass(result, false);
		
	}
	
	private void continueInterception(Player player) {
		
		if (!state.getCurrentPass().getInterceptionPlayers().contains(player)){
			
		}
		
		DiceRoll roll = new DiceRoll();
		D6 d = new D6();
		d.roll();
		roll.addDice(d);
		state.setCurrentDiceRoll(roll);
		int result = d.getResultAsInt();
		
		int zones = numberOfTackleZones(player);
		int success = 7 - player.getAG() + zones + 2;
		success = Math.max( 2, Math.min(6, success) );
		
		if (result >= success){
			
			state.getPitch().getBall().setSquare(player.getPosition());
			state.getPitch().getBall().setUnderControl(true);
			state.setCurrentPass(null);
			if (logging)
				GameLog.push("Successfull interception. Result: " + result + " (" + success + " was needed");
			endTurn();
			
		} else {
			
			if (logging)
				GameLog.push("Failed interception. Result: " + result + " (" + success + " was needed");
			state.getCurrentPass().setAwaitingInterception(false);
			state.getCurrentPass().setInterceptionPlayers(null);
			
			d.roll();
			result = d.getResultAsInt();
			continuePass(result, false);
			
		}
		
	}

	private void continuePass(int result, boolean execute) {
		
		state.setAwaitReroll(false);
		
		Player passer = state.getCurrentPass().getPasser();
		Player catcher = state.getCurrentPass().getCatcher();
		int success = state.getCurrentPass().getSuccess();
		
		PassRange range = passingRange(passer, catcher);
		String rangeStr = range.getName();
		char[] stringArray = rangeStr.toCharArray();
		stringArray[0] = Character.toUpperCase(stringArray[0]);
		rangeStr = new String(stringArray);
		
		if (result >= success){
			
			if (logging)
				GameLog.push(rangeStr + " succeded! Result: " + result + " (" + success + " was needed).");
			
			state.getPitch().getBall().setSquare(catcher.getPosition());
			state.getCurrentPass().setAccurate(true);
			catchBall();
			
			return;
			
		} else {
			
			if (logging)
				GameLog.push("Failed " + rangeStr + "! Result: " + result + " (" + success + " was needed).");
			
			if (passer.getSkills().contains(Skill.PASS)){
				 
				DiceRoll roll = new DiceRoll();
				roll.addDice(new D6());
				state.setCurrentDiceRoll(roll);
				state.getCurrentDiceRoll().getDices().get(0).roll();
				result = state.getCurrentDiceRoll().getDices().get(0).getResultAsInt();
				state.setCurrentPass(new Pass(passer, catcher, success));
				
				if (result >= success){
					
					if (logging)
						GameLog.push(rangeStr + " succeded! Result: " + result + " (" + success + " was needed).");
					
					Square newSquare = catcher.getPosition();
					state.getPitch().getBall().setSquare(newSquare);
					state.getCurrentPass().setAccurate(true);
					catchBall();
					
					return;
					
				}
				
			} else if (!execute && ableToReroll(getPlayerOwner(passer))){
				
				// Prepare for reroll usage
				state.setCurrentPass(new Pass(passer, catcher, success));
				state.setAwaitReroll(true);
				return;
				
			}
			
		}
		
		// FAIL
		if (result == 1){
			// Fumble
			scatterBall();
			endTurn();
			
		} else {
			state.getCurrentPass().setAccurate(false);
			state.getPitch().getBall().setSquare(catcher.getPosition());
			scatterPass();
			if (state.getPitch().getBall().isUnderControl() && state.getPitch().getBall().getSquare() != null){
				Player ballCarrier = state.getPitch().getPlayerAt(state.getPitch().getBall().getSquare());
				if (playerOwner(ballCarrier) != playerOwner(passer)){
					endTurn();
				}
			} else {
				endTurn();
			}
		}
		
	}
	
	private void scatterPass() {
		
		int scatters = 3;
		Square ballOn = state.getPitch().getBall().getSquare();
		if (ballOn == null)
			return;
		
		while(scatters > 0){
			int result = (int) (Math.random() * 8 + 1);
			
			switch (result){
			case 1 : ballOn = new Square(ballOn.getX() - 1, ballOn.getY() - 1); break;
			case 2 : ballOn = new Square(ballOn.getX(), ballOn.getY() - 1); break;
			case 3 : ballOn = new Square(ballOn.getX() + 1, ballOn.getY() - 1); break;
			case 4 : ballOn = new Square(ballOn.getX() - 1, ballOn.getY()); break;
			case 5 : ballOn = new Square(ballOn.getX() + 1, ballOn.getY()); break;
			case 6 : ballOn = new Square(ballOn.getX() + 1, ballOn.getY() + 1); break;
			case 7 : ballOn = new Square(ballOn.getX() + 1, ballOn.getY() + 1); break;
			case 8 : ballOn = new Square(ballOn.getX() + 1, ballOn.getY() + 1); break;
			}
			
			state.getPitch().getBall().setSquare(ballOn);
			scatters--;
			
			if (!state.getPitch().isBallInsidePitch()){
				//correctOOBBallPosition(moveX, moveY)
				break;
			}
			
		}
		
		Player player = state.getPitch().getPlayerAt(ballOn);
		
		// Land on player
		if (player != null){
			
			if (player.getPlayerStatus().getStanding() == Standing.UP)
				catchBall();
			else
				scatterBall();
			
			return;
		}
			
		if (!state.getPitch().isBallInsidePitch()){

			throwInBall();
			
		}
		
	}

	private int getPassSuccessRoll(Player passer, PassRange passingRange) {
		
		int zones = numberOfTackleZones(passer);
		int success = 7 - passer.getAG() + zones;
		success += passingRange.getModifier();
		if (state.getWeather() == Weather.VERY_SUNNY){
			success++;
		}
		return Math.max( 2, Math.min(6, success) );
		
	}

	public PassRange passingRange(Player passer, Player catcher) {
		
		Square a = passer.getPosition();
		Square b = catcher.getPosition();
		if (a == null || b == null)
			return PassRange.OUT_OF_RANGE;
		
		int x = (a.getX() - b.getX()) * (a.getX() - b.getX());
		int y = (a.getY() - b.getY()) * (a.getY() - b.getY());
		int distance = (int) Math.sqrt(x + y);
		PassRange range = RangeRuler.getPassRange(distance);
		if (state.getWeather() == Weather.BLIZZARD){
			if (range == PassRange.LONG_BOMB || range == PassRange.LONG_PASS){
				return PassRange.OUT_OF_RANGE;
			}
		}
		
		return range;
		
	}
	
	private void performFoul(Player fouler, Player target) {
			
		if (!onDifferentTeams(fouler, target))
			return;
		
		if (!nextToEachOther(fouler, target))
			return;
		
		if (target.getPlayerStatus().getStanding() == Standing.UP)
			return;
		
		if (state.getCurrentFoul() != null)
			return;
			
		if (playerOwner(fouler).getTeamStatus().hasFouled())
			return;
		
		if (foulTarget == null){
			foulTarget = target;
			return;
		}
		foulTarget = null;
		
		if (fouler.getPlayerStatus().getTurn() == PlayerTurn.UNUSED){
			fouler.getPlayerStatus().setTurn(PlayerTurn.FOUL_ACTION);
		}
		
		endTurnForOtherPlayers(playerOwner(fouler), fouler);
		
		playerOwner(fouler).getTeamStatus().setHasFouled(true);
		
		int foulSum = calculateFoulSum(fouler, target);
		
		soundManager.playSound(Sound.KNOCKEDDOWN);
		
		// Armour roll
		D6 da = new D6();
		D6 db = new D6();
		da.roll();
		db.roll();
		
		int result = da.getResultAsInt() + db.getResultAsInt() + foulSum;
		boolean knockedOut = false;
		boolean deadAndInjured = false;
		boolean sendOffField = false;
		
		if (da.getResultAsInt() == db.getResultAsInt()){
			sendOffField = true;
		}
		
		if (logging)
			GameLog.push("Foul! Armour roll: " + da.getResultAsInt() + " + " + db.getResultAsInt() + " (" + (target.getAV() + 1 - foulSum) + " needed)");
		
		if (result > target.getAV()){
			
			// Injury roll
			da.roll();
			db.roll();
			
			if (da.getResultAsInt() == db.getResultAsInt()){
				sendOffField = true;
			}
			
			result = da.getResultAsInt() + db.getResultAsInt();
			
			if (result < 8){
				
				// Stunned
				target.getPlayerStatus().setStanding(Standing.STUNNED);
				
				if (logging)
					GameLog.push("Foul! Player stunned!");
				
			} else if (result < 10){
				
				// Knocked out
				knockedOut = true;
				if (logging)
					GameLog.push("Foul! Player knocked out!");
				
			} else {
				
				// Dead and injured
				deadAndInjured = true;
				if (logging)
					GameLog.push("Foul! Player dead or injured!");
				
			}
			
		}
		
		if (knockedOut){
			
			target.getPlayerStatus().setStanding(Standing.UP);
			state.getPitch().removePlayer(target);
			state.getPitch().getDogout(getPlayerOwner(target)).getKnockedOut().add(target);
			
		} else if (deadAndInjured){
			
			target.getPlayerStatus().setStanding(Standing.UP);
			state.getPitch().removePlayer(target);
			state.getPitch().getDogout(getPlayerOwner(target)).getDeadAndInjured().add(target);
			
		}
		
		if (sendOffField){
			
			if (playerOwner(fouler) == state.getHomeTeam() && !state.isRefAgainstHomeTeam()){
				return;
			}
			if (playerOwner(fouler) == state.getAwayTeam() && !state.isRefAgainstAwayTeam()){
				return;
			}
			
			if (logging)
				GameLog.push("Foul! Player was sent off the field!");
			
			// Fumble
			boolean fumble = false;
			if (isBallCarried(fouler)){
				fumble = true;
			}
			
			state.getPitch().removePlayer(fouler);
			state.getPitch().getDungeoun().add(fouler);
			
			if (fumble){
				state.getPitch().getBall().setUnderControl(false);
				scatterBall();
			}
			
		}
		
	}

	/**
	 * Performs a block roll.
	 * 
	 * @param attacker
	 * @param defender
	 */
	public void performBlock(Player attacker, Player defender){
		
		if (!allowedToBlock(attacker))
			return;
			
		if (!onDifferentTeams(attacker, defender))
			return;
		
		if (!nextToEachOther(attacker, defender))
			return;
		
		if (state.getCurrentBlock() != null){
			/*
			if (!state.getCurrentBlock().getAttacker().getPlayerStatus().hasMovedToBlock()){
				return;
			}
			*/
			blockTarget = state.getCurrentBlock().getDefender();
		}
		
		if (blockTarget == null){
			blockTarget = defender;
			return;
		}
		blockTarget = null;
				
		if (attacker.getPlayerStatus().getTurn() == PlayerTurn.UNUSED){
			attacker.getPlayerStatus().setTurn(PlayerTurn.BLOCK_ACTION);
		}
		
		if (attacker.getPlayerStatus().getTurn() == PlayerTurn.BLITZ_ACTION && 
				attacker.getPlayerStatus().getStanding() == Standing.DOWN){
			attacker.getPlayerStatus().setStanding(Standing.UP);
			attacker.getPlayerStatus().useMovement(3);
		}
		
		endTurnForOtherPlayers(playerOwner(attacker), attacker);
		
		// Blitz?
		if (attacker.getPlayerStatus().getTurn() == PlayerTurn.BLITZ_ACTION){
			if (attacker.getPlayerStatus().getMovementUsed() >= attacker.getMA()){
				if (!attacker.getPlayerStatus().hasMovedToBlock()){
					state.setCurrentBlock(new Block(attacker, defender, null));
					goingForIt(attacker, attacker.getPosition());
					return;
				}
			} else {
				attacker.getPlayerStatus().moveOneSquare();
			}
		}
		
		DiceRoll roll = new DiceRoll();
		
		BlockSum sum = calculateBlockSum(attacker, defender);
		
		Team selectTeam = playerOwner(attacker);
		
		soundManager.playSound(Sound.DICEROLL);
		
		if (sum == BlockSum.EQUAL){
			
			BB ba = new BB();
			ba.roll();
			roll.addDice(ba);
			
		} else if(sum == BlockSum.ATTACKER_STRONGER){
			
			BB ba = new BB();
			BB bb = new BB();
			ba.roll();
			bb.roll();
			roll.addDice(ba);
			roll.addDice(bb);
			
			if (logging)
				GameLog.push("Attacker selects block die.");
			
		}  else if(sum == BlockSum.DEFENDER_STRONGER){
			
			BB ba = new BB();
			BB bb = new BB();
			ba.roll();
			bb.roll();
			roll.addDice(ba);
			roll.addDice(bb);
			
			selectTeam = playerOwner(defender);
			
			if (logging)
				GameLog.push("Defender selects block die.");
			
		} else if(sum == BlockSum.ATTACKER_DOUBLE_STRONG){
			
			BB ba = new BB();
			BB bb = new BB();
			BB bc = new BB();
			ba.roll();
			bb.roll();
			bc.roll();
			roll.addDice(ba);
			roll.addDice(bb);
			roll.addDice(bc);
			
			if (logging)
				GameLog.push("Attacker selects block die.");
			
		} else if(sum == BlockSum.DEFENDER_DOUBLE_STRONG){
			
			BB ba = new BB();
			BB bb = new BB();
			BB bc = new BB();
			ba.roll();
			bb.roll();
			bc.roll();
			roll.addDice(ba);
			roll.addDice(bb);
			roll.addDice(bc);
			
			if (logging)
				GameLog.push("Defender selects block die.");
			
		}
		
		state.setCurrentDiceRoll(roll);
		
		// Select or continue
		if (roll.getDices().size() == 1 && !ableToReroll(selectTeam)){
			state.setCurrentBlock(new Block(attacker, defender, selectTeam));
			continueBlock(roll.getFaces().get(0));
		} else {
			state.setCurrentBlock(new Block(attacker, defender, selectTeam));
			state.setAwaitReroll(true);
		}
		
	}
	
	/**
	 * Selects a rolled die.
	 * 
	 * @param i
	 * 		The index of the die in the dice roll.
	 */
	public void selectDie(int i){
		
		if (state.getCurrentDiceRoll() != null){
			
			// Select face
			DiceFace face = state.getCurrentDiceRoll().getFaces().get(i);
			int result = state.getCurrentDiceRoll().getDices().get(i).getResultAsInt();
			
			// Continue block/dodge/going/pass
			if (state.getCurrentBlock() != null && state.getCurrentBlock().getResult() == null){
				
				state.setAwaitReroll(false);
				state.setCurrentDiceRoll(null);
				continueBlock(face);
				return;
				
			}
			if (state.getCurrentPass() != null){
				
				state.setAwaitReroll(false);
				state.setCurrentDiceRoll(null);
				continuePass(result, true);
				return;
				
			}
			if (state.getCurrentPickUp() != null){
					
				state.setAwaitReroll(false);
				state.setCurrentDiceRoll(null);
				continuePickUp(result);
				return;
				
			}
			if (state.getCurrentCatch() != null){
				
				state.setAwaitReroll(false);
				state.setCurrentDiceRoll(null);
				continueCatch(result);
				return;
				
			}
			if (state.getCurrentDodge() != null){
				
				state.setAwaitReroll(false);
				state.setCurrentDiceRoll(null);
				
				// Player fall
				movePlayer(state.getCurrentDodge().getPlayer(), state.getCurrentDodge().getSquare(), true);
				//knockDown(state.getCurrentDodge().getPlayer(), true);
				
				return;
				
			}	
			if (state.getCurrentGoingForIt() != null){
				
				state.setAwaitReroll(false);
				
				// Player fall
				movePlayer(state.getCurrentGoingForIt().getPlayer(), state.getCurrentGoingForIt().getSquare(), true);
				//knockDown(state.getCurrentGoingForIt().getPlayer(), true);
				state.setCurrentDiceRoll(null);
				
				return;
				
			}	
			
		}
		
	}

	/**
	 * Rerolls the current dice roll.
	 */
	public void reroll(){
		
		// Anything to reroll?
		if (!state.isAwaitingReroll() || 
				state.getCurrentDiceRoll() == null || 
				!ableToReroll(getMovingTeam()))
			return;
		
		getMovingTeam().useReroll();
		
		for(IDice d : state.getCurrentDiceRoll().getDices()){
			d.roll();
		}
		
		soundManager.playSound(Sound.DICEROLL);
		
		// Dodge/going/block/pass
	 	if (state.getCurrentDodge() != null){
	 		
	 		state.setAwaitReroll(false);
			continueDodge(state.getCurrentDiceRoll().getDices().get(0).getResultAsInt());
			
		} else if (state.getCurrentGoingForIt() != null){
			
			state.setAwaitReroll(false);
			continueGoingForIt(state.getCurrentDiceRoll().getDices().get(0).getResultAsInt(), state.getCurrentGoingForIt().getSuccess());
			
		} else if (state.getCurrentBlock() != null){

			if (state.getCurrentDiceRoll().getDices().size() == 1){
				state.setAwaitReroll(false);
				continueBlock(state.getCurrentDiceRoll().getFaces().get(0));
			}
			
		} else if (state.getCurrentPickUp() != null){

			state.setAwaitReroll(false);
			continuePickUp(state.getCurrentDiceRoll().getDices().get(0).getResultAsInt());
			
		} else if (state.getCurrentCatch() != null){

			state.setAwaitReroll(false);
			continueCatch(state.getCurrentDiceRoll().getDices().get(0).getResultAsInt());
			
		} else if (state.getCurrentPass() != null){

			state.setAwaitReroll(false);
			continuePass(state.getCurrentDiceRoll().getDices().get(0).getResultAsInt(), true);
			
		}
		
	}

	/**
	 * Moves a player to a square if allowed. 
	 * 
	 * @param player
	 * @param square
	 */
	public void movePlayerIfAllowed(Player player, Square square){
		
		if (state.isAwaitingReroll())
			return;
		
		boolean moveAllowed = moveAllowed(player, square);
		
		if (!moveAllowed){
			return;
		}
		
		// Player turn
		if (player.getPlayerStatus().getTurn() == PlayerTurn.UNUSED){
			endTurnForOtherPlayers(playerOwner(player), player);
			player.getPlayerStatus().setTurn(PlayerTurn.MOVE_ACTION);
		}
		
		// Dodge
		if (isInTackleZone(player) && state.getGameStage() != GameStage.QUICK_SNAP){
			
			dodgeToMovePlayer(player, square);
			
		} else {
			
			// Move
			movePlayer(player, square, false);
			
		}
		
	}
	
	/**
	 * Stands a player up if allowed. 
	 * 
	 * @param player
	 * @param square
	 */
	public void standPlayerUpIfAllowed(Player player){
		
		if (state.isAwaitingReroll())
			return;
		
		boolean standUpAllowed = standUpAllowed(player);
		
		if (!standUpAllowed){
			return;
		}
		
		// Player turn
		if (player.getPlayerStatus().getTurn() == PlayerTurn.UNUSED){
			endTurnForOtherPlayers(playerOwner(player), player);
			player.getPlayerStatus().setTurn(PlayerTurn.MOVE_ACTION);
		}
		
		player.getPlayerStatus().setStanding(Standing.UP);
		
	}

	/**
	 * Selects an action to the selected player.
	 * @param action
	 */
	public void selectAction(PlayerTurn action){
		
		// Only if allowed
		if (selectedPlayer != null && 
				(playerOwner(selectedPlayer) == getMovingTeam() || 
					(state.getGameStage() == GameStage.QUICK_SNAP && playerOwner(selectedPlayer) == state.getReceivingTeam())
					) &&
				selectedPlayer.getPlayerStatus().getTurn() == PlayerTurn.UNUSED){
			
			endTurnForOtherPlayers(playerOwner(selectedPlayer), selectedPlayer);
			
			if (action == PlayerTurn.USED || 
					action == PlayerTurn.UNUSED || 
					selectedPlayer.getPlayerStatus().getStanding() == Standing.STUNNED){
				return;
			}
			
			if (action == PlayerTurn.BLITZ_ACTION && 
					playerOwner(selectedPlayer).getTeamStatus().hasBlitzed()){
				return;
			}
			
			if (action == PlayerTurn.FOUL_ACTION && 
					playerOwner(selectedPlayer).getTeamStatus().hasFouled()){
				return;
			}
			
			if (action == PlayerTurn.PASS_ACTION && 
					playerOwner(selectedPlayer).getTeamStatus().hasPassed()){
				return;
			}
			
			if (action == PlayerTurn.HAND_OFF_ACTION && 
					playerOwner(selectedPlayer).getTeamStatus().hasHandedOf()){
				return;
			}
			
			selectedPlayer.getPlayerStatus().setTurn(action);
			//endTurnForOtherPlayers(playerOwner(selectedPlayer), selectedPlayer);
			
		}
		
	}
	
	private void setupTeam(Team team) {
			
		ArrayList<Player> placablePlayers = new ArrayList<Player>();
		
		for (Player p : state.getPitch().getDogout(team).getReserves()){
			placablePlayers.add(p);
		}
		
		for (Player p : placablePlayers){
			
			// No more players in reserves
			if (state.getPitch().getDogout(team).getReserves().isEmpty()){
				break;
			}
			
			// Three on scrimmage?
			if (state.getPitch().playersOnScrimmage(team) < 3){
				
				state.getPitch().placePlayerOnScrimmage(p, team);
				
				continue;
				
			}
			
			// Two on top wide zones
			if (state.getPitch().playersOnTopWideZones(team) < 2){
				
				state.getPitch().placePlayerInTopWideZone(p, team);
				
				continue;
				
			}
			
			// Two on bottom wide zones
			if (state.getPitch().playersOnBottomWideZones(team) < 2){
				
				state.getPitch().placePlayerInBottomWideZone(p, team);
				
				continue;
				
			}
			
			// Place on rest of pitch
			if (state.getPitch().playersOnPitch(team) < 11){
				
				state.getPitch().placePlayerInMidfield(p, team);
				
				continue;
				
			}
			
		}
		
	}
	
	private void movePlayerToReserves(Player player, boolean home) {
		
		if (playerOwner(player) == state.getHomeTeam() && home){
			
			removePlayerFromCurrentSquare(player);
			
			removePlayerFromReserves(player);
			
			state.getPitch().getHomeDogout().getReserves().add(player);
			
			
		} else if (playerOwner(player) == state.getAwayTeam() && !home){
			
			removePlayerFromCurrentSquare(player);
			
			removePlayerFromReserves(player);
			
			state.getPitch().getAwayDogout().getReserves().add(player);
			
		}
		
	}
	
	private boolean onDifferentTeams(Player a, Player b) {
		if (playerOwner(a) != playerOwner(b)){
			return true;
		}
		return false;
	}
	
	private void followUp(boolean follow) {
		
		if (follow){
		
			// Move ball - TD?
			if (isBallCarried(state.getCurrentBlock().getAttacker())){
				
				// Move ball
				state.getPitch().getBall().setSquare(state.getCurrentBlock().getFollowUpSquare());
				
				if (state.getPitch().isBallInEndzone(oppositeTeam(playerOwner(state.getCurrentBlock().getAttacker())))){
					touchdown(playerOwner(state.getCurrentBlock().getAttacker()));
					return;
				}
			
			}
			
			removePlayerFromCurrentSquare(state.getCurrentBlock().getAttacker());
			placePlayerAt(state.getCurrentBlock().getAttacker(), state.getCurrentBlock().getFollowUpSquare());
			
		}
		
		state.setAwaitFollowUp(false);
		
		endPushingBlock();
		
	}

	private void endPush() {
		
		state.setAwaitPush(false);
		
		state.setAwaitFollowUp(true);
		
	}
	
	private void endPushingBlock(){
		
		if (state.getCurrentBlock().getResult() == DiceFace.DEFENDER_KNOCKED_DOWN){
			
			knockDown(state.getCurrentBlock().getDefender(), true);
			
		} else if (state.getCurrentBlock().getResult() == DiceFace.DEFENDER_STUMBLES){
			
			if (!state.getCurrentBlock().getDefender().getSkills().contains(Skill.DODGE)){
				
				knockDown(state.getCurrentBlock().getDefender(), true);
				
			}
		}
		
		if (state.getCurrentBlock().getAttacker().getPlayerStatus().getTurn() == PlayerTurn.BLITZ_ACTION){
			
			playerOwner(state.getCurrentBlock().getAttacker()).getTeamStatus().setHasBlitzed(true);
			
		} else {
			
			state.getCurrentBlock().getAttacker().getPlayerStatus().setTurn(PlayerTurn.USED);
			
			
			// Remove selection
			selectedPlayer = null;
			
		}
		
		state.setCurrentBlock(null);
		
	}

	private void pushToSquare(Square from, Square to) {
		
		if (state.getCurrentBlock().getCurrentPush().isAmongSquares(to)){
			
			Player player = state.getPitch().getPlayerAt(to);
			
			if (player != null && !state.getCurrentBlock().isAmongPlayers(player)){
				
				Push push = new Push(player, from, to);
				
				push.setPushSquares(eliminatedPushSquares(push));
				
				state.getCurrentBlock().getCurrentPush().setFollowingPush(push);
				
			} else {
				
				performPush(to);
				state.setAwaitPush(false);
				
			}
			
		}
		
		if (state.getCurrentBlock().getCurrentPush() == null){
			endPush();
			return;
		}
		
	}

	private void performPush(Square to) {
		
		boolean scatterBall = false;
		boolean throwIn = false;
		
		while(state.getCurrentBlock().getCurrentPush() != null){
			
			Player player = state.getCurrentBlock().getCurrentPush().getPushedPlayer();
			
			// Move ball - TD?
			if (isBallCarried(player)){
				
				// Move ball
				state.getPitch().getBall().setSquare(to);
				
				if (state.getPitch().isBallInEndzone(oppositeTeam(playerOwner(player)))){
					touchdown(playerOwner(player));
					return;
				}
			
			}
			
			Square before = player.getPosition();
			removePlayerFromCurrentSquare(player);
			placePlayerAt(player, to);
			
			// Scatter ball?
			if (state.getPitch().getBall().getSquare() != null &&
					state.getPitch().getBall().getSquare().getX() == to.getX() && 
					state.getPitch().getBall().getSquare().getY() == to.getY() && 
					!state.getPitch().getBall().isUnderControl() && 
					state.getPitch().getBall().isOnGround()){
				
				scatterBall = true;
				
			}
			
			// Out of bounds?
			if (!state.getPitch().isOnPitch(to)){
				
				if (isBallCarried(player)){
					state.getPitch().getBall().setUnderControl(false);
					throwIn = true;
				}
				
				knockDown(player, false);
				
				if (player.getPlayerStatus().getStanding() == Standing.STUNNED || player.getPlayerStatus().getStanding() == Standing.DOWN){
					player.getPlayerStatus().setStanding(Standing.UP);
					removePlayerFromCurrentSquare(player);
					movePlayerToReserves(player, (playerOwner(player) == state.getHomeTeam()));
				}
				
			}
			
			to = before;
			state.getCurrentBlock().removeCurrentPush();
			
		}
		
		if (throwIn){
			throwInBall();
			return;
		}
		
		if (scatterBall){
			scatterBall();
		}
		
	}

	private void placeBallOnPlayer(Square square) {
		
		Player player = state.getPitch().getPlayerAt(square);
		
		// Correct team?
		if (playerOwner(player) == state.getReceivingTeam()){
			
			// Place ball
			state.getPitch().getBall().setSquare(square);
			state.getPitch().getBall().setOnGround(true);
			state.getPitch().getBall().setUnderControl(true);
			
			// End phase
			endTurn();
			
		}
		
	}
	
	private void placePlayerUnderBall(Player player) {
		
		Square ballOn = state.getPitch().getBall().getSquare();
		
		if (ballOn == null)
			return;
			
		removePlayerFromCurrentSquare(player);
		movePlayerToSquare(player, ballOn);
		
		state.getPitch().getBall().setOnGround(false);
		state.getPitch().getBall().setUnderControl(false);
		
	}

	private void endTurnForOtherPlayers(Team team, Player player) {
		
		for(Player p : team.getPlayers()){
			if (p.getPlayerStatus().getTurn() != PlayerTurn.UNUSED){
				if (p != player){
					if (p.getPlayerStatus().getTurn() == PlayerTurn.BLITZ_ACTION){
						playerOwner(p).getTeamStatus().setHasBlitzed(true);
					} else if (p.getPlayerStatus().getTurn() == PlayerTurn.PASS_ACTION){
						playerOwner(p).getTeamStatus().setHasPassed(true);
					} else if (p.getPlayerStatus().getTurn() == PlayerTurn.HAND_OFF_ACTION){
						playerOwner(p).getTeamStatus().setHasHandedOf(true);
					} else if (p.getPlayerStatus().getTurn() == PlayerTurn.FOUL_ACTION){
						playerOwner(p).getTeamStatus().setHasFouled(true);
					}
					p.getPlayerStatus().setTurn(PlayerTurn.USED);
				}
			}
		}
		
	}
	
	private void rollForFans() {
		
		soundManager.playSound(Sound.CHEER);
		
		// Fans
		D6 a = new D6();
		D6 b = new D6();
		a.roll();
		b.roll();
		int homeFans = 1000 * (a.getResultAsInt() + b.getResultAsInt() + state.getHomeTeam().getFanFactor());
		a.roll();
		b.roll();
		int awayFans = 1000 * (a.getResultAsInt() + b.getResultAsInt() + state.getAwayTeam().getFanFactor());
		
		state.getHomeTeam().getTeamStatus().setFans(homeFans);
		state.getAwayTeam().getTeamStatus().setFans(awayFans);
		
		// FAME
		if (homeFans <= awayFans){
			state.getHomeTeam().getTeamStatus().setFAME(0);
		}
		if (awayFans <= homeFans){
			state.getAwayTeam().getTeamStatus().setFAME(0);
		}
		
		if (homeFans > awayFans){
			state.getHomeTeam().getTeamStatus().setFAME(1);
		}
		if (awayFans > homeFans){
			state.getAwayTeam().getTeamStatus().setFAME(1);
		}
		
		if (homeFans > awayFans){
			state.getHomeTeam().getTeamStatus().setFAME(1);
		}
		if (awayFans > homeFans){
			state.getAwayTeam().getTeamStatus().setFAME(1);
		}
		
		if (homeFans >= awayFans * 2){
			state.getHomeTeam().getTeamStatus().setFAME(2);
		}
		if (awayFans >= homeFans * 2){
			state.getAwayTeam().getTeamStatus().setFAME(2);
		}
		
	}
	
	private void continueGoingForIt(int result, int success) {
		
		Player player = state.getCurrentGoingForIt().getPlayer();
		Square square = state.getCurrentGoingForIt().getSquare();
		state.setCurrentGoingForIt(null);
		
		if (result > success){
			
			if (logging)
				GameLog.push("Succeeded going for it! Result: " + result + " (" + success + " was needed).");
			
			// Blitz?
			if (player.getPosition() != null && 
					player.getPosition().equals(square) && 
					state.getCurrentBlock() != null && 
					state.getCurrentBlock().getAttacker() == player){
				player.getPlayerStatus().setMovedToBlock(true);
				performBlock(player, state.getCurrentBlock().getDefender());
				return;
			}
			
			// Dodge or move
			if (isInTackleZone(player) && 
					state.getGameStage() != GameStage.QUICK_SNAP){
				
				dodgeToMovePlayer(player, square);
				
			} else {
				
				// Move
				movePlayer(player, square, false);
				
			}
			
		} else {
			
			if (logging)
				GameLog.push("Failed going for it! Result: " + result + " (" + success + " was needed).");
			movePlayer(player, square, true);
			//knockDown(player, true);
			
		}
		
	}

	private void continueDodge(int result) {
		
		performDodge(state.getCurrentDodge().getPlayer(), 
				state.getCurrentDodge().getSquare(), 
				result, 
				state.getCurrentDodge().getSuccess());
		
	}

	private void dodgeToMovePlayer(Player player, Square square) {

		int zones = numberOfTackleZones(player);
		
		int success = getDodgeSuccesRoll(player, zones);
		
		DiceRoll roll = new DiceRoll();
		D6 d = new D6();
		roll.addDice(d);
		d.roll();
		int result = d.getResultAsInt();
		soundManager.playSound(Sound.DICEROLL);
		
		state.setCurrentDiceRoll(roll);
		
		performDodge(player, square, result, success);
		
	}

	private void performDodge(Player player, Square square, int result, int success) {
		
		state.setCurrentDodge(null);
		
		// Success?
		if (result == 6 || (result != 1 && result >= success)){
			
			if (logging)
				GameLog.push("Succeeded dodge! Result: " + result + " (" + success + " was needed).");
			
			// Move
			movePlayer(player, square, false);
			
		} else {
			
			if (logging)
				GameLog.push("Failed dodge! Result: " + result + " (" + success + " was needed).");
			
			// Dodge skill
			if (player.getSkills().contains(Skill.DODGE)){
				
				DiceRoll dr = new DiceRoll();
				D6 d = new D6();
				d.roll();
				result = d.getResultAsInt();
				state.setCurrentDiceRoll(dr);
				soundManager.playSound(Sound.DICEROLL);
				
				if (result == 6 || (result != 1 && result >= success)){
					
					if (logging)
						GameLog.push("Succeeded dodge - using Dodge skill! Result: " + result + " (" + success + " was needed).");
				
					// Move
					movePlayer(player, square, false);
					return;
				
				} else {
					
					if (logging)
						GameLog.push("Failed dodge - using Dodge skill! Result: " + result + " (" + success + " was needed).");
					
				}
				
			} else if (ableToReroll(getPlayerOwner(player))){
				
				// Prepare for reroll usage
				if (logging)
					GameLog.push("Failed dodge! Result: " + result + " (" + success + " was needed).");
				state.setCurrentDodge(new Dodge(player, square, success));
				state.setAwaitReroll(true);
				return;
				
			}
			
			// Player fall
			movePlayer(player, square, true);
			
		}
		
	}

	private void continueCatch(int result) {
		
		int success = state.getCurrentCatch().getSuccess();
		
		Player player = state.getCurrentCatch().getPlayer();
		
		state.setCurrentCatch(null);
		state.setAwaitReroll(false);
		
		if (result == 6 || 
				(result != 1 && result >= success)){
			
			state.getPitch().getBall().setUnderControl(true);
			
			if (logging)
				GameLog.push("Succeeded catch! Result: " + result + " (" + success + " was needed).");
			
			// Touchdown
			if (state.getPitch().isBallInEndzone(oppositeTeam(playerOwner(player)))){
				touchdown(playerOwner(player));
			}
			
		} else { 
				
			// FAIL
			boolean endTurn = false;
			if (state.getCurrentPass() != null){
				endTurn = true;
				state.setCurrentPass(null);
			}
			
			scatterBall();
			
			if (endTurn)
				endTurn();
			
		}
		
	}

	private void continuePickUp(int result) {
		
		int success = state.getCurrentPickUp().getSuccess();
		
		Player player = state.getCurrentPickUp().getPlayer();
		
		state.setCurrentPickUp(null);
		state.setAwaitReroll(false);
		
		if (result == 6 || 
				(result != 1 && result >= success)){
			
			state.getPitch().getBall().setUnderControl(true);
			
			if (logging)
				GameLog.push("Succeeded pick up! Result: " + result + " (" + success + " was needed).");
			
			// Touchdown
			if (state.getPitch().isBallInEndzone(oppositeTeam(playerOwner(player)))){
				touchdown(playerOwner(player));
			}
			
		} else { 
				
			scatterBall();
			
			endTurn();
			
		}
		
	}

	private boolean ableToReroll(Team team) {
		
		if (!rerollsAllowed)
			return false;
		
		if (state.getGameStage() == GameStage.HOME_TURN){
			
			if (team != state.getHomeTeam()){
				return false;
			}
			
		} else if (state.getGameStage() == GameStage.AWAY_TURN){
			
			if (team != state.getAwayTeam()){
				return false;
			}
			
		} else {
			
			return false;
			
		}
		
		if (team.getTeamStatus().getRerolls() > 0 &&
				!team.getTeamStatus().rerolledThisTurn()){
			
			return true;
			
		}
		
		return false;
		
		
	}

	private int getDodgeSuccesRoll(Player player, int zones) {
		
		int roll = 6 - player.getAG() + zones;
		
		return Math.max( 2, Math.min(6, roll) );
	}

	private int numberOfTackleZones(Player player){
		int num = 0;
		
		for(int y = -1; y <= 1; y++){
			for(int x = -1; x <= 1; x++){
				
				Square test = new Square(player.getPosition().getX() + x, player.getPosition().getY() + y);
				
				Player p = state.getPitch().getPlayerAt(test); 
				
				// Opposite team and up
				if (p != null &&
						getPlayerOwner(p) != getPlayerOwner(player)){
					
					if (p.getPlayerStatus().getStanding() == Standing.UP){
						num++;
					}
					
				}
			}
		}
		
		return num;
		
	}

	private boolean isInTackleZone(Player player) {
		
		for(int y = -1; y <= 1; y++){
			for(int x = -1; x <= 1; x++){
				
				Square test = new Square(player.getPosition().getX() + x, player.getPosition().getY() + y);
				
				Player p = state.getPitch().getPlayerAt(test); 
				
				// Opposite team?
				if (p != null &&
						getPlayerOwner(p) != getPlayerOwner(player)){
					
					if (p.getPlayerStatus().getStanding() == Standing.UP){
						return true;
					}
					
					
				}
			}
		}
		
		return false;
		
	}

	private void movePlayer(Player player, Square square, boolean falling) {

		// Use movement
		if (player.getPlayerStatus().getStanding() == Standing.DOWN){
			
			player.getPlayerStatus().useMovement(3 + 1);
			player.getPlayerStatus().setStanding(Standing.UP);
			
		} else {
			
			player.getPlayerStatus().useMovement(1);

		}
		
		if (isBallCarried(player)){
			
			// Move player
			removePlayerFromCurrentSquare(player);
			movePlayerToSquare(player, square);
			state.getPitch().getBall().setSquare(square);
			
			if (state.getPitch().isBallInEndzone(oppositeTeam(playerOwner(player)))){
				touchdown(playerOwner(player));
				return;
			}
			
		} else {
			// Move player
			removePlayerFromCurrentSquare(player);
			movePlayerToSquare(player, square);
		}
		
		// Pick up ball
		Square ballOn = state.getPitch().getBall().getSquare();
		boolean onBall = false;
		if (ballOn != null && 
				ballOn.getX() == square.getX() && 
				ballOn.getY() == square.getY() && 
				state.getPitch().getBall().isOnGround() && 
				!state.getPitch().getBall().isUnderControl()){
			
			onBall = true;
			
		}
		
		if (falling){
			knockDown(player, true);
			if (onBall){
				rerollsAllowed = false;
				scatterBall();
			}
			endTurn();
		} else if (onBall){
			pickUpBall();
		}
		
	}

	private void touchdown(Team team) {
		
		// Add score
		team.getTeamStatus().incScore();
		
		if (logging)
			GameLog.push("TOUCHDOWN! " + team.getTeamName() + " scored a touchdown.");
		
		if ((team == state.getHomeTeam() && 
				state.getAwayTurn() == 8) || 
				(team == state.getAwayTeam() && 
				state.getHomeTurn() == 8)){
			
			endHalf();
			return;
			
		}
		
		state.setAwaitFollowUp(false);
		state.setAwaitPush(false);
		state.setAwaitReroll(false);
		
		setupUpForKickOff();
		
		// Who kicks?
		state.setKickingTeam(team);
		state.setReceivingTeam(oppositeTeam(team));
		
	}

	private Team oppositeTeam(Team team) {
		
		if (team == state.getHomeTeam()){
			return state.getAwayTeam();
		}
		
		return state.getHomeTeam();
	}

	private boolean isBallCarried(Player player) {
		
		Square playerOn = player.getPosition();
		Square ballOn = state.getPitch().getBall().getSquare();
		
		if (playerOn != null &&
				ballOn != null &&
				playerOn.getX() == ballOn.getX() && 
				playerOn.getY() == ballOn.getY() && 
				state.getPitch().getBall().isUnderControl()){
			
			return true;
			
		}
		
		return false;
		
	}

	private void pickUpBall() {
		
		Square square = state.getPitch().getBall().getSquare();
		
		Player player = state.getPitch().getPlayerAt(square);
		
		if (player == null || square == null){	
			return;
		}
		
		int zones = numberOfTackleZones(player);
		int success = 6 - player.getAG() + zones;
		success = Math.max( 2, Math.min(6, success) );
		if (state.getWeather() == Weather.POURING_RAIN){
			success++;
		}
		
		// Roll
		DiceRoll roll = new DiceRoll();
		D6 d = new D6();
		roll.addDice(d);
		d.roll();
		state.setCurrentDiceRoll(roll);
		int result = d.getResultAsInt();
		soundManager.playSound(Sound.DICEROLL);
		
		if (result == 6 || 
				(result != 1 && result >= success)){
			
			state.getPitch().getBall().setUnderControl(true);
			
			if (logging)
				GameLog.push("Succeeded pick up! Result: " + result + ", (" + success + " was needed).");
			
			// Touchdown
			if (state.getPitch().isBallInEndzone(oppositeTeam(playerOwner(player)))){
				touchdown(playerOwner(player));
			}
			
		} else { 
			
			if (logging)
				GameLog.push("Failed pick up! Result: " + result + ", (" + success + " was needed).");
			
			if (player.getSkills().contains(Skill.SURE_HANDS)){
			
				// Roll
				DiceRoll sroll = new DiceRoll();
				D6 sd = new D6();
				sroll.addDice(sd);
				sd.roll();
				state.setCurrentDiceRoll(sroll);
				result = d.getResultAsInt();
				soundManager.playSound(Sound.DICEROLL);
				
				if (result == 6 || 
						(result != 1 && result >= success)){
					
					state.getPitch().getBall().setUnderControl(true);
					if (logging)
						GameLog.push("Succeeded pick up - using Sure Hands! Result: " + result + ", (" + success + " was needed).");
					
					// Touchdown
					if (state.getPitch().isBallInEndzone(oppositeTeam(playerOwner(player)))){
						touchdown(playerOwner(player));
					}
					
				} else {
					
					if (logging)
						GameLog.push("Failed pick up - using Sure Hands! Result: " + result + ", (" + success + " was needed).");
					
				}
				
			} else if (ableToReroll(getPlayerOwner(player))) {
				
				state.setCurrentPickUp(new PickUp(player, square, success));
				state.setAwaitReroll(true);
				return;
				
			}
				
			scatterBall();
			
			endTurn();
			
		}
		
	}

	private void scatterBall() {
		
		int result = (int) (Math.random() * 8 + 1);
		Square ballOn = state.getPitch().getBall().getSquare();
		
		if (ballOn == null)
			return;
			
		switch (result){
		case 1 : ballOn = new Square(ballOn.getX() - 1, ballOn.getY() - 1); break;
		case 2 : ballOn = new Square(ballOn.getX(), ballOn.getY() - 1); break;
		case 3 : ballOn = new Square(ballOn.getX() + 1, ballOn.getY() - 1); break;
		case 4 : ballOn = new Square(ballOn.getX() - 1, ballOn.getY()); break;
		case 5 : ballOn = new Square(ballOn.getX() + 1, ballOn.getY()); break;
		case 6 : ballOn = new Square(ballOn.getX() + 1, ballOn.getY() + 1); break;
		case 7 : ballOn = new Square(ballOn.getX() + 1, ballOn.getY() + 1); break;
		case 8 : ballOn = new Square(ballOn.getX() + 1, ballOn.getY() + 1); break;
		}
		
		state.getPitch().getBall().setSquare(ballOn);
		
		Player player = state.getPitch().getPlayerAt( ballOn );
		
		// Land on player
		if (player != null){
			
			if (player.getPlayerStatus().getStanding() == Standing.UP)
				catchBall();
			else
				scatterBall();
			
			return;
		}
			
		if (state.getGameStage() == GameStage.KICK_OFF){
			
			// Outside pitch
			if (!state.getPitch().isBallInsidePitch() || 
					!state.getPitch().isBallOnTeamSide(state.getReceivingTeam())){
				
				state.setGameStage(GameStage.PLACE_BALL_ON_PLAYER);
				if (logging)
					GameLog.push("Ball landed out of bounds. Place ball on a player.");
				
			}
			
		} else if (!state.getPitch().isBallInsidePitch()){

			throwInBall();
			
		}
		
	}
	
	private void throwInBall() {
		
		Square ballOn = state.getPitch().getBall().getSquare();
		
		if (ballOn == null)
			return;
			
		int x = 0;
		int y = 0;
		
		D3 d = new D3();
		d.roll();
		int roll = d.getResultAsInt();
		
		if (ballOn.getY() < 1){
			y = 1;
		} else if (ballOn.getY() > 15){
			y = -1;
		}
		
		if (ballOn.getX() < 1){
			x = 1;
		} else if (ballOn.getX() > 26){
			x = -1;
		}
		
		if (x != 0 && y == 0){
		
			// Move ball on square in
			state.getPitch().getBall().getSquare().setX(ballOn.getX() + x);
			
			// Left or right
			switch(roll){
			case 1: throwInDirection(x, -1); break;
			case 2: throwInDirection(x, 0); break;
			case 3: throwInDirection(x, 1); break;
			}
		
		} else if (x == 0 && y != 0){
			
			// Move ball on square in
			state.getPitch().getBall().getSquare().setY(ballOn.getY() + y);
			
			// Up or Down
			switch(roll){
			case 1: throwInDirection(-1, y); break;
			case 2: throwInDirection(0, y); break;
			case 3: throwInDirection(1, y); break;
			}
			
		} else if (x != 0 && y != 0){
			
			// Diagonal
			switch(roll){
			case 1: throwInDirection(x, 0); break;
			case 2: throwInDirection(x, y); break;
			case 3: throwInDirection(0, y); break;
			}
			
		}
		
		// Landed on player?
		Square sq = state.getPitch().getBall().getSquare();
		
		if (ballOn == null)
			return;
			
		Player player = state.getPitch().getPlayerAt(sq);
		
		if (player != null){
			catchBall();
		} else if (!state.getPitch().isBallInsidePitch()){
			throwInBall();
		} else {
			scatterBall();
		}
		
	}

	private void throwInDirection(int x, int y) {
		
		D6 da = new D6();
		da.roll();
		D6 db = new D6();
		db.roll();
		int distance = da.getResultAsInt() + db.getResultAsInt();
		
		while(distance > 0){
			
			Square ballOn = state.getPitch().getBall().getSquare();
			
			Square newBallOn = new Square(ballOn.getX() + x, ballOn.getY() + y);
			
			state.getPitch().getBall().setSquare(newBallOn);
			
			if (!state.getPitch().isBallInsidePitch()){
				correctOOBBallPosition(x, y);
				break;
			}
			
			distance--;
			
		}
		
	}

	private void correctOOBBallPosition(int moveX, int moveY) {
		
		Square ballOn = state.getPitch().getBall().getSquare();
		
		if (ballOn.getX() == 0 || ballOn.getX() == 27){
			if (ballOn.getY() != 0 && ballOn.getY() != 16){
				ballOn.setY(ballOn.getY() - moveY);
			}
		} else if (ballOn.getY() == 0 || ballOn.getY() == 16){
			ballOn.setX(ballOn.getX() - moveX);
		}
		
	}

	private void scatterKickedBall() {
		D8 da = new D8();
		D6 db = new D6();
		da.roll();
		db.roll();
		int d8 = da.getResultAsInt();
		int d6 = db.getResultAsInt();
		
		Square ballOn = state.getPitch().getBall().getSquare();
		
		while(d6 > 0){
			ballOn = state.getPitch().getBall().getSquare();
			
			switch (d8){
				case 1 : ballOn = new Square(ballOn.getX() - 1, ballOn.getY() - 1); break;
				case 2 : ballOn = new Square(ballOn.getX(), ballOn.getY() - 1); break;
				case 3 : ballOn = new Square(ballOn.getX() + 1, ballOn.getY() - 1); break;
				case 4 : ballOn = new Square(ballOn.getX() - 1, ballOn.getY()); break;
				case 5 : ballOn = new Square(ballOn.getX() + 1, ballOn.getY()); break;
				case 6 : ballOn = new Square(ballOn.getX() - 1, ballOn.getY() + 1); break;
				case 7 : ballOn = new Square(ballOn.getX(), ballOn.getY() + 1); break;
				case 8 : ballOn = new Square(ballOn.getX() + 1, ballOn.getY() + 1); break;
			}
			
			state.getPitch().getBall().setSquare(ballOn);
			
			d6--;
			
		}
		
		// Gust of wind
		if (state.isGust()){
			
			scatterBall();
			
		}
		
		// Ball lands..
		state.getPitch().getBall().setOnGround(true);
		
		// Landed outside pitch
		if (!state.getPitch().isBallOnTeamSide(state.getReceivingTeam()) || 
				!state.getPitch().isBallInsidePitch()){
			
			state.setGameStage(GameStage.PLACE_BALL_ON_PLAYER);
			
			return;
			
		}

		// Land on player
		Player player = state.getPitch().getPlayerAt(ballOn);
		
		if (player != null){
			
			if (player.getPlayerStatus().getStanding() == Standing.UP)
				catchBall();
			else
				scatterBall();
			
		} else {
			
			scatterBall();
			
		}
		
	}

	private void catchBall() {
		
		Square square = state.getPitch().getBall().getSquare();
		
		if (square == null)
			return;
		
		Player player = state.getPitch().getPlayerAt(square);
		
		if (player == null || square == null){	
			return;
		}
		
		int zones = numberOfTackleZones(player);
		int success = 6 - player.getAG() + zones + 1;
		if (state.getCurrentPass() != null && state.getCurrentPass().isAccurate()){
			success -= 1;
		} else if (state.getCurrentHandOff() != null){
			success -= 1;
		}
		if (state.getWeather() == Weather.POURING_RAIN){
			success++;
		}
		success = Math.max( 2, Math.min(6, success) );
		
		// Roll
		DiceRoll roll = new DiceRoll();
		D6 d = new D6();
		roll.addDice(d);
		d.roll();
		state.setCurrentDiceRoll(roll);
		int result = d.getResultAsInt();
		soundManager.playSound(Sound.DICEROLL);
		
		if (result == 6 || 
				(result != 1 && result >= success)){
			
			state.getPitch().getBall().setUnderControl(true);
			
			if (logging)
				GameLog.push("Succeeded catch! Result: " + result + ", (" + success + " was needed).");
			
			// Touchdown
			if (state.getPitch().isBallInEndzone(oppositeTeam(playerOwner(player)))){
				touchdown(playerOwner(player));
			}
			
		} else { 
			
			if (logging)
				GameLog.push("Failed catch! Result: " + result + ", (" + success + " was needed).");
			
			if (player.getSkills().contains(Skill.CATCH)){
		
				// Roll
				DiceRoll sroll = new DiceRoll();
				D6 sd = new D6();
				sroll.addDice(sd);
				sd.roll();
				state.setCurrentDiceRoll(sroll);
				result = d.getResultAsInt();
				soundManager.playSound(Sound.DICEROLL);
				
				if (result == 6 || 
						(result != 1 && d.getResultAsInt() >= success)){
					
					state.getPitch().getBall().setUnderControl(true);
					if (logging)
						GameLog.push("Succeeded catch - using Catch! Result: " + result + ", (" + success + " was needed).");
					
					// Touchdown
					if (state.getPitch().isBallInEndzone(oppositeTeam(playerOwner(player)))){
						touchdown(playerOwner(player));
					}
					
					return;
					
				} else {
					
					if (logging)
						GameLog.push("Failed catch - using Catch! Result: " + result + ", (" + success + " was needed).");
					
				}
				
			} else if (ableToReroll(getPlayerOwner(player))) {
				
				state.setCurrentCatch(new Catch(player, square, success));
				state.setAwaitReroll(true);
				return;
				
			}
			
			// FAIL
			boolean endTurn = false;
			if (state.getCurrentPass() != null || state.getCurrentHandOff() != null){
				endTurn = true;
				state.setCurrentPass(null);
				state.setCurrentHandOff(null);
			}
			
			scatterBall();
			
			if (endTurn)
				endTurn();
			
		}
		
	}

	private Team getMovingTeam() {
		if (state.getGameStage() == GameStage.HOME_TURN){
			return state.getHomeTeam();
		} else if (state.getGameStage() == GameStage.AWAY_TURN){
			return state.getAwayTeam();
		} else if (state.getGameStage() == GameStage.BLITZ){
			return state.getKickingTeam();
		}
		return null;
	}

	private void movePlayerToSquare(Player player, Square square) {

		// Move player
		placePlayerAt(player, square);

	}

	private boolean moveAllowed(Player player, Square square) {
		
		// Block?
		if (state.isAwaitingPush() || state.isAwaitingFollowUp()){
			return false;
		}
		
		// Legal square
		if (!nextToEachOther(player, square)){
			return false;
		}
		
		// Square on pitch
		if (!state.getPitch().isOnPitch(square)){
			return false;
		}
		
		// Square occupied?
		if (state.getPitch().getPlayerAt(square) != null){
			return false;
		}
		
		// Turn
		if (!isPlayerTurn(player)){
			return false;
		}
		
		// Player turn
		if (player.getPlayerStatus().getTurn() == PlayerTurn.USED){
			return false;
		}
		
		// Enough movement left?
		if (playerMovementLeft(player))
			return true;
		
		// Able to sprint
		if (state.getGameStage() != GameStage.QUICK_SNAP){
			
			if (player.getPlayerStatus().getMovementUsed() < player.getMA() + 2 && 
					player.getPlayerStatus().getStanding() == Standing.UP){
				
				// Going for it
				goingForIt(player, square);
				
			}
			
		}
		
		return false;
		
	}
	
	private boolean standUpAllowed(Player player) {
		
		// Block?
		if (state.isAwaitingPush() || state.isAwaitingFollowUp()){
			return false;
		}
		
		// Turn
		if (!isPlayerTurn(player)){
			return false;
		}
		
		// Player turn
		if (player.getPlayerStatus().getTurn() == PlayerTurn.USED){
			return false;
		}
		
		// Enough movement left?
		if (playerMovementLeft(player))
			return true;
		
		return false;
		
	}

	private boolean playerMovementLeft(Player player) {

		boolean movementLeft = false;
		
		if (player.getPlayerStatus().getStanding() == Standing.UP){
			
			// Quick snap
			if (state.getGameStage() == GameStage.QUICK_SNAP){
				
				if (player.getPlayerStatus().getMovementUsed() == 0){
					
					return true;
					
				} 
				
				return false;
				
			} 

			// Normal turn
			if (player.getPlayerStatus().getMovementUsed() < player.getMA()){
				
				// Normal move
				movementLeft = true;
				
			}
			
		} else if (player.getPlayerStatus().getStanding() == Standing.DOWN && 
				player.getPlayerStatus().getMovementUsed() + 3 < player.getMA()){

			// Stand up and move
			movementLeft = true;
				
		}
		
		return movementLeft;
	}

	private boolean isPlayerTurn(Player player) {
		
		boolean playerTurn = false;
		
		if (state.getGameStage() == GameStage.HOME_TURN && 
				state.getHomeTeam() == playerOwner(player)){
			
			playerTurn = true;
			
		} else if (state.getGameStage() == GameStage.AWAY_TURN && 
				state.getAwayTeam() == playerOwner(player)){
			
			playerTurn = true;
			
		} else if (state.getGameStage() == GameStage.BLITZ && 
				state.getKickingTeam() == playerOwner(player)){
			
			playerTurn = true;
			
		} else if (state.getGameStage() == GameStage.QUICK_SNAP && 
				state.getReceivingTeam() == playerOwner(player)){
			
			playerTurn = true;
			
		} else if (state.getGameStage() == GameStage.PERFECT_DEFENSE &&
				state.getKickingTeam() == playerOwner(player)){
					
			//playerTurn = true;
			
		}
		
		return playerTurn;
	}

	private void goingForIt(Player player, Square square) {
		
		DiceRoll roll = new DiceRoll();
		D6 d = new D6();
		roll.addDice(d);
		d.roll();
		state.setCurrentDiceRoll(roll);
		soundManager.playSound(Sound.DICEROLL);
		int success = 2;
		if (state.getWeather() == Weather.BLIZZARD){
			success++;
		}
		
		if (d.getResultAsInt() >= success){
			
			if (logging)
				GameLog.push("Succeded going for it! Result: " + d.getResultAsInt() + " (" + success + " was needed).");
			
			// Blitz?
			if (player.getPosition().equals(square) && 
					state.getCurrentBlock() != null && 
					state.getCurrentBlock().getAttacker() == player){
				player.getPlayerStatus().setMovedToBlock(true);
				performBlock(player, state.getCurrentBlock().getDefender());
				return;
			}
			
			if (isInTackleZone(player))
				dodgeToMovePlayer(player, square);
			else 
				movePlayer(player, square, false);
			
		} else {
			
			if (logging)
				GameLog.push("Failed going for it! Result: " + d.getResultAsInt() + " (" + success + " was needed).");
			
			if (ableToReroll(playerOwner(player))){
				state.setCurrentGoingForIt(new GoingForIt(player, square, success));
				state.setAwaitReroll(true);
			} else {
				movePlayer(player, square, true);
			}
			
		}
		
	}

	private void continueBlock(DiceFace face) {
		
		// DEBUG:
		//face = DiceFace.PUSH;
		
		state.setAwaitReroll(false);
		
		state.getCurrentBlock().setResult(face);
		
		switch(face){
		case SKULL : attackerDown(state.getCurrentBlock()); break;
		case PUSH : defenderPushed(state.getCurrentBlock()); break;
		case BOTH_DOWN : bothDown(state.getCurrentBlock()); break;
		case DEFENDER_STUMBLES : defenderStumples(state.getCurrentBlock()); break;
		case DEFENDER_KNOCKED_DOWN : defenderKnockedDown(state.getCurrentBlock()); break;
		default:
			break;
		}
	}

	private void defenderKnockedDown(Block block) {
		
		if (logging)
			GameLog.push("Defender down!");
		
		defenderPushed(block);
		
	}

	private void defenderStumples(Block block) {
		
		if (logging)
			GameLog.push("Defender pushed!");
		
		defenderPushed(block);
		
	}

	private void bothDown(Block block) {
		
		if (logging)
			GameLog.push("Both down!");
		
		if (!block.getDefender().getSkills().contains(Skill.BLOCK)){
			knockDown(block.getDefender(), true);
		}
		
		if (!block.getAttacker().getSkills().contains(Skill.BLOCK)){
			knockDown(block.getAttacker(), true);
			state.setCurrentBlock(null);
			endTurn();
			return;
		} else {
			/*
			if (block.getAttacker().getPlayerStatus().getTurn() != PlayerTurn.BLITZ_ACTION){
				block.getAttacker().getPlayerStatus().setTurn(PlayerTurn.USED);
			}
			*/
			if (block.getAttacker().getPlayerStatus().getTurn() == PlayerTurn.BLOCK_ACTION){
				block.getAttacker().getPlayerStatus().setTurn(PlayerTurn.USED);
			}
			state.setCurrentBlock(null);
			
		}
		
	}

	private void defenderPushed(Block block) {
		
		Square from = block.getAttacker().getPosition();
		Square to = block.getDefender().getPosition();
		
		Push push = new Push(block.getDefender(), from, to);
		
		push.setPushSquares( eliminatedPushSquares(push) );
		
		state.getCurrentBlock().setPush(push);
		
		state.setAwaitPush(true);
		
	}

	private ArrayList<Square> eliminatedPushSquares(Push push) {
		
		ArrayList<Square> squaresOOB = new ArrayList<Square>();
		ArrayList<Square> squaresWithPlayers = new ArrayList<Square>();
		ArrayList<Square> squaresWithoutPlayers = new ArrayList<Square>();
		for (Square sq : push.getPushSquares()){
			if (state.getPitch().isOnPitch(sq)){
				if (state.getPitch().getPlayerAt(sq) == null){
					squaresWithoutPlayers.add(sq);
				} else {
					squaresWithPlayers.add(sq);
				}
			} else {
				squaresOOB.add(sq);
			}
		}
		
		if (squaresWithoutPlayers.size() == 3){
			return squaresWithoutPlayers;
		} else if (squaresWithoutPlayers.size() == 0){
			if (squaresOOB.size() > 0){
				return squaresOOB;
			}
			return squaresWithPlayers;
		}
		
		return squaresWithoutPlayers;
	}

	private void attackerDown(Block block) {
		
		state.setCurrentBlock(null);
		
		knockDown( block.getAttacker(), true );
		
		endTurn();
		
	}

	private void endTurn() {
		
		if (state.isAwaitingFollowUp() || state.isAwaitingPush() || state.isAwaitingReroll()){
			if (state.getGameStage() != GameStage.PLACE_BALL_ON_PLAYER)
				return;
		}
		
		// Clear dice roll
		state.setCurrentBlock(null);
		state.setCurrentDodge(null);
		state.setCurrentPickUp(null);
		state.setCurrentPass(null);
		state.setCurrentCatch(null);
		state.setCurrentHandOff(null);
		state.setCurrentGoingForIt(null);
		state.setAwaitFollowUp(false);
		state.setAwaitPush(false);
		state.setAwaitReroll(false);
		
		// Any turns left?
		if (state.getGameStage() == GameStage.HOME_TURN){
			
			if (state.getAwayTurn() < 8){
				
				// Away turn
				startNewTurn();
				
			} else {
				
				endHalf();
				
			}
			
		} else if (state.getGameStage() == GameStage.AWAY_TURN){
			
			if (state.getHomeTurn() < 8){
				
				// Away turn
				startNewTurn();
				
			} else {
				
				if (state.getHalf() == 1){
					
					startNextHalf();
					
				} else {
					
					endGame();
					
				}
				
			}
			
		} else if (state.getGameStage() == GameStage.BLITZ || 
				state.getGameStage() == GameStage.QUICK_SNAP || 
				state.getGameStage() == GameStage.PERFECT_DEFENSE){
			
			//state.setGameStage(GameStage.KICK_OFF);
			endKickOffPhase();
			//startNewTurn();
		
		} else if (state.getGameStage() == GameStage.PLACE_BALL_ON_PLAYER){
			
			if (state.getPitch().getBall().isUnderControl()) {
				//state.setGameStage(GameStage.KICK_OFF);
				startNewTurn();
			} else {
				if (logging)
					GameLog.push("Place the ball on a player.");
			}
		
		} else if (state.getGameStage() == GameStage.HIGH_KICK){
			
			Square ballOn = state.getPitch().getBall().getSquare();
			
			if (state.getPitch().getPlayerAt(ballOn) != null || selectedPlayer == null){
				//state.setGameStage(GameStage.KICK_OFF);
				endKickOffPhase();
				return;
			}
			
			if (playerOwner(selectedPlayer) == state.getReceivingTeam() && 
					state.getPitch().isOnPitch(selectedPlayer) && 
					!inTacklesZoneExcept(selectedPlayer, selectedPlayer)){
				
				// Place player under ball
				placePlayerUnderBall(selectedPlayer);
				
				state.getPitch().getBall().setOnGround(true);
				catchBall();
				
				//endKickOffPhas0e();
				state.setGameStage(GameStage.KICK_OFF);
				//startNewTurn();
				
			}
		} else if (state.getGameStage() == GameStage.KICK_OFF){

			endKickOffPhase();
			
		}
		
	}

	private void endHalf() {
		
		soundManager.playSound(Sound.WHISTLE);
		
		if (state.getHalf() == 1){
			
			startNextHalf();
			
		} else {
			
			endGame();
			
		}
		
	}

	private void endGame() {
		
		state.setGameStage(GameStage.GAME_ENDED);
		
	}

	private void startNextHalf() {
		
		// set to next half
		state.setHalf(state.getHalf() + 1);
		state.setHomeTurn(0);
		state.setAwayTurn(0);
		
		// Who kicks?
		if ( state.getCoinToss().isHomeReceives() ){
			
			state.setKickingTeam(state.getHomeTeam());
			state.setReceivingTeam(state.getAwayTeam());
			
		} else {
			
			state.setKickingTeam(state.getAwayTeam());
			state.setReceivingTeam(state.getHomeTeam());
			
		}
		
		setupUpForKickOff();
		
	}

	private void setupUpForKickOff() {
		
		ArrayList<Player> collapsedPlayers = new ArrayList<Player>();
		
		if (state.getWeather() == Weather.SWELTERING_HEAT){
			
			for (Player p : state.getAwayTeam().getPlayers()){
				if (state.getPitch().isOnPitch(p)){
					D6 d = new D6();
					d.roll();
					if (d.getResultAsInt() == 1){
						collapsedPlayers.add(p);
					}
				}
			}
			
			for (Player p : state.getHomeTeam().getPlayers()){
				if (state.getPitch().isOnPitch(p)){
					D6 d = new D6();
					d.roll();
					if (d.getResultAsInt() == 1){
						collapsedPlayers.add(p);
					}
				}
			}
			
		}
		
		clearField();
		fixStunnedPlayers(state.getHomeTeam());
		resetStatii(state.getAwayTeam(), true);
		resetStatii(state.getHomeTeam(), true);
		standUpAllPlayers();
		rollForKnockedOut();
		
		state.getPitch().getBall().setOnGround(false);
		state.getPitch().getBall().setSquare(null);
		
		for (Player p : collapsedPlayers){
			p.getPlayerStatus().setStanding(Standing.DOWN);
		}
		
		state.setGameStage(GameStage.KICKING_SETUP);
		
	}

	private void standUpAllPlayers() {
		
		for(Player p : state.getHomeTeam().getPlayers()){
			p.getPlayerStatus().setStanding(Standing.UP);
		}
		
		for(Player p : state.getAwayTeam().getPlayers()){
			p.getPlayerStatus().setStanding(Standing.UP);
		}
		
	}

	private void rollForKnockedOut() {
		
		D6 da = new D6();
		
		// Home team
		ArrayList<Player> ready = new ArrayList<Player>();
		for(Player player : state.getPitch().getHomeDogout().getKnockedOut()){
			
			da.roll();
			
			if (da.getResultAsInt()> 3){
				ready.add(player);
				
			}
			
		}
		
		for(Player player : ready){
			state.getPitch().getHomeDogout().getKnockedOut().remove(player);
			state.getPitch().getHomeDogout().getReserves().add(player);
		}
		
		// Away team
		ready = new ArrayList<Player>();
		for(Player player : state.getPitch().getAwayDogout().getKnockedOut()){
			
			da.roll();
			
			if (da.getResultAsInt() > 3){
				ready.add(player);
			}
			
		}
		
		for(Player player : ready){
			state.getPitch().getAwayDogout().getKnockedOut().remove(player);
			state.getPitch().getAwayDogout().getReserves().add(player);
		}
		
	}

	private void clearField() {
		
		// Home team
		for(Player player : state.getHomeTeam().getPlayers()){
			
			if (state.getPitch().isOnPitch(player)){
				
				state.getPitch().removePlayer(player);
				state.getPitch().getHomeDogout().getReserves().add(player);
				
			}
			
		}
		
		// Away team
		for(Player player : state.getAwayTeam().getPlayers()){
			
			if (state.getPitch().isOnPitch(player)){
				
				state.getPitch().removePlayer(player);
				state.getPitch().getAwayDogout().getReserves().add(player);
				
			}
			
		}
		
	}

	private void startNewTurn() {
		
		selectedPlayer = null;
		
		if (state.getGameStage() == GameStage.KICK_OFF || 
				state.getGameStage() == GameStage.BLITZ || 
				state.getGameStage() == GameStage.QUICK_SNAP || 
				state.getGameStage() == GameStage.PLACE_BALL_ON_PLAYER || 
				state.getGameStage() == GameStage.HIGH_KICK){
			
			if (state.getHomeTeam() == state.getReceivingTeam()){
				
				state.setGameStage(GameStage.HOME_TURN);
				state.incHomeTurn();
				
				resetStatii(state.getAwayTeam(), false);
				resetStatii(state.getHomeTeam(), false);
				fixStunnedPlayers(state.getHomeTeam());
				
			} else {
				
				state.setGameStage(GameStage.AWAY_TURN);
				state.incAwayTurn();
				
				resetStatii(state.getAwayTeam(), false);
				resetStatii(state.getHomeTeam(), false);
				fixStunnedPlayers(state.getAwayTeam());
				
			}
			
			rerollsAllowed = true;
			
		} else if (state.getGameStage() == GameStage.HOME_TURN){
			
			state.setGameStage(GameStage.AWAY_TURN);
			state.incAwayTurn();
			
			resetStatii(state.getHomeTeam(), false);
			resetStatii(state.getAwayTeam(), false);
			fixStunnedPlayers(state.getAwayTeam());
			
			rerollsAllowed = true;
			
		} else if (state.getGameStage() == GameStage.AWAY_TURN){
				
			state.setGameStage(GameStage.HOME_TURN);
			state.incHomeTurn();
			
			resetStatii(state.getAwayTeam(), false);
			resetStatii(state.getHomeTeam(), false);
			fixStunnedPlayers(state.getHomeTeam());
			
			rerollsAllowed = true;
				
		}
		
	}

	private void resetStatii(Team team, boolean newRerolls) {
		
		if (newRerolls)
			team.reset();
		else 
			team.getTeamStatus().reset();
		
		for(Player p : team.getPlayers()){
			
			p.getPlayerStatus().reset();
			
		}
		
	}

	private void fixStunnedPlayers(Team team) {
		
		for(Player p : team.getPlayers()){
			
			if (p.getPlayerStatus().getStanding() == Standing.STUNNED){
				
				p.getPlayerStatus().setStanding(Standing.DOWN);
				p.getPlayerStatus().setTurn(PlayerTurn.USED);
				
			}
			
		}
		
	}

	private void knockDown(Player player, boolean armourRoll) {
		
		soundManager.playSound(Sound.KNOCKEDDOWN);
		
		player.getPlayerStatus().setTurn(PlayerTurn.USED);
		
		// Armour roll
		D6 da = new D6();
		D6 db = new D6();
		da.roll();
		db.roll();
		
		int result = da.getResultAsInt() + db.getResultAsInt();
		boolean knockedOut = false;
		boolean deadAndInjured = false;
		if (armourRoll && logging)
			GameLog.push("Armour roll: " + result + " (AV: " + player.getAV() + ")");
		
		if (result > player.getAV() || !armourRoll){
			
			// Injury roll
			da.roll();
			db.roll();
			
			result = da.getResultAsInt() + db.getResultAsInt();
			
			if (logging)
				GameLog.push("Injury roll: " + result);
			
			if (result < 8){
				
				// Stunned
				player.getPlayerStatus().setStanding(Standing.STUNNED);
				if (logging)
					GameLog.push("Player stunned.");
				
				// Fumble
				if (isBallCarried(player)){
					state.getPitch().getBall().setUnderControl(false);
					rerollsAllowed = false;
					scatterBall();
				}
				
			} else if (result < 10){
				
				// Knocked out
				if (logging)
					GameLog.push("Player knocked out.");
				knockedOut = true;
				
				
			} else {
				
				// Dead and injured
				if (logging)
					GameLog.push("Player dead or injured.");
				deadAndInjured = true;
				
			}
			
		} else {
		
			player.getPlayerStatus().setStanding(Standing.DOWN);
			
			// Fumble
			if (isBallCarried(player)){
				state.getPitch().getBall().setUnderControl(false);
				rerollsAllowed = false;
				scatterBall();
			}
			
		}
		
		if (knockedOut){
			player.getPlayerStatus().setStanding(Standing.DOWN);
			
			// Fumble
			if (isBallCarried(player)){
				state.getPitch().getBall().setUnderControl(false);
				rerollsAllowed = false;
				scatterBall();
			}
			
			player.getPlayerStatus().setStanding(Standing.UP);
			state.getPitch().removePlayer(player);
			state.getPitch().getDogout(getPlayerOwner(player)).getKnockedOut().add(player);
			
		} else if (deadAndInjured){
			player.getPlayerStatus().setStanding(Standing.DOWN);
			
			// Fumble
			if (isBallCarried(player)){
				state.getPitch().getBall().setUnderControl(false);
				rerollsAllowed = false;
				scatterBall();
			}

			player.getPlayerStatus().setStanding(Standing.UP);
			state.getPitch().removePlayer(player);
			state.getPitch().getDogout(getPlayerOwner(player)).getDeadAndInjured().add(player);
			
		}
		
	}
	
	private int calculateFoulSum(Player fouler, Player target) {
		
		int attAss = assists(fouler, target);
		int defAss = assists(target, fouler);
		
		return attAss - defAss;
	}

	private BlockSum calculateBlockSum(Player attacker, Player defender) {
		
		int attStr = attacker.getST();
		int defStr = defender.getST();
		
		attStr += assists(attacker, defender);
		defStr += assists(defender, attacker);
		
		if (attStr > defStr * 2){
			return BlockSum.ATTACKER_DOUBLE_STRONG;
		} else if (defStr > attStr * 2){
			return BlockSum.DEFENDER_DOUBLE_STRONG;
		} else if (attStr > defStr){
			return BlockSum.ATTACKER_STRONGER;
		} else if (attStr < defStr){
			return BlockSum.DEFENDER_STRONGER;
		}
		
		return BlockSum.EQUAL;
	}

	private int assists(Player attacker, Player defender) {
		
		int assists = 0;
		
		Square defPos = defender.getPosition();
		
		for(int y = -1; y <= 1; y++){
			
			for(int x = -1; x <= 1; x++){
				
				Square sq = new Square(x + defPos.getX(), y + defPos.getY());
				
				Player player = state.getPitch().getPlayerAt(sq);
				
				if (player == null ||
						player == attacker || 
						player == defender || 
						playerOwner(player) == playerOwner(defender) ||
						player.getPlayerStatus().getStanding() != Standing.UP){
				
					continue;
				}
					
				if (!inTacklesZoneExcept(player, defender)){
					
					assists++;
					
				}
			}
			
		}
		
		return assists;
	}

	private boolean inTacklesZoneExcept(Player player, Player exception) {
		
		Square square = player.getPosition();
		
		for(int y = -1; y <= 1; y++){
			for(int x = -1; x <= 1; x++){
				
				Square test = new Square(square.getX() + x, square.getY() + y);
				
				Player p = state.getPitch().getPlayerAt(test); 
				
				// Opposite team or exception?
				if (p == null ||
						getPlayerOwner(p) == getPlayerOwner(player) ||
						p == exception){
					continue;
				}
					
				if (p.getPlayerStatus().getStanding() == Standing.UP){
					return true;
				}
			}
		}
		
		return false;
	}

	private boolean nextToEachOther(Player a, Player b) {
		
		if (state.getPitch().isOnPitch(a) &&
				state.getPitch().isOnPitch(b)){
			
			Square aPos = a.getPosition();
			Square bPos = b.getPosition();
			
			// Not equal
			if (aPos.getX() == bPos.getX() && aPos.getY() == bPos.getY()){
				return false;
			}
			
			// At most one away
			if (Math.abs( aPos.getX() - bPos.getX() ) <= 1 ){
				if (Math.abs( aPos.getY() - bPos.getY() ) <= 1 ){
					return true;
				}
			}
			
		}
		
		return false;
	}
	
	private void placeBall(Square square) {
		
		state.getPitch().getBall().setSquare(square);
		
	}

	private boolean allowedToBlock(Player player) {
		
		boolean allowed = false;
		
		// Home turn
		if (state.getGameStage() == GameStage.HOME_TURN && 
				state.getHomeTeam() == playerOwner(player)){
			
			allowed = true;
			
		}
		
		// Away turn
		if (state.getGameStage() == GameStage.AWAY_TURN && 
				state.getAwayTeam() == playerOwner(player)){
			
			allowed = true;
			
		}
		
		// Blitz phase
		if (state.getGameStage() == GameStage.BLITZ && 
				state.getKickingTeam() == playerOwner(player)){
			
			allowed = true;
			
		}
		
		if (!allowed)
			return false;
		
		allowed = false;
		
		// Player have had turn?
		if (player.getPlayerStatus().getTurn() == PlayerTurn.USED){
			
			return false;
			
		} else if (player.getPlayerStatus().getTurn() == PlayerTurn.BLITZ_ACTION && 
				!playerOwner(player).getTeamStatus().hasBlitzed()){
			
			// Blitz
			if (player.getPlayerStatus().getMovementUsed() < player.getMA() + 2){
				allowed = true;
			}
			
		} else if (player.getPlayerStatus().getTurn() == PlayerTurn.UNUSED){
			
			allowed = true;
			
		}  else if (player.getPlayerStatus().getTurn() == PlayerTurn.BLOCK_ACTION){
			
			allowed = true;
			
		}
		
		// Standing
		if (allowed && player.getPlayerStatus().getStanding() == Standing.UP){
			
			return true;
			
		} else if (allowed && player.getPlayerStatus().getStanding() == Standing.DOWN && 
				player.getPlayerStatus().getTurn() == PlayerTurn.BLITZ_ACTION){
			
			if (player.getMA() > 3){
				
				return true;
			}
			
		}
			
		return false;
		
	}
	
	private boolean nextToEachOther(Player player, Square square) {
		
		if (state.getPitch().isOnPitch(player)){
			
			Square aPos = player.getPosition();
			
			// Not equal
			if (aPos.getX() == square.getX() && aPos.getY() == square.getY()){
				return false;
			}
			
			// At most one away
			if (Math.abs( aPos.getX() - square.getX() ) <= 1 ){
				if (Math.abs( aPos.getY() - square.getY() ) <= 1 ){
					return true;
				}
			}
			
		}
		
		return false;
		
	}

	private Team playerOwner(Player player) {
		if (state.getHomeTeam().getPlayers().contains(player)){
			return state.getHomeTeam();
		} 
		return state.getAwayTeam();
	}

	private void placePlayerAt(Player player, Square square) {
		state.getPitch().getPlayerArr()[square.getY()][square.getX()] = player;
		player.setPosition(square);
	}

	private void removePlayerFromReserves(Player player) {
		state.getPitch().getDogout(getPlayerOwner(player)).getReserves().remove(player);
	}
	
	private void removePlayerFromCurrentSquare(Player player) {
		state.getPitch().removePlayer(player);
		player.setPosition(null);
	}

	private Team getPlayerOwner(Player player) {

		// On home team?
		if (state.getHomeTeam().getPlayers().contains(player)){
			return state.getHomeTeam();
		} else {
			return state.getAwayTeam();
		}

	}
	
	private void kickBall() {
		
		// Ball not placed?
		if (state.getPitch().getBall().getSquare() == null){
			if (logging)
				GameLog.push("The ball has not been placed!");
			return;
		}
		
		// Ball corectly placed?
		if (!state.getPitch().ballCorreclyPlaced(state.getKickingTeam())){
			if (logging)
				GameLog.push("The ball has not been placed correctly!");
			return;
		}
		
		//state.setGameStage(GameStage.KICK_OFF);
		
		rollForKickOff();
		
		// If special kick off phase started
		if (state.getGameStage() != GameStage.KICK_OFF){
			
			return;
		}
		
		endKickOffPhase();
		
	}

	private void endKickOffPhase() {
		
		if (state.getGameStage() == GameStage.PERFECT_DEFENSE && 
				!state.getPitch().isSetupLegal(state.getKickingTeam(), state.getHalf())){
			
			return;
		}
		
		scatterKickedBall();
		
		if (state.getGameStage() != GameStage.PLACE_BALL_ON_PLAYER){
			//state.setGameStage(GameStage.KICK_OFF);
			startNewTurn();
		}
	}

	private void rollForKickOff(){
		
		soundManager.playSound(Sound.WHISTLE);
		soundManager.playSound(Sound.CHEER);
		
		D6 da = new D6();
		D6 db = new D6();
		da.roll(); 
		db.roll();
		int roll = da.getResultAsInt() + db.getResultAsInt();
		
		// DEBUGGING
		blitz();
		//throwARock();
		//highKick();
		//perfectDefense();
		//quickSnap();
		/*
		switch(roll){
			case 2: getTheRef(); break;
			case 3: riot(); break;
			case 4: perfectDefense(); break;
			//case 5: highKick(); break;
			case 5: cheeringFans(); break;
			case 6: cheeringFans(); break;
			case 7: changingWeather(); break;
			case 8: brilliantCoaching(); break;
			case 9: quickSnap(); break;
			case 10: blitz(); break;
			case 11: throwARock(); break;
			case 12: pitchInvasion(); break;
		}
		*/
	}

	/**
	 * Pitch Invasion:  Both coaches roll a D6 for each 
	 * opposing player on the pitch and add their FAME 
	 * (see page 18) to the roll. If a roll is 6 or more after 
	 * modification then the player is Stunned (players with 
	 * the Ball & Chain skill are KO'd). A roll of 1 before 
	 * adding FAME will always have no effect.
	 */
	private void pitchInvasion() {
		
		if (logging)
			GameLog.push("Pitch invasion!");
	
		invadeTeam(state.getHomeTeam());
		
		invadeTeam(state.getAwayTeam());
		
	}

	/**
	 * Throw a Rock: An enraged fan hurls a large rock at 
	 * one of the players on the opposing team.  Each 
	 * coach rolls a D6 and adds their FAME (see page 
	 * 18) to the roll. The fans of the team that rolls higher 
	 * are the ones that threw the rock. In the case of a tie 
	 * a rock is thrown at each team! Decide randomly 
	 * which player in the other team was hit (only players 
	 * on the pitch are eligible) and roll for the effects of 
	 * the injury straight away. No Armour roll is required.
	 */
	private void throwARock() {
		
		if (logging)
			GameLog.push("Throw a rock!");
	
		D6 home = new D6();
		D6 away = new D6();
		
		home.roll();
		away.roll();
		
		int homeResult = home.getResultAsInt() + 
				state.getHomeTeam().getTeamStatus().getFAME();
		
		int awayResult = away.getResultAsInt() + 
				state.getAwayTeam().getTeamStatus().getFAME();
		
		if (homeResult >= awayResult){
			if (logging)
				GameLog.push(state.getHomeTeam().getTeamName() + " threw a rock.");
			
			// Injure random away player
			throwRockAt(state.getAwayTeam());
			
		}
		if (awayResult >= homeResult){
			if (logging)
				GameLog.push(state.getAwayTeam().getTeamName() + " threw a rock.");
			
			// Injure random home player
			throwRockAt(state.getHomeTeam());
			
		}
		
	}
	
	private void invadeTeam(Team team) {
		
		for(Player p : team.getPlayers()){
			
			if (state.getPitch().isOnPitch(p)){
				
				D6 d = new D6();
				
				d.roll();
				
				int result = d.getResultAsInt() + 
						state.getHomeTeam().getTeamStatus().getFAME();
				
				if (d.getResultAsInt() == 1){
					
					continue;
					
				} else if (result >= 6){
					
					p.getPlayerStatus().setStanding(Standing.STUNNED);
					
				}
				
			}
			
		}
		
	}

	private void throwRockAt(Team team) {
		
		ArrayList<Player> targets = new ArrayList<Player>();
		
		for(Player p : team.getPlayers()){
			
			if (state.getPitch().isOnPitch(p)){
				targets.add(p);
			}
			
		}
		
		int ran = (int)(Math.random()*targets.size());
		
		Player target = targets.get(ran);
		
		// Knock down player with no armour save
		knockDown(target, false);
		
	}

	private void blitz() {
		state.setGameStage(GameStage.BLITZ);
		if (logging)
			GameLog.push("Blitz!");
	}

	private void quickSnap() {
		state.setGameStage(GameStage.QUICK_SNAP);
		if (logging)
			GameLog.push("Quck snap!");
	}

	/**
	 * Brilliant Coaching: Each coach rolls a D3 and adds 
	 * their FAME (see page 18) and the number of 
	 * assistant coaches on their team to the score. The 
	 * team with the highest total gets an extra team re-roll 
	 * this half thanks to the brilliant instruction provided 
	 * by the coaching staff. In case of a tie both teams get 
	 * an extra team re-roll. 
	 */
	private void brilliantCoaching() {
		
		if (logging)
			GameLog.push("Brilliant coaching!");
		
		D3 home = new D3();
		D3 away = new D3();
		
		home.roll();
		away.roll();
		
		int homeResult = home.getResultAsInt() + 
				state.getHomeTeam().getTeamStatus().getFAME() + 
				state.getHomeTeam().getAssistantCoaches();
		
		int awayResult = away.getResultAsInt() + 
				state.getAwayTeam().getTeamStatus().getFAME() + 
				state.getAwayTeam().getAssistantCoaches();
		
		if (homeResult >= awayResult){
			int rr = state.getHomeTeam().getTeamStatus().getRerolls() + 1;
			state.getHomeTeam().getTeamStatus().setRerolls(rr);
			if (logging)
				GameLog.push(state.getHomeTeam().getTeamName() + " gets an extra reroll.");
		}
		if (awayResult >= homeResult){
			int rr = state.getAwayTeam().getTeamStatus().getRerolls() + 1;
			state.getAwayTeam().getTeamStatus().setRerolls(rr);
			if (logging)
				GameLog.push(state.getAwayTeam().getTeamName() + " gets an extra reroll.");
		}
		
	}

	/**
	 * Changing Weather: Make a new roll on the Weather 
	 * table (see page 20). Apply the new Weather roll. If 
	 * the new Weather roll was a ‘Nice’ result, then a 
	 * gentle gust of wind makes the ball scatter one extra 
	 * square in a random direction before landing.  
	 */
	private void changingWeather() {
		if (logging)
			GameLog.push("Changing weather!");
		rollForWeather();
		
		// Gentle gust
		if (state.getWeather() == Weather.NICE){
			state.setGust(true);
		}
	}

	/**
	 * Cheering Fans:  Each coach rolls a  D3  and 
	 * adds their team’s FAME (see page 18) 
	 * and the number of cheerleaders on their 
	 * team to the score. 
	 * The team with the highest score is 
	 * inspired by their fans' 
	 * cheering and gets an extra re-roll this half. 
	 * If both teams have the same score, then 
	 * both teams get a reroll.
	 */
	private void cheeringFans() {
		
		if (logging)
			GameLog.push("Cheering fans!");
		
		D3 home = new D3();
		D3 away = new D3();
		
		home.roll();
		away.roll();
		
		int homeResult = home.getResultAsInt() + 
				state.getHomeTeam().getTeamStatus().getFAME() + 
				state.getHomeTeam().getCheerleaders();
		
		int awayResult = away.getResultAsInt() + 
				state.getAwayTeam().getTeamStatus().getFAME() + 
				state.getAwayTeam().getCheerleaders();
		
		if (homeResult >= awayResult){
			int rr = state.getHomeTeam().getTeamStatus().getRerolls() + 1;
			state.getHomeTeam().getTeamStatus().setRerolls(rr);
			if (logging)
				GameLog.push(state.getHomeTeam().getTeamName() + " gets an extra reroll.");
		}
		if (awayResult >= homeResult){
			int rr = state.getAwayTeam().getTeamStatus().getRerolls() + 1;
			state.getAwayTeam().getTeamStatus().setRerolls(rr);
			if (logging)
				GameLog.push(state.getAwayTeam().getTeamName() + " gets an extra reroll.");
		}
		
	}

	private void highKick() {
		if (logging)
			GameLog.push("High kick!");
		state.setGameStage(GameStage.HIGH_KICK);
		
		// Scatter kick
		scatterKickedBall();
		
	}

	private void perfectDefense() {
		if (logging)
			GameLog.push("Perfect defense!");
		state.setGameStage(GameStage.PERFECT_DEFENSE);
	}

	/**
	 * 	The trash talk between two opposing players 
	 *	explodes and rapidly degenerates, involving the rest 
	 *	of the players. Roll a D6. On a 1-3, the referee lets the 
	 *	clock run on during the fight; both teams’ turn markers 
	 *	are moved  forward  along the turn track a number of 
	 *	spaces equal to the D6 roll. If this takes the number of 
	 *	turns to 8 or more for both teams, then the half ends. 
	 *	On a roll of 4-6 the referee resets the clock back to 
	 *	before the fight started, so both teams turn markers 
	 *	are moved one space back along the track. The turn 
	 *	marker may not be moved back before turn 1; if this 
	 *	would happen do not move the Turn marker in either 
	 * 	direction. 
	 */
	private void riot() {
		
		if (logging)
			GameLog.push("Riot!");
		
		D6 d = new D6();
		d.roll();
		if (d.getResultAsInt() <= 3){
			
			if (logging)
				GameLog.push("The referee lets the clock run on during the fight.");
			
			// End half?
			if (state.getHomeTurn() + d.getResultAsInt() >= 8 && 
					state.getAwayTurn() + d.getResultAsInt() >= 8){
				
				endHalf();
				
			} else {
				
				// Move turn markers forward
				state.setHomeTurn(state.getHomeTurn() + d.getResultAsInt());
				state.setAwayTurn(state.getAwayTurn() + d.getResultAsInt());
				
			}
			
		} else {
			
			if (logging)
				GameLog.push("The referee resets the clock back to before the fight started.");
			
			// Move turn makers one backwards
			if (state.getHomeTurn() != 0)
				state.setHomeTurn(Math.max(1, state.getHomeTurn() - 1));
			
			if (state.getAwayTurn() != 0)
				state.setAwayTurn(Math.max(1, state.getAwayTurn() - 1));
			
		}
		
	}

	/**
	 * Get the Ref: The fans exact gruesome revenge on the 
	 * referee for some of the dubious decisions he has 
	 * made, either during this match or in the past. His 
	 * replacement is so intimidated that for the rest of the 
	 * half he will not send players from either team off for
	 */
	private void getTheRef() {
		state.setRefAgainstHomeTeam(false);
		state.setRefAgainstAwayTeam(false);
		if (logging)
			GameLog.push("Get the ref! No players from either team will be send off the field for making a foul nor be banned for using secret weapons.");
	}

	private void rollForWeather() {
		D6 da = new D6();
		D6 db = new D6();
		da.roll(); 
		db.roll();
		int roll = da.getResultAsInt() + db.getResultAsInt();
		switch(roll){
			case 2: state.setWeather(Weather.SWELTERING_HEAT); break;
			case 3: state.setWeather(Weather.VERY_SUNNY); break;
			case 4: state.setWeather(Weather.NICE); break;
			case 5: state.setWeather(Weather.NICE); break;
			case 6: state.setWeather(Weather.NICE); break;
			case 7: state.setWeather(Weather.NICE); break;
			case 8: state.setWeather(Weather.NICE); break;
			case 9: state.setWeather(Weather.NICE); break;
			case 10: state.setWeather(Weather.NICE); break;
			case 11: state.setWeather(Weather.POURING_RAIN); break;
			case 12: state.setWeather(Weather.BLIZZARD); break;
		}
		// DEBUG:
		//state.setWeather(Weather.BLIZZARD);
		
		if (logging){
			
			switch(state.getWeather()){
				case SWELTERING_HEAT : GameLog.push("Weather changed to swealtering heat."); break;
				case VERY_SUNNY : GameLog.push("Weather changed to very sunny."); break;
				case NICE : GameLog.push("Weather changed to nice."); break;
				case POURING_RAIN : GameLog.push("Weather changed to pouring rain."); break;
				case BLIZZARD : GameLog.push("Weather changed to blizzard."); break;
			}
		
		}
		
	}

	public Player getSelectedPlayer() {
		return selectedPlayer;
	}
	
	public Player getBlockTarget() {
		return blockTarget;
	}

	public GameState getState() {
		return state;
	}

	public Player getPassTarget() {
		return passTarget;
	}

}