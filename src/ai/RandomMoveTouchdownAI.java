package ai;

import java.util.ArrayList;

import Statistics.StatisticManager;
import ai.actions.Action;
import ai.actions.BlockPlayerAction;
import ai.actions.EndPhaseAction;
import ai.actions.EndPlayerTurnAction;
import ai.actions.EndSetupAction;
import ai.actions.FollowUpAction;
import ai.actions.FoulPlayerAction;
import ai.actions.HandOffPlayerAction;
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
import models.Ball;
import models.GameState;
import models.PassRange;
import models.Player;
import models.PlayerTurn;
import models.RangeRuler;
import models.Square;
import models.Standing;
import models.Weather;

public class RandomMoveTouchdownAI extends AIAgent {
	
	private static final int ACTIVE_PLAYER_PERCENTAGE = 80;
	private static final int GOING_FOR_IT_PERCENTAGE = 20;
	
	private static long time;
	private ArrayList <Square> moves;

	public RandomMoveTouchdownAI(boolean homeTeam) {
		super(homeTeam);
	}
	
	@Override
	protected Action decideReroll(GameState state) {
		
		time = System.nanoTime();
		
		int r = (int) (Math.random() * 2);
		if (r == 0){
			return new RerollAction();
		}
		
		r = (int) (Math.random() * state.getCurrentDiceRoll().getDices().size());
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new SelectDieAction(r);
		
	}

	@Override
	protected Action decidePush(GameState state) {
		
		time = System.nanoTime();

		int r = (int) (Math.random() * state.getCurrentBlock().getCurrentPushSquares().size());
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new SelectPushSquareAction(state.getCurrentBlock().getCurrentPushSquares().get(r));
		
	}

	@Override
	protected Action decideFollowUp(GameState state) {
		
		time = System.nanoTime();
		
		double f = Math.random() * 2;
		
		if (f > 1.0)
			return new FollowUpAction(true);
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new FollowUpAction(false);
		
	}
	
	@Override
	protected Action placeBallOnPlayer(GameState state) {
		
		time = System.nanoTime();
		
		int rand = (int) (Math.random() * state.getPitch().playersOnPitch(myTeam(state)));
		Player player = state.getPitch().getPlayersOnPitch(myTeam(state)).get(rand);
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new PlaceBallOnPlayerAction(player);
		
	}

	@Override
	protected Action blitz(GameState state) {
		
		time = System.nanoTime();
		
		Player player = null;
		
		// Pick active player
		int r = (int) (Math.random() * 100);
		
		if (r <= ACTIVE_PLAYER_PERCENTAGE){
			for(Player p : state.getPitch().getPlayersOnPitch(myTeam(state))){
				if (p.getPlayerStatus().getTurn() != PlayerTurn.USED && p.getPlayerStatus().getTurn() != PlayerTurn.UNUSED){
					player = p;
					break;
				}
			}
		}
		
		// Pick non used player
		ArrayList<Player> usable = new ArrayList<Player>();
		if (player == null){
			for(Player p : state.getPitch().getPlayersOnPitch(myTeam(state))){
				if (p.getPlayerStatus().getTurn() != PlayerTurn.USED){
					usable.add(p);
				}
			}
			if (usable.size() != 0){
				int i = (int) (Math.random() * usable.size());
				player = usable.get(i);
			}
		}
		
		// Select action
		if (player != null) {
			switch(player.getPlayerStatus().getTurn()){
			case UNUSED : return startPlayerActionBlitz(player, state);
			case MOVE_ACTION : return continueMoveAction(player, state);
			case BLITZ_ACTION : return continueBlitzAction(player, state);
			case PASS_ACTION : return continuePassAction(player, state);
			case HAND_OFF_ACTION : return continueHandOffAction(player, state);
			case FOUL_ACTION : return continueFoulAction(player, state);
			case USED : break;
			default:
				break;
			}
			
		}
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new EndPhaseAction();
	}

