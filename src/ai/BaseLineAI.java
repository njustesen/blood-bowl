package ai;

import java.util.ArrayList;

import models.Ball;
import models.GameState;
import models.PassRange;
import models.Player;
import models.PlayerTurn;
import models.RangeRuler;
import models.Skill;
import models.Square;
import models.Standing;
import models.Team;
import models.Weather;
import models.dice.DiceFace;
import models.dice.IDice;
import Statistics.StatisticManager;
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

public class BaseLineAI extends AIAgent{

	private static final int ACTIVE_PLAYER_PERCENTAGE = 80;
	private static final int GOING_FOR_IT_PERCENTAGE = 20;
	private static long time;
	private ArrayList <Square> moves;
	
	public BaseLineAI(boolean homeTeam) {
		super(homeTeam);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Action turn(GameState state) {
		time = System.nanoTime();
		
		Player player = null;
		
		// Pick active player
//		int r = (int) (Math.random() * 100);
		
//		if (r <= ACTIVE_PLAYER_PERCENTAGE){
			for(Player p : state.getPitch().getPlayersOnPitch(myTeam(state))){
				if (p.getPlayerStatus().getTurn() != PlayerTurn.USED && 
						p.getPlayerStatus().getTurn() != PlayerTurn.UNUSED){
					player = p;
					break;
				}
			}
//		}
		
		// Pick non used player
		ArrayList<Player> usable = new ArrayList<Player>();
		if (player == null){
			for(Player p : state.getPitch().getPlayersOnPitch(myTeam(state))){
				if (p.getPlayerStatus().getTurn() != PlayerTurn.USED && 
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

	@Override
	protected Action decideReroll(GameState state) {
	
		ArrayList<IDice> dices = state.getCurrentDiceRoll().getDices();
		
		int selected = 0;
		int bestValue = 0;
		
		
		if(state.getCurrentBlock() != null){
			
			Player defender = state.getCurrentBlock().getDefender();
			Player attacker = state.getCurrentBlock().getAttacker();
			
			if(attacker.getTeamName() == myTeam(state).getTeamName()){
				
				for(int i = 0; i < dices.size(); i++){
					//DEFENDER_KNOCKED_DOWN
					if(dices.get(i).getResult() == DiceFace.DEFENDER_KNOCKED_DOWN && bestValue < 5){
						selected = i;
						bestValue = 5;
					//PUSH
					}else if(dices.get(i).getResult() == DiceFace.PUSH && bestValue < 4){
						selected = i;
						bestValue = 4;
					//DEFENDER_STUMBLES
					}else if(dices.get(i).getResult() == DiceFace.DEFENDER_STUMBLES){
						//defender has dodge
						if(defender.getSkills().contains(Skill.DODGE) && bestValue < 4){
							selected = i;
							bestValue = 4;
						//defender does NOT have dodge
						}else if(!defender.getSkills().contains(Skill.DODGE) && bestValue < 5){
							selected = i;
							bestValue = 5;
						}
					//BOTH_DOWN
					}else if(dices.get(i).getResult() == DiceFace.BOTH_DOWN){
						//defender has block
						if(defender.getSkills().contains(Skill.BLOCK)){
							//both have block
							if(attacker.getSkills().contains(Skill.BLOCK) && bestValue < 3){
								selected = i;
								bestValue = 3;
							//only defender has block
							}else if(!attacker.getSkills().contains(Skill.BLOCK) && bestValue < 1){
								selected = i;
								bestValue = 1;
							}
						//defender does NOT have block
						}else{
							//only i have block
							if(attacker.getSkills().contains(Skill.BLOCK) && bestValue < 5){
								selected = i;
								bestValue = 5;
								
							//none of us have block
							}else if(!attacker.getSkills().contains(Skill.BLOCK) && bestValue < 2){
								selected = i;
								bestValue = 2;
							}
						}
					//SKULL
					}else if(dices.get(i).getResult() == DiceFace.SKULL && bestValue < 1){
						selected = i;
						bestValue = 1;
					}
					
				}
				
			}else if(defender.getTeamName() == myTeam(state).getTeamName()){
			
				for(int i = 0; i < dices.size(); i++){
					//DEFENDER_KNOCKED_DOWN
					if(dices.get(i).getResult() == DiceFace.DEFENDER_KNOCKED_DOWN && bestValue < 1){
						selected = i;
						bestValue = 1;
					//PUSH
					}else if(dices.get(i).getResult() == DiceFace.PUSH && bestValue < 2){
						selected = i;
						bestValue = 2;
					//DEFENDER_STUMBLES
					}else if(dices.get(i).getResult() == DiceFace.DEFENDER_STUMBLES){
						//defender has dodge
						if(defender.getSkills().contains(Skill.DODGE) && bestValue < 4){
							selected = i;
							bestValue = 4;
						//defender does NOT have dodge
						}else if(!defender.getSkills().contains(Skill.DODGE) && bestValue < 5){
							selected = i;
							bestValue = 5;
						}
					//BOTH_DOWN
					}else if(dices.get(i).getResult() == DiceFace.BOTH_DOWN){
						//defender has block
						if(defender.getSkills().contains(Skill.BLOCK)){
							//both have block
							if(attacker.getSkills().contains(Skill.BLOCK) && bestValue < 3){
								selected = i;
								bestValue = 3;
							//only defender has block
							}else if(!attacker.getSkills().contains(Skill.BLOCK) && bestValue < 5){
								selected = i;
								bestValue = 5;
							}
						//defender does NOT have block
						}else{
							//only attacker have block
							if(attacker.getSkills().contains(Skill.BLOCK) && bestValue < 1){
								selected = i;
								bestValue = 1;
								
							//none of us have block
							}else if(!attacker.getSkills().contains(Skill.BLOCK) && bestValue < 4){
								selected = i;
								bestValue = 4;
							}
						}
					//SKULL
					}else if(dices.get(i).getResult() == DiceFace.SKULL && bestValue < 5){
						selected = i;
						bestValue = 5;
					}
					
				}
			}
		}
		
		return new SelectDieAction(selected);	
	}
	
	@Override
	protected Action pickIntercepter(GameState state) {
		time = System.nanoTime();

		int i = (int) (Math.random() * state.getCurrentPass().getInterceptionPlayers().size());
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new SelectInterceptionAction(state.getCurrentPass().getInterceptionPlayers().get(i));
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
	protected Action pickCoinSide(GameState state) {
		return new SelectCoinSideAction(true);
	}

	@Override
	protected Action pickCoinSideEffect(GameState state) {
		return new SelectCoinTossEffectAction(true);
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
	protected Action placeKick(GameState state) {
		
		time = System.nanoTime();
		
		Square square = state.getPitch().getRandomOpposingSquare(myTeam(state));
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new PlaceBallAction(square);
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
		
		
		for(Player p : state.getPitch().getPlayersOnPitch(myTeam(state))){
			if (p.getPlayerStatus().getTurn() != PlayerTurn.USED && p.getPlayerStatus().getTurn() != PlayerTurn.UNUSED){
				player = p;
				break;
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
	
	//egne metoder
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
		
	
		
		if (state.getPitch().getBall().isUnderControl()){
			
			Player ballCarrier = state.getPitch().getPlayerAt(state.getPitch().getBall().getSquare());
			if (player == ballCarrier){
				ArrayList<Player> inRange = new ArrayList<Player>();
				
				for(Player p : state.getPitch().getPlayersOnPitch(myTeam(state))){
					if (p != player && isInRange(player, p, state)){
						inRange.add(p);
					}
				}
				
				int i = (int) (Math.random() * inRange.size());
				
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
		//System.out.println("BLIIITZ!!!");
		
			if(!myTeam(state).getTeamStatus().hasBlitzed()){
				if(numberOfSurroundingOpponents(state, player.getPosition()) == 0){
					//System.out.println("LOOP 1");
	//				System.out.println("numberOfSurroundingOpponents(state, player.getPosition()) == 0");
					return continueMoveAction(player, state);
				}
			
				// Enemies
				ArrayList<Player> enemies = new ArrayList<Player>();
				
				
				Square playerPos = player.getPosition();
				Square ballPos = state.getPitch().getBall().getSquare();
				
				
				for(int i = -1; i <= 1; i++){
					for(int j = -1; j <= 1; j++){
						Player opponent = state.getPitch().getPlayerAt(new Square(playerPos.getX()+i,playerPos.getY()+j));
						if(opponent != null){
							if(opponent.getTeamName() != player.getTeamName()){
								enemies.add(opponent);
								//System.out.println("LOOP 2");
							}
						}
					}
				}
	//			System.out.println("enemies = "+enemies);
				
				if (!enemies.isEmpty()){
	//				System.out.println("blitz is NOT empty");
					Player selected = enemies.get(0);
					for(Player e: enemies){
						//System.out.println("LOOP 3");
						if(e.getST() < selected.getST()){
							//System.out.println("LOOP 3.1");
							selected = e;
						}
							
						if(e.getPosition().getX() == ballPos.getX() && e.getPosition().getY() == ballPos.getY()){
							//System.out.println("LOOP 3.2");
							selected = e;
						}
						
						if(enemies.size() == 1){
							//System.out.println("LOOP 3.3");
							selected = e;
						}
					}
					//System.out.println("return selected = "+selected);
					return new BlockPlayerAction(player, selected);
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
		
		for(Player e: enemies){
			if(state.getPitch().getBall().getSquare().getX() == e.getPosition().getX() &&
				state.getPitch().getBall().getSquare().getY() == e.getPosition().getY()){
				return new BlockPlayerAction(player, e);
			}
		}
		
		int r = (int) (Math.random() * enemies.size());
		
		StatisticManager.timeSpendByRandomAI += System.nanoTime() - time;
		
		return new BlockPlayerAction(player, enemies.get(r));
		
	}
	
	private Action continueMoveAction(Player player, GameState state) {
		
		//System.out.println("continue move    player = "+player+"   standing at square "+player.getPosition().getX()+","+player.getPosition().getY()+"  moves used="+player.getPlayerStatus().getMovementUsed());
		
		time = System.nanoTime();
		
		if (player.getPlayerStatus().getMovementUsed() >= player.getMA() + 2){
		//	System.out.println("player.getPlayerStatus().getMovementUsed() >= player.getMA() + 2   WHAT??");
			if(moves != null)
				moves.clear();
			return new EndPlayerTurnAction(player);
		}
		
		if (player.getPlayerStatus().getMovementUsed() >= player.getMA()){
		//	if (Math.random() * 100 > calculateGoingForItPercentage(state, player)){
		//	System.out.println("player.getPlayerStatus().getMovementUsed() >= player.getMA()   WHAT??");
			if(moves != null)
				moves.clear();
			return new EndPlayerTurnAction(player);
		//	}
		}
		
		Ball b = state.getPitch().getBall();
		Square playerPos = player.getPosition();
		
		if(player.getPlayerStatus().getMovementUsed() == 0){
		
			//if ball is not on the ground
		if(state.getPitch().getPlayerAt(b.getSquare()) == null){
			
			moves = aStar.findPath(player, b.getSquare(), state, false);
		
		//if SOME player has the ball
		}else{
			
			//if player has the ball
			if(playerPos.getX() == b.getSquare().getX() && playerPos.getY() == b.getSquare().getY()){
				moves = aStar.findPath(player, b.getSquare(), state, true);
			//	System.out.println();
			//	System.out.println("NEW PLAYER!!!");
			//	System.out.println();
			//	System.out.println("player has not used any moves - has the ball   moves.size = "+moves.size());
			}else{ 
			
				//if player from team has the ball
				if(state.getPitch().getPlayerAt(b.getSquare()).getTeamName() == player.getTeamName()){
						
					//if not grouped up
					if(!isGroupedUp(state.getPitch().getPlayerAt(b.getSquare()), state)){
						moves = aStar.findPath(player, groupUp(player, state), state, false);
						
					//find nearest opponent	
					}else{
						Player opponent = getNearestOpponents(player, state).get(0);
					//	System.out.println("opponent = "+opponent);
						moves = aStar.findPath(player, opponent.getPosition(), state, false);
					}
						
				//if opponent has ball or ball is on ground
				}else{
					moves = aStar.findPath(player, b.getSquare(), state, false);
			//		System.out.println();
			//		System.out.println("NEW PLAYER!!!");
			//		System.out.println();
			//		System.out.println("player has not used any moves - doen't have the ball, but is going for it  b="+b.getSquare().getX()+","+b.getSquare().getY()+"   moves.size = "+moves.size()+"   MA="+player.getMA());
				}
			}
		}
	}
		
		if(moves != null){
			if(!moves.isEmpty()){
//				System.out.println("MOVEPLAYERACTION!!!");
				int size = moves.size();
//				System.out.println("moves.size = "+moves.size());
				Square sq = moves.remove(size-1);
//				System.out.println("return new MovePlayerAction(player, sq);  sq = ("+sq.getX()+","+sq.getY()+")");
				return new MovePlayerAction(player, sq);
			}else{}
		}else{}
	
		if(moves != null)
			moves.clear();
		
		return new EndPlayerTurnAction(player);
		
	}
	
	//The higher the number of unused players, the higher the
	private int calculateGoingForItPercentage(GameState state, Player chosenPlayer){
		
		/*		if(chosenPlayer.getMA() > (Math.abs(chosenPlayer.getPosition().getX() - state.getPitch().getBall().getSquare().getX()) +
		  			Math.abs(chosenPlayer.getPosition().getY() - state.getPitch().getBall().getSquare().getY())) &&
					chosenPlayer.getMA() < (Math.abs(chosenPlayer.getPosition().getX() - state.getPitch().getBall().getSquare().getX()) +
				  	Math.abs(chosenPlayer.getPosition().getY() - state.getPitch().getBall().getSquare().getY())+2) &&
				  	state.getPitch().getBall().isOnGround()){
				return 100;
				}
		 */
		
		int usedTurns = 0;
		int numberOfPlayers = state.getPitch().getPlayersOnPitch(myTeam(state)).size();
		
		for(Player p: state.getPitch().getPlayersOnPitch(myTeam(state))){
			if(p.getPlayerStatus().getTurn() == PlayerTurn.USED){
				usedTurns++;
			}
		}
		return 100/numberOfPlayers*usedTurns;
		
	}
	

	
	private ArrayList<Player> getSurroundingPlayers(Player player, GameState state){
		
		Square playerPos = player.getPosition();
		ArrayList<Player> surroundingPlayers = new ArrayList<Player>();

		for(int i = 0; i < 8; i++){
			switch(i){
				case 0: Square up = new Square(playerPos.getX(), playerPos.getY()-1); 
						if(state.getPitch().getPlayerAt(up) != null)surroundingPlayers.add(state.getPitch().getPlayerAt(up)); break;
				case 1: Square upRight = new Square(playerPos.getX()+1, playerPos.getY()-1); 
						if(state.getPitch().getPlayerAt(upRight) != null) surroundingPlayers.add(state.getPitch().getPlayerAt(upRight)); break;
				case 2: Square right = new Square(playerPos.getX()+1, playerPos.getY()); 
						if(state.getPitch().getPlayerAt(right) != null) surroundingPlayers.add(state.getPitch().getPlayerAt(right)); break;
				case 3: Square downRight = new Square(playerPos.getX()+1, playerPos.getY()+1); 
						if(state.getPitch().getPlayerAt(downRight) != null)surroundingPlayers.add(state.getPitch().getPlayerAt(downRight)); break;
				case 4: Square down = new Square(playerPos.getX(), playerPos.getY()+1);
						if(state.getPitch().getPlayerAt(down) != null) surroundingPlayers.add(state.getPitch().getPlayerAt(down)); break;
				case 5: Square downLeft = new Square(playerPos.getX()-1, playerPos.getY()+1); 
						if(state.getPitch().getPlayerAt(downLeft) != null) surroundingPlayers.add(state.getPitch().getPlayerAt(downLeft)); break;
				case 6: Square left = new Square(playerPos.getX()-1, playerPos.getY()); 
						if(state.getPitch().getPlayerAt(left) != null) surroundingPlayers.add(state.getPitch().getPlayerAt(left)); break;
				case 7: Square upLeft = new Square(playerPos.getX()-1, playerPos.getY()-1); 
						if(state.getPitch().getPlayerAt(upLeft) != null) surroundingPlayers.add(state.getPitch().getPlayerAt(upLeft)); break;
				default: break;
			}
		}
		//System.out.println("surroundingPlayers.size = "+surroundingPlayers.size()+"  surroundingPlayers = "+surroundingPlayers);
		return surroundingPlayers;
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
	
	private Action startPlayerAction(Player player, GameState state) {
		
//		System.out.println(player);
		time = System.nanoTime();
		
		PlayerTurn action = null;
		Square playerPos = player.getPosition();
		int i = (int) (Math.random() * 6);
		
		ArrayList<Player> surroundingPlayers = getSurroundingPlayers(player, state);
		
		
		Square ballPosition = state.getPitch().getBall().getSquare();
		
		if(state.getPitch().getPlayerAt(ballPosition) != null && state.getPitch().getPlayerAt(ballPosition).getTeamName() != player.getTeamName()){
			if(canReachPosition(player, ballPosition, state)){
				i = 4;
			}
		}
		if(numberOfSurroundingOpponents(state, player.getPosition()) != 0){
			for(Player p: surroundingPlayers){
				Square playerSquare = p.getPosition();
				if(//state.getPitch().getBall().getSquare().getX() == playerSquare.getX() &&  <--only for ball carriers
					//state.getPitch().getBall().getSquare().getY() == playerSquare.getY() &&
					player.getTeamName() != p.getTeamName()){
	//				System.out.println("GOING FOR KIIIIIILL");
					i = 1;
				}
			}
		}else{
//			System.out.println("GOING FOR MOOOOOVE");
			i = 0;
		}
		
		
		while(true){
			//int i = (int) (Math.random() * 6);
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

}
