package ai.monteCarlo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sound.FakeSoundManager;

import models.GameStage;
import models.GameState;
import models.PassRange;
import models.Player;
import models.PlayerTurn;
import models.RangeRuler;
import models.Square;
import models.Standing;
import models.Team;
import models.Weather;
import ai.AIAgent;
import ai.RandomAI;
import ai.RandomMoveTouchdownAI;
import ai.RandomTouchdownAI;
import ai.actions.Action;
import ai.actions.BlockPlayerAction;
import ai.actions.EndPhaseAction;
import ai.actions.EndPlayerTurnAction;
import ai.actions.EndSetupAction;
import ai.actions.FollowUpAction;
import ai.actions.FoulPlayerAction;
import ai.actions.HandOffPlayerAction;
import ai.actions.MovePlayerAction;
import ai.actions.PassPlayerAction;
import ai.actions.PlaceBallAction;
import ai.actions.PlaceBallOnPlayerAction;
import ai.actions.PlacePlayerAction;
import ai.actions.RerollAction;
import ai.actions.SelectCoinSideAction;
import ai.actions.SelectCoinTossEffectAction;
import ai.actions.SelectDieAction;
import ai.actions.SelectInterceptionAction;
import ai.actions.SelectPlayerAction;
import ai.actions.SelectPlayerTurnAction;
import ai.actions.SelectPushSquareAction;
import ai.util.GameStateCloner;
import game.GameMaster;

public class FlatMonteCarloAI extends AIAgent{
	
	private static long time;

	private static final int MULTIPLIER = 2;
	private static final int FEW = 1 * MULTIPLIER;
	private static final int MEDIUM = 2 * MULTIPLIER;
	private static final int MANY = 4 * MULTIPLIER;
	private static final int INSANE = 10 * MULTIPLIER;

	private boolean heuristics;
	private AIAgent homeAgent;
	private AIAgent awayAgent;
	
	public FlatMonteCarloAI(boolean homeTeam, boolean heuristics, AIAgent homeAgent, AIAgent awayAgent) {
		super(homeTeam);
		this.heuristics = heuristics;
		this.homeAgent = homeAgent;
		this.awayAgent = awayAgent;
	}
	
	private Action search(List<Action> possibleActions, GameState state, int simulations){
		
		//MCTSNode root = new MCTSNode(null, null);
		double bestSum = -100000.0;
		GameStateCloner cloner = new GameStateCloner();
		ArrayList<Double> sums = new ArrayList<Double>();
		
		for(Action a : possibleActions){
			
			double sum = 0.0;
			
			for(int i = 0; i < simulations; i++){
				Date now = new Date();
				double result = 0.0;
				
				GameState as = cloner.clone(state);
				GameMaster gameMaster = new GameMaster(as, homeAgent, awayAgent, true, false);
				gameMaster.setSoundManager(new FakeSoundManager());
				gameMaster.performAIAction(a);
				
				int lastHalf = 0;
				int lastX = 0;
				Player lastBallCarrier = null;
				while(as.getGameStage() != GameStage.GAME_ENDED){
					if (heuristics && lastHalf == 1 && as.getHalf() == 2){
						break;
					}
					
					if (heuristics && as.getPitch().getBall().isOnGround() && as.getPitch().getBall().getSquare() != null){
						lastX = as.getPitch().getBall().getSquare().getX();
						lastBallCarrier = as.getPitch().getPlayerAt(as.getPitch().getBall().getSquare());
					}
					gameMaster.update();
				}
				
				sum += evaluate(as, lastX, lastBallCarrier);
				Date newNow = new Date();
				
			}
			
			System.out.println("sum: " + sum);
			sums.add(sum);
			
		}
		
		ArrayList<Action> bestActions = new ArrayList<Action>();
		for (int i = 0; i < sums.size(); i++){
			if (sums.get(i) > bestSum){
				bestActions = new ArrayList<Action>();
				bestActions.add(possibleActions.get(i));
				bestSum = sums.get(i);
			} else if (sums.get(i) == bestSum){
				bestActions.add(possibleActions.get(i));
			}
		}
		
		return pickRandom(bestActions);
	}	