	@Override
	protected Action quickSnap(GameState state) {
		
		time = System.nanoTime();
		
		for(Player p : state.getPitch().getPlayersOnPitch(myTeam(state))){
			
			if (p.getPlayerStatus().getTurn() == PlayerTurn.UNUSED){
				
				Square square = p.getPosition();
				int i = 1 + (int) (Math.random() * 9);
				switch(i){
				case 1 : square = new Square(square.getX()-1, square.getY()-1); break;
				case 2 : square = new Square(square.getX(), square.getY()-1); break;
				case 3 : square = new Square(square.getX()+1, square.getY()-1); break;
				case 4 : square = new Square(square.getX()-1, square.getY()); break;
				case 5 : return new EndPlayerTurnAction(p);
				case 6 : square = new Square(square.getX()+1, square.getY()); break;
				case 7 : square = new Square(square.getX()-1, square.getY()+1); break;
				case 8 : square = new Square(square.getX(), square.getY()+1); break;
				case 9 : square = new Square(square.getX()+1, square.getY()+1); break;
				}
				
				if (state.getPitch().getPlayerAt(square) == null && state.getPitch().isOnPitch(square)){
					StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
					return new MovePlayerAction(p, square);
				} else {
					StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
					return new EndPlayerTurnAction(p);
				}
				
			}
			
		}
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new EndPhaseAction();
	}

	@Override
	protected Action highKick(GameState state) {
		
		time = System.nanoTime();
		
		int rand = (int) (Math.random() * state.getPitch().playersOnPitch(myTeam(state)));
		Player player = state.getPitch().getPlayersOnPitch(myTeam(state)).get(rand);
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new SelectPlayerAction(player);
		
	}
	
	@Override
	protected Action perfectDefense(GameState state) {
		
		return new EndPhaseAction();
		
	}

	@Override
	protected Action placeKick(GameState state) {
		
		time = System.nanoTime();
		
		Square square = state.getPitch().getRandomOpposingSquare(myTeam(state));
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new PlaceBallAction(square);
		
	}

	@Override
	protected Action setup(GameState state) {
		
		time = System.nanoTime();
		
		if (state.getPitch().getDogout(myTeam(state)).getReserves().size() == 0 ||  
				state.getPitch().playersOnPitch(myTeam(state)) == 11){
			
			if (state.getPitch().isSetupLegal(myTeam(state), state.getHalf())){
				StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
				return new EndSetupAction();
			}
			
		}
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return placeRandomPlayer(state);
		
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
		
		time = System.nanoTime();
		
		Player player = null;
		
		// Pick active player
		int r = (int) (Math.random() * 100);
		
		if (r <= ACTIVE_PLAYER_PERCENTAGE){
			for(Player p : state.getPitch().getPlayersOnPitch(myTeam(state))){
				if (p.getPlayerStatus().getTurn() != PlayerTurn.USED && 
						p.getPlayerStatus().getTurn() != PlayerTurn.UNUSED){
					player = p;
					break;
				}
			}
		}
		
		// Pick non used player
		ArrayList<Player> usable = new ArrayList<Player>();
		if (player == null){
			for(Player p : state.getPitch().getPlayersOnPitch(myTeam(state))){
				if (p.getPlayerStatus().getTurn() == PlayerTurn.UNUSED && 
						p.getPlayerStatus().getStanding() != Standing.STUNNED){
					usable.add(p);
				}
			}
			if (usable.size() != 0){
				int i = (int) (Math.random() * usable.size());
				player = usable.get(i);
			}
		}
		
		// Continue action
		if (player != null) {
			switch(player.getPlayerStatus().getTurn()){
			case UNUSED : return startPlayerAction(player, state);
			case MOVE_ACTION : return continueMoveAction(player, state);
			case BLOCK_ACTION : return continueBlockAction(player, state);
			case BLITZ_ACTION : return continueBlitzAction(player, state);
			case PASS_ACTION : return continuePassAction(player, state);
			case HAND_OFF_ACTION : return continueHandOffAction(player, state);
			case FOUL_ACTION : return continueFoulAction(player, state);
			case USED : break;
			}
			
		}
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new EndPhaseAction();
		
	}
	
	private Action continueFoulAction(Player player, GameState state) {
		
		time = System.nanoTime();
		
		double r = Math.random();
		
		if (r > 0.5 || player.getPlayerStatus().hasMovedToBlock()){
			// Enemies
			ArrayList<Player> enemies = new ArrayList<Player>();
			Square playerPos = player.getPosition();
			
			for(int y = -1; y <= 1; y++){
				for(int x = -1; x <= 1; x++){
					Square sq = new Square(playerPos.getX() + x, playerPos.getY() + y);
					Player enemy = state.getPitch().getPlayerAt(sq);
					if (enemy != null && !myTeam(state).getPlayers().contains(enemy) && enemy.getPlayerStatus().getStanding() != Standing.UP){
						enemies.add(enemy);
					}
				}
			}
			
			// Block random enemy if any
			if (!enemies.isEmpty()){
				int rr = (int) (Math.random() * enemies.size());
				
				return new FoulPlayerAction(player, enemies.get(rr));
			}
		}
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return continueMoveAction(player, state);
	}

	private Action continueHandOffAction(Player player, GameState state) {
		
		time = System.nanoTime();
		
		double r = Math.random();
		
		if (r > 0.0 && state.getPitch().getBall().isUnderControl()){
			
			Player ballCarrier = state.getPitch().getPlayerAt(state.getPitch().getBall().getSquare());
			if (player == ballCarrier){
				
				// Team members
				ArrayList<Player> teamMembers = new ArrayList<Player>();
				Square playerPos = player.getPosition();
				
				for(int y = -1; y <= 1; y++){
					for(int x = -1; x <= 1; x++){
						if (x == 0 && y == 0){
							continue;
						}
						Square sq = new Square(playerPos.getX() + x, playerPos.getY() + y);
						Player other = state.getPitch().getPlayerAt(sq);
						if (other != null && myTeam(state).getPlayers().contains(other) && other.getPlayerStatus().getStanding() == Standing.UP){
							teamMembers.add(other);
						}
					}
				}
				if (teamMembers.size() > 0){
					int i = (int) (Math.random() * teamMembers.size());
					
					return new HandOffPlayerAction(player, teamMembers.get(i));
				}
				
			}
			
		}
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return continueMoveAction(player, state);
	}