	private double evaluate(GameState state, int lastX, Player lastBallCarrier) {
		double result = 0.0;
		if (state.getHomeTeam().getTeamStatus().getScore() > state.getAwayTeam().getTeamStatus().getScore()){
			if (myTeam(state) == state.getHomeTeam()){
				result++;
			} else {
				result--;
			}
		} else if (state.getHomeTeam().getTeamStatus().getScore() < state.getAwayTeam().getTeamStatus().getScore()){
			if (myTeam(state) == state.getHomeTeam()){
				result--;
			} else {
				result++;
			}
		}
		
		if (heuristics && result == 0.0){
			
			// Ball position
			if (state.getPitch().getBall().isOnGround()){
				double x = lastX;
				x -= 13.0;
				if (myTeam(state) == state.getHomeTeam()){
					result = x/13.0/2.0;
				} else {
					result = -x/13.0/2.0;
				}
			}
			
			// Injuries
			/*
			int awayKOs = state.getPitch().getAwayDogout().getKnockedOut().size();
			int awayDnI = state.getPitch().getAwayDogout().getDeadAndInjured().size();
			int homeKOs = state.getPitch().getHomeDogout().getKnockedOut().size();
			int homeDnI = state.getPitch().getHomeDogout().getDeadAndInjured().size();
			
			int injSocre = (awayKOs - homeKOs) + (awayDnI - homeDnI) * 3;
			if (myTeam(state) == state.getHomeTeam()){
				result += injSocre;
			} else {
				result -= injSocre;
			}
			*/
		}
		
		return result;
	}

	private Action pickRandom(ArrayList<Action> bestActions) {
		int x = (int) (Math.random() * bestActions.size());
		return bestActions.get(x);
	}

	@Override
	protected Action decideReroll(GameState state) {
		
		ArrayList<Action> actions = new ArrayList<Action>();
		
		if ((state.getGameStage() == GameStage.HOME_TURN && homeTeam) || 
				(state.getGameStage() == GameStage.AWAY_TURN && !homeTeam)){
			
			if (!myTeam(state).getTeamStatus().rerolledThisTurn())
				actions.add(new RerollAction());
			
		}
		
		for(int i = 0; i < state.getCurrentDiceRoll().getDices().size(); i++){
			actions.add(new SelectDieAction(i));
		}
		
		return search(actions, state, INSANE);
		
	}

	@Override
	protected Action decidePush(GameState state) {

		ArrayList<Action> actions = new ArrayList<Action>();
		
		for(int i = 0; i < state.getCurrentBlock().getCurrentPushSquares().size(); i++){
			actions.add(new SelectPushSquareAction(state.getCurrentBlock().getCurrentPushSquares().get(i)));
		}
		
		return search(actions, state, INSANE);
		
	}

	@Override
	protected Action decideFollowUp(GameState state) {
		
		ArrayList<Action> actions = new ArrayList<Action>();
		
		actions.add( new FollowUpAction(true) );
		actions.add( new FollowUpAction(false) );
		
		return search(actions, state, INSANE);
		
	}
	
	@Override
	protected Action placeBallOnPlayer(GameState state) {
		
		ArrayList<Action> actions = new ArrayList<Action>();
		
		for(Player p : state.getPitch().getPlayersOnPitch(myTeam(state)) ){
			
			actions.add(new PlaceBallOnPlayerAction(p));
			
		}
		
		return search(actions, state, MANY);
		
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	protected Action blitz(GameState state) {
		
		ArrayList<Action> actions = new ArrayList<Action>();
		
		// Pick non used player
		ArrayList<Player> usable = new ArrayList<Player>();
		Player active = null;
		for(Player p : state.getPitch().getPlayersOnPitch(myTeam(state))){
			if (p.getPlayerStatus().getTurn() != PlayerTurn.USED && 
					p.getPlayerStatus().getStanding() != Standing.STUNNED){
				if (p.getPlayerStatus().getTurn() != PlayerTurn.UNUSED){
					active = p;
					break;
				}
				usable.add(p);
			}
		}
		
		if (active == null && !usable.isEmpty()){
			if (usable.size() == 1){
				active = usable.get(0);
			} else {
				active = findActivePlayer(state, usable);
			}
		}
		
		if (active != null){
			switch(active.getPlayerStatus().getTurn()){
			case UNUSED : actions.addAll( startPlayerActions(active, state, true, false) ); break;
			case MOVE_ACTION : actions.addAll( continuedMoveActions(active, state) ); break;
			case BLITZ_ACTION : actions.addAll( continuedBlitzActions(active, state) ); break;
			case USED : break;
			}
		}
		
		actions.add( new EndPhaseAction() );
		
		return search(actions, state, MANY);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	protected Action quickSnap(GameState state) {
		
		ArrayList<Action> actions = new ArrayList<Action>();
		
		// Pick non used player
		ArrayList<Player> usable = new ArrayList<Player>();
		Player active = null;
		for(Player p : state.getPitch().getPlayersOnPitch(myTeam(state))){
			if (p.getPlayerStatus().getTurn() != PlayerTurn.USED && 
					p.getPlayerStatus().getStanding() != Standing.STUNNED){
				if (p.getPlayerStatus().getTurn() != PlayerTurn.UNUSED){
					active = p;
					break;
				}
				usable.add(p);
			}
		}
		
		if (active == null && !usable.isEmpty()){
			if (usable.size() == 1){
				active = usable.get(0);
			} else {
				active = findActivePlayer(state, usable);
			}
		}
		
		if (active != null){
			switch(active.getPlayerStatus().getTurn()){
			case UNUSED : actions.addAll( startPlayerActions(active, state, false, true) ); break;
			case MOVE_ACTION : actions.addAll( continuedMoveActions(active, state) ); break;
			case USED : break;
			}
		}
		
		actions.add( new EndPhaseAction() );
		
		return search(actions, state, MANY);
	}

	@Override
	protected Action highKick(GameState state) {
		
		ArrayList<Action> actions = new ArrayList<Action>();
		
		for(Player p : state.getPitch().getPlayersOnPitch(myTeam(state)) ){
			
			actions.add(new SelectPlayerAction(p));
			
		}
		
		return search(actions, state, INSANE);
		
	}
	
	@Override
	protected Action perfectDefense(GameState state) {
		
		return new EndPhaseAction();
		
	}

	@Override
	protected Action placeKick(GameState state) {
		
		ArrayList<Action> actions = new ArrayList<Action>();
		
		for(Square square : state.getPitch().getOpposingSquares(myTeam(state)) ){
			
			actions.add(new PlaceBallAction(square));
			
		}
		
		return search(actions, state, 1);
		
	}

	@Override
	protected Action setup(GameState state) {
		
		ArrayList<Action> actions = new ArrayList<Action>();
		
		if (state.getPitch().getDogout(myTeam(state)).getReserves().size() == 0 ||  
				state.getPitch().playersOnPitch(myTeam(state)) == 11){
			
			if (state.getPitch().isSetupLegal(myTeam(state), state.getHalf())){
				return new EndSetupAction();
			}
			
		}
			
		if (state.getPitch().playersOnScrimmage(myTeam(state)) < 3){
			// Scrimmage
			for(Square square : state.getPitch().getScrimmageSquares(myTeam(state))){
				if (state.getPitch().getPlayerAt(square) == null){
					for (Player p : state.getPitch().getDogout(myTeam(state)).getReserves()){
						actions.add(new PlacePlayerAction(p, square));
					}
				}
			}
		} else {
	
			Player player = state.getPitch().getDogout(myTeam(state)).getReserves().get(0);
			
			int top = state.getPitch().playersOnTopWideZones(myTeam(state));
			int bottom = state.getPitch().playersOnBottomWideZones(myTeam(state));
			
			for(Square square : state.getPitch().getTeamSquares(myTeam(state))){
				if (state.getPitch().getPlayerAt(square) == null){
					if (top >= 2 && state.getPitch().isInTopWideZone(square)){
					} else if (bottom >= 2 && state.getPitch().isInBottomWideZone(square)){
					} else {
						actions.add(new PlacePlayerAction(player, square));
					}
				}
			}
			
		}
		
		return search(actions, state, 1);
		
	}

	@Override
	protected Action pickCoinSideEffect(GameState state) {
		return new SelectCoinTossEffectAction(true);
	}

	@Override
	protected Action pickCoinSide(GameState state) {
		return new SelectCoinSideAction(true);
	}

	@Override
	protected Action turn(GameState state) {
		
		ArrayList<Action> actions = new ArrayList<Action>();
		
		// Pick non used player
		ArrayList<Player> usable = new ArrayList<Player>();
		Player active = null;
		for(Player p : state.getPitch().getPlayersOnPitch(myTeam(state))){
			if (p.getPlayerStatus().getTurn() != PlayerTurn.USED && 
					p.getPlayerStatus().getStanding() != Standing.STUNNED){
				if (p.getPlayerStatus().getTurn() != PlayerTurn.UNUSED){
					active = p;
					break;
				}
				usable.add(p);
			}
		}
		
		if (active == null && !usable.isEmpty()){
			if (usable.size() == 1){
				active = usable.get(0);
			} else {
				active = findActivePlayer(state, usable);
			}
		}
		
		if (active != null){
			switch(active.getPlayerStatus().getTurn()){
			case UNUSED : actions.addAll( startPlayerActions(active, state, false, false) ); break;
			case MOVE_ACTION : actions.addAll( continuedMoveActions(active, state) ); break;
			case BLOCK_ACTION : actions.addAll( continuedBlockActions(active, state) ); break;
			case BLITZ_ACTION : actions.addAll( continuedBlitzActions(active, state) ); break;
			case PASS_ACTION : actions.addAll( continuedPassActions(active, state) ); break;
			case HAND_OFF_ACTION : actions.addAll( continuedHandOffActions(active, state) ); break;
			case FOUL_ACTION : actions.addAll( continuedFoulActions(active, state) ); break;
			case USED : break;
			}
		} else {
			actions.add( new EndPhaseAction() );
		}
		
		//actions.add( new EndPhaseAction() );
		
		return search(actions, state, INSANE);
		
	}
	
	private Player findActivePlayer(GameState state, ArrayList<Player> players) {
		
		double bestSum = 0;
		Player bestPlayer = null;
		GameStateCloner cloner = new GameStateCloner();
		
		for(Player p : players){
			
			double sum = 0;
			
			for(int i = 0; i < FEW; i++){
				Date now = new Date();
				double result = 0.0;
				
				GameState as = cloner.clone(state);
				GameMaster gameMaster = new GameMaster(as, homeAgent, awayAgent, true, false);
				gameMaster.setSoundManager(new FakeSoundManager());
				
				Player clonedPlayer = findIdenticalPlayer(p, myTeam(as), state, as);
				Action randAction = new SelectPlayerTurnAction(getRandomPlayerTurn(), clonedPlayer);
				gameMaster.performAIAction(randAction);
				
				int lastHalf = 0;
				int lastX = 0;
				Player lastBallCarrier = null;
				while(as.getGameStage() != GameStage.GAME_ENDED){
					if (lastHalf == 1 && as.getHalf() == 2){
						break;
					}
					if (as.getPitch().getBall().isOnGround() && as.getPitch().getBall().getSquare() != null){
						lastX = as.getPitch().getBall().getSquare().getX();
						lastBallCarrier = as.getPitch().getPlayerAt(as.getPitch().getBall().getSquare());
					}
					gameMaster.update();
				}
				
				if (as.getHomeTeam().getTeamStatus().getScore() > as.getAwayTeam().getTeamStatus().getScore()){
					if (myTeam(as) == as.getHomeTeam()){
						result++;
					} else {
						result--;
					}
				} else if (as.getHomeTeam().getTeamStatus().getScore() < as.getAwayTeam().getTeamStatus().getScore()){
					if (myTeam(as) == as.getHomeTeam()){
						result--;
					} else {
						result++;
					}
				}
				
				if (heuristics && result == 0.0){
					
					// Ball position
					if (as.getPitch().getBall().isOnGround()){
						double x = lastX;
						x -= 13.0;
						if (myTeam(as) == as.getHomeTeam()){
							result = x/13.0/2.0;
						} else {
							result = -x/13.0/2.0;
						}
					}
					
					// Ball possession
					if (as.getPitch().getBall().isUnderControl()){
						Player player = lastBallCarrier;
						if (player != null && player.getTeamName().equals(myTeam(as).getTeamName())){
							result += 0.5;
						} else if (player != null && !player.getTeamName().equals(myTeam(as).getTeamName())){
							result -= 0.5;
						}
					}
				}
				
				Date newNow = new Date();
				sum += result;
				
			}
			
			if (bestPlayer == null || 
					(sum > bestSum && homeTeam) || 
					(sum < bestSum && !homeTeam)){
				bestSum = sum;
				bestPlayer = p;
			}
		}
		
		return bestPlayer;
		
	}

	private Player findIdenticalPlayer(Player player, Team newTeam, GameState oldState, GameState newState) {

		for(Player p : newTeam.getPlayers()){
			if (p.getNumber() == player.getNumber()){
				return p;
			}
		}
		
		return null;
	}

	private PlayerTurn getRandomPlayerTurn() {

		int i = (int) (Math.random() * 6);
		switch(i){
		case 0: return PlayerTurn.BLITZ_ACTION;
		case 1: return PlayerTurn.BLOCK_ACTION;
		case 2: return PlayerTurn.FOUL_ACTION;
		case 3: return PlayerTurn.HAND_OFF_ACTION;
		case 4: return PlayerTurn.MOVE_ACTION;
		case 5: return PlayerTurn.PASS_ACTION;
		}
		return PlayerTurn.MOVE_ACTION;
	}

	private ArrayList<Action> continuedFoulActions(Player player, GameState state) {
		
		ArrayList<Action> actions = new ArrayList<Action>();

		// Enemies
		Square playerPos = player.getPosition();
			
		if (!myTeam(state).getTeamStatus().hasFouled()){
			for(int y = -1; y <= 1; y++){
				for(int x = -1; x <= 1; x++){
					Square sq = new Square(playerPos.getX() + x, playerPos.getY() + y);
					Player enemy = state.getPitch().getPlayerAt(sq);
					if (enemy != null && !myTeam(state).getPlayers().contains(enemy) && enemy.getPlayerStatus().getStanding() != Standing.UP){
						actions.add(new FoulPlayerAction(player, enemy));
					}
				}
			}
		}
		
		actions.add(new EndPlayerTurnAction(player));
		return continuedMoveActions(player, state);
	}

	private ArrayList<Action> continuedHandOffActions(Player player, GameState state) {
		
		ArrayList<Action> actions = new ArrayList<Action>();
		
		if (state.getPitch().getBall().isUnderControl()){
			
			Player ballCarrier = state.getPitch().getPlayerAt(state.getPitch().getBall().getSquare());
			if (player == ballCarrier){
				
				// Team members
				Square playerPos = player.getPosition();
				
				for(int y = -1; y <= 1; y++){
					for(int x = -1; x <= 1; x++){
						if (x == 0 && y == 0){
							continue;
						}
						Square sq = new Square(playerPos.getX() + x, playerPos.getY() + y);
						Player other = state.getPitch().getPlayerAt(sq);
						if (other != null && myTeam(state).getPlayers().contains(other) && other.getPlayerStatus().getStanding() == Standing.UP){
							actions.add(new HandOffPlayerAction(player, other) );
						}
					}
				}
			}
			
			actions.add(new EndPlayerTurnAction(player));
			return actions;
			
		}
		
		actions.addAll( continuedMoveActions(player, state) );
		
		actions.add(new EndPlayerTurnAction(player));
		return actions;
		
	}

	private ArrayList<Action> continuedPassActions(Player player, GameState state) {
		
		ArrayList<Action> actions = new ArrayList<Action>();
		
		if (state.getPitch().getBall().isUnderControl()){
			
			Player ballCarrier = state.getPitch().getPlayerAt(state.getPitch().getBall().getSquare());
			if (player == ballCarrier){
				
				for(Player p : state.getPitch().getPlayersOnPitch(myTeam(state))){
					if (p != player && isInRange(player, p, state)){
						actions.add(new PassPlayerAction(player, p));
					}
				}
			}
			
		}
		
		actions.addAll( continuedMoveActions(player, state) );
		
		actions.add(new EndPlayerTurnAction(player));
		
		return actions;
		
	}

	private boolean isInRange(Player passer, Player catcher, GameState state) {
		Square a = passer.getPosition();
		Square b = catcher.getPosition();
		int x = (a.getX() - b.getX()) * (a.getX() - b.getX());
		int y = (a.getY() - b.getY()) * (a.getY() - b.getY());
		int distance = (int) Math.sqrt(x + y);
		PassRange range = RangeRuler.getPassRange(distance);
		if (state.getWeather() == Weather.BLIZZARD){
			if (range == PassRange.LONG_BOMB || range == PassRange.LONG_PASS){
				return false;
			}
		}
		if (range == PassRange.OUT_OF_RANGE){
			return false;
		}
		return true;
	}

	private ArrayList<Action> continuedBlitzActions(Player player, GameState state) {
		
		ArrayList<Action> actions = new ArrayList<Action>();

		if (!myTeam(state).getTeamStatus().hasBlitzed()){
			for(int y = -1; y <= 1; y++){
				for(int x = -1; x <= 1; x++){
					Square sq = new Square(player.getPosition().getX() + x, player.getPosition().getY() + y);
					Player enemy = state.getPitch().getPlayerAt(sq);
					if (enemy != null && !myTeam(state).getPlayers().contains(enemy) && enemy.getPlayerStatus().getStanding() == Standing.UP){
						actions.add( new BlockPlayerAction(player, enemy) );
					}
				}
			}
		}
		
		actions.addAll(continuedMoveActions(player, state));
		
		return actions;
	}

	private ArrayList<Action> continuedBlockActions(Player player, GameState state) {
		
		// Enemies
		ArrayList<Action> actions = new ArrayList<Action>();
		
		for(int y = -1; y <= 1; y++){
			for(int x = -1; x <= 1; x++){
				Square sq = new Square(player.getPosition().getX() + x, player.getPosition().getY() + y);
				Player enemy = state.getPitch().getPlayerAt(sq);
				if (enemy != null && !myTeam(state).getPlayers().contains(enemy) && enemy.getPlayerStatus().getStanding() == Standing.UP){
					actions.add(new BlockPlayerAction(player, enemy));
				}
			}
		}
		
		actions.add(new EndPlayerTurnAction(player));
		
		return actions;
		
	}

	private ArrayList<Action> continuedMoveActions(Player player, GameState state) {
		
		ArrayList<Action> actions = new ArrayList<Action>();
	
		if (player.getPlayerStatus().getMovementUsed() == 1 && state.getGameStage() == GameStage.QUICK_SNAP){
			actions.add(new EndPlayerTurnAction(player));
			return actions;
		}
		
		if (player.getPlayerStatus().getMovementUsed() == player.getMA() + 2){
			actions.add(new EndPlayerTurnAction(player));
			return actions;
		}
		
		for(int y = -1; y <= 1; y++){
			for(int x = -1; x <= 1; x++){
				Square sq = new Square(player.getPosition().getX() + x, player.getPosition().getY() + y);
				if (state.getPitch().isOnPitch(sq) && state.getPitch().getPlayerAt(sq) == null){
					actions.add(new MovePlayerAction(player, sq));
				}
			}
		}

		actions.add(new EndPlayerTurnAction(player));
		return actions;
		
	}

	private ArrayList<Action> startPlayerActions(Player player, GameState state, boolean blitzPhase, boolean quickPhase) {
		
		ArrayList<Action> actions = new ArrayList<Action>();
		
		actions.add( new SelectPlayerTurnAction(PlayerTurn.MOVE_ACTION, player) );
		
		if (!myTeam(state).getTeamStatus().hasBlitzed() && !quickPhase){
			actions.add( new SelectPlayerTurnAction(PlayerTurn.BLITZ_ACTION, player) );
		}
		
		if (!blitzPhase && !quickPhase){
			if (player.getPlayerStatus().getStanding() == Standing.UP){
				actions.add( new SelectPlayerTurnAction(PlayerTurn.BLOCK_ACTION, player) );
			}
			if (!myTeam(state).getTeamStatus().hasPassed()){
				actions.add( new SelectPlayerTurnAction(PlayerTurn.PASS_ACTION, player) );
			}
			if (!myTeam(state).getTeamStatus().hasHandedOf()){
				actions.add( new SelectPlayerTurnAction(PlayerTurn.HAND_OFF_ACTION, player) );
			}
			if (!myTeam(state).getTeamStatus().hasFouled()){
				actions.add( new SelectPlayerTurnAction(PlayerTurn.FOUL_ACTION, player) );
			}
		}
		
		return actions;
	}
	
	@Override
	protected Action pickIntercepter(GameState state) {
		
		ArrayList<Action> actions = new ArrayList<Action>();
		
		for(Player p : state.getCurrentPass().getInterceptionPlayers()){
			
			actions.add(new SelectInterceptionAction(p));
			
		}
		
		return search(actions, state, MANY);
		
	}

	public boolean isHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(boolean homeTeam) {
		this.homeTeam = homeTeam;
	}

	
	
}