	private Action continuePassAction(Player player, GameState state) {
		
		time = System.nanoTime();
		
		double r = Math.random();
		
		if (r > 0.5 && state.getPitch().getBall().isUnderControl()){
			
			Player ballCarrier = state.getPitch().getPlayerAt(state.getPitch().getBall().getSquare());
			if (player == ballCarrier){
				ArrayList<Player> inRange = new ArrayList<Player>();
				
				for(Player p : state.getPitch().getPlayersOnPitch(myTeam(state))){
					if (p != player && isInRange(player, p, state)){
						inRange.add(p);
					}
				}
				
				if (inRange.isEmpty())
					return new EndPlayerTurnAction(player);
				
				int i = (int) (Math.random() * inRange.size());
				i = Math.min(i, inRange.size());
				
				return new PassPlayerAction(player, inRange.get(i));
			}
			
		}
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return continueMoveAction(player, state);
		
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

	private Action continueBlitzAction(Player player, GameState state) {
		
		time = System.nanoTime();
		
		double r = Math.random();
		
		if (r > 0.5 || player.getPlayerStatus().hasMovedToBlock()){
			
			if (myTeam(state).getTeamStatus().hasBlitzed())
				return new EndPlayerTurnAction(player);
			
			// Enemies
			ArrayList<Player> enemies = new ArrayList<Player>();
			Square playerPos = player.getPosition();
			
			for(int y = -1; y <= 1; y++){
				for(int x = -1; x <= 1; x++){
					Square sq = new Square(playerPos.getX() + x, playerPos.getY() + y);
					Player enemy = state.getPitch().getPlayerAt(sq);
					if (enemy != null && !myTeam(state).getPlayers().contains(enemy) && enemy.getPlayerStatus().getStanding() == Standing.UP){
						enemies.add(enemy);
					}
				}
			}
			
			// Block random enemy if any
			if (!enemies.isEmpty()){
				int rr = (int) (Math.random() * enemies.size());
				
				return new BlockPlayerAction(player, enemies.get(rr));
			}
		}
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return continueMoveAction(player, state);
	}

	private Action continueBlockAction(Player player, GameState state) {
		
		time = System.nanoTime();
		
		// Enemies
		ArrayList<Player> enemies = new ArrayList<Player>();
		Square playerPos = player.getPosition();
		
		for(int y = -1; y <= 1; y++){
			for(int x = -1; x <= 1; x++){
				Square sq = new Square(playerPos.getX() + x, playerPos.getY() + y);
				Player enemy = state.getPitch().getPlayerAt(sq);
				if (enemy != null && !myTeam(state).getPlayers().contains(enemy) && enemy.getPlayerStatus().getStanding() == Standing.UP){
					enemies.add(enemy);
				}
			}
		}
		
		// Block random enemy if any
		if (enemies.isEmpty()){
			return new EndPlayerTurnAction(player);
		}
		
		int r = (int) (Math.random() * enemies.size());
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new BlockPlayerAction(player, enemies.get(r));
		
	}

	private Action continueMoveAction(Player player, GameState state) {
		
		time = System.nanoTime();
		
		if (player.getPlayerStatus().getMovementUsed() >= player.getMA() + 2){
			if(moves != null)
				moves.clear();
			return new EndPlayerTurnAction(player);
		}
		
		if (player.getPlayerStatus().getMovementUsed() >= player.getMA()){
			if(moves != null)
				moves.clear();
			return new EndPlayerTurnAction(player);
		}
		
		Ball b = state.getPitch().getBall();
		Square playerPos = player.getPosition();
		
		if(player.getPlayerStatus().getMovementUsed() < player.getMA()){
		
			//if ball is not on the ground
			if(b.getSquare() != null && state.getPitch().getPlayerAt(b.getSquare()) == null){
				
				moves = aStar.findPath(player, b.getSquare(), state, false);
				if (moves.size() > player.getMA() - player.getPlayerStatus().getMovementUsed()
						|| player.getAG() < 3){
					moves = null;
				}
			
			//if SOME player has the ball
			}else{
				
				//if player has the ball
				if(playerPos.equals(b.getSquare())){
					moves = aStar.findPath(player, b.getSquare(), state, true);
				}else{ 
					return continueRandomMoveAction(player, state);
				}
			}
		}
		
		if(moves != null){
			if(!moves.isEmpty()){
				int size = moves.size();
				Square sq = moves.remove(size-1);
				return new MovePlayerAction(player, sq);
			}else{}
		}else{}
	
		if(moves != null)
			moves.clear();
		
		return continueRandomMoveAction(player, state);
		
	}
	
	private Action continueRandomMoveAction(Player player, GameState state) {
		
		time = System.nanoTime();
		
		if (player.getPlayerStatus().getMovementUsed() > 0 && player.getPlayerStatus().getStanding() == Standing.DOWN){
			return new EndPlayerTurnAction(player);
		}
		
		if (player.getPlayerStatus().getMovementUsed() >= player.getMA() + 2){
			return new EndPlayerTurnAction(player);
		}
		
		if (player.getPlayerStatus().getMovementUsed() >= player.getMA()){
			//if (Math.random() * 100 < 1){
				//return new EndPlayerTurnAction(player);
			//}
		}
		
		ArrayList<Square> squares = new ArrayList<Square>();
		Square playerPos = player.getPosition();
		
		for(int y = -1; y <= 1; y++){
			for(int x = -1; x <= 1; x++){
				Square sq = new Square(playerPos.getX() + x, playerPos.getY() + y);
				if (state.getPitch().isOnPitch(sq) && state.getPitch().getPlayerAt(sq) == null){
					squares.add(sq);
				}
			}
		}
		
		if (squares.isEmpty()){
			return new EndPlayerTurnAction(player);
		}
		
		int i = (int) (Math.random() * squares.size());
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new MovePlayerAction(player, squares.get(i));
		
	}

	private Action startPlayerAction(Player player, GameState state) {
		
		time = System.nanoTime();
		
		PlayerTurn action = null;
		
		while(true){
			int i = (int) (Math.random() * 6);
			//int i = 3;
			switch(i){
			case 0: action = PlayerTurn.MOVE_ACTION; break;
			case 1: action = PlayerTurn.BLOCK_ACTION; break;
			case 2: action = PlayerTurn.PASS_ACTION; break;
			case 3: action = PlayerTurn.HAND_OFF_ACTION; break;
			case 4: action = PlayerTurn.BLITZ_ACTION; break;
			case 5: action = PlayerTurn.FOUL_ACTION; break;
			}
			
			if(action == PlayerTurn.BLOCK_ACTION && player.getPlayerStatus().getStanding() != Standing.UP){
				action = PlayerTurn.MOVE_ACTION;
			}
			if (action == PlayerTurn.HAND_OFF_ACTION && myTeam(state).getTeamStatus().hasHandedOf()){
				action = PlayerTurn.MOVE_ACTION;
			}
			if (action == PlayerTurn.PASS_ACTION && myTeam(state).getTeamStatus().hasPassed()){
				action = PlayerTurn.MOVE_ACTION;
			}
			if (action == PlayerTurn.BLITZ_ACTION && myTeam(state).getTeamStatus().hasBlitzed()){
				action = PlayerTurn.MOVE_ACTION;
			}
			if (action == PlayerTurn.FOUL_ACTION && myTeam(state).getTeamStatus().hasFouled()){
				action = PlayerTurn.MOVE_ACTION;
			}
			
			break;
		}
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new SelectPlayerTurnAction(action, player);
	}
	

	private Action startPlayerActionBlitz(Player player, GameState state) {
		
		time = System.nanoTime();
		
		PlayerTurn action = null;

		if (myTeam(state).getTeamStatus().hasBlitzed()){
			action = PlayerTurn.MOVE_ACTION;
		} else {
			int i = (int) (Math.random() * 2);
			switch(i){
			case 0: action = PlayerTurn.MOVE_ACTION; break;
			case 1: action = PlayerTurn.BLITZ_ACTION; break;
			}
		}
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new SelectPlayerTurnAction(action, player);
		
	}

	private Action placeRandomPlayer(GameState state) {
		
		time = System.nanoTime();

		if (state.getPitch().getDogout(myTeam(state)).getReserves().isEmpty()){
			return new EndPhaseAction();
		}
		
		int rand = (int) (Math.random() * state.getPitch().getDogout(myTeam(state)).getReserves().size());
		Player player = state.getPitch().getDogout(myTeam(state)).getReserves().get(rand);
		Square square = new Square(1,1);
		
		if (state.getPitch().playersOnScrimmage(myTeam(state)) < 3){
			
			square = state.getPitch().getRandomFreeScrimmageSquare(myTeam(state));
			
		} else if (state.getPitch().playersOnTopWideZones(myTeam(state)) < 2 && 
				state.getPitch().playersOnBottomWideZones(myTeam(state)) < 2){
			
			square = state.getPitch().getRandomFreeSquare(myTeam(state));
			
		} else {
			
			square = state.getPitch().getRandomFreeMiddleSquare(myTeam(state));
			
		}
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new PlacePlayerAction(player, square);
		
	}


	public boolean isHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(boolean homeTeam) {
		this.homeTeam = homeTeam;
	}

	@Override
	protected Action pickIntercepter(GameState state) {
		
		time = System.nanoTime();

		int i = (int) (Math.random() * state.getCurrentPass().getInterceptionPlayers().size());
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new SelectInterceptionAction(state.getCurrentPass().getInterceptionPlayers().get(i));
		
	}

	
}