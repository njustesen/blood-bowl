package ai;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import javax.swing.JPanel;

import Statistics.StatisticManager;
import ai.actions.Action;
import game.GameMaster;
import models.Ball;
import models.GameStage;
import models.GameState;
import models.Pitch;
import models.Player;
import models.Square;
import models.Standing;
import models.Team;

public abstract class AIAgent {
	
	protected boolean homeTeam;
	protected AStar aStar = new AStar();
	
	public AIAgent(boolean homeTeam) {
		this.homeTeam = homeTeam;
	}

	public Action takeAction(GameMaster master, GameState state) {
		
		StatisticManager.actions++;
		
		if (state.getGameStage() == GameStage.HOME_TURN || 
				state.getGameStage() == GameStage.AWAY_TURN){

			if (state.isAwaitingReroll() && state.getCurrentDiceRoll() != null)
				return decideReroll(state);
			
			if (state.isAwaitingPush())
				return decidePush(state);
			
			if (state.isAwaitingFollowUp())
				return decideFollowUp(state);
			
			if (state.getCurrentPass() != null &&
				state.getCurrentPass().getInterceptionPlayers() != null &&
				state.getCurrentPass().getInterceptionPlayers().size() > 0){
				
				return pickIntercepter(state);
				
			}
			
			return turn(state);
			
		} else if (state.getGameStage() == GameStage.COIN_TOSS && !homeTeam){
			return pickCoinSide(state);
		} else if (state.getGameStage() == GameStage.PICK_COIN_TOSS_EFFECT){
			
			//if (state.getCoinToss().hasAwayPickedHeads() == state.getCoinToss().isResultHeads() && !homeTeam){
				return pickCoinSideEffect(state);
			//} else if (state.getCoinToss().hasAwayPickedHeads() != state.getCoinToss().isResultHeads() && homeTeam){
				//return pickCoinSideEffect(state);
			//}
			
		} else if (state.getGameStage() == GameStage.KICKING_SETUP){
			
			//if (state.getKickingTeam() == myTeam(state))
				return setup(state);
			
		} else if (state.getGameStage() == GameStage.RECEIVING_SETUP){
			
			//if (state.getReceivingTeam() == myTeam(state))
				return setup(state);
			
		} else if (state.getGameStage() == GameStage.KICK_PLACEMENT){
			
			//if (state.getKickingTeam() == myTeam(state))
				return placeKick(state);
			
		} else if (state.getGameStage() == GameStage.PLACE_BALL_ON_PLAYER){
			
			//if (state.getReceivingTeam() == myTeam(state))
				return placeBallOnPlayer(state);
			
		} else if (state.getGameStage() == GameStage.BLITZ){
			
			//if (state.getKickingTeam() == myTeam(state)){
				
				if (state.isAwaitingReroll() && state.getCurrentDiceRoll() != null)
					return decideReroll(state);
				
				if (state.isAwaitingPush())
					return decidePush(state);
				
				if (state.isAwaitingFollowUp())
					return decideFollowUp(state);
				
				return blitz(state);
			//}
			
		} else if (state.getGameStage() == GameStage.QUICK_SNAP){
			
			//if (state.getReceivingTeam() == myTeam(state))
				return quickSnap(state);
			
		} else if (state.getGameStage() == GameStage.HIGH_KICK){
			
			//if (state.getReceivingTeam() == myTeam(state))
				return highKick(state);
			
		} else if (state.getGameStage() == GameStage.PERFECT_DEFENSE){
			
			//if (state.getKickingTeam() == myTeam(state))
				return perfectDefense(state);
			
		}
		
		return null;
		
	}

	protected Team myTeam(GameState state){
		
		if (homeTeam){
			return state.getHomeTeam();
		}
		return state.getAwayTeam();
		
	}
	
	protected Team otherTeam(GameState state){
		
		if (!homeTeam){
			return state.getHomeTeam();
		}
		return state.getAwayTeam();
		
	}

	protected Square groupUp(Player p, GameState state){
		
		Square sq = state.getPitch().getBall().getSquare();
		Player ballCarrier = state.getPitch().getPlayerAt(sq);
		
		if(ballCarrier != null && ballCarrier.getTeamName() == p.getTeamName()){
			for(int i = -1; i <= 1; i += 2){
				for(int j = -1; j <= 1; j += 2){
					if(state.getPitch().getPlayerAt(new Square(sq.getX()+i,sq.getY()+j)) == null){
						return new Square(sq.getX()+i,sq.getY()+j);
					}
				}
			}	
		}return null;
	}
	
	protected boolean isGroupedUp(Player ballCarrier, GameState state){
	//	System.out.println("is grouped up");
		int counter = 0;
		Square sq = ballCarrier.getPosition();
		
		for(int i = -1; i <= 1; i += 2){
			for(int j = -1; j <= 1; j += 2){	
				if(state.getPitch().getPlayerAt(new Square(sq.getX()+i,sq.getY()+j)) != null){
					counter++;
				}
			}
		}if(counter == 4){
			return true;
		}else{
			if(sq.getX() == 1 || sq.getX() == 26 || sq.getY() == 1 || sq.getY() == 15){
				if(counter == 2){
					return true;
				}
			}
			return false;
		}
	}
	
	protected boolean canReachPosition(Player player, Square sq, GameState state){
		if(aStar
				.findPath(player, sq , state, false).size() <= player.getMA()){
			return true;
		}
		return false;
	}
	
	//MIGHT WORK IF ASTAR GETS FIXED
	protected ArrayList <Player> getNearestOpponents(Player player, GameState state){
	//	System.out.println("GETNEARESTOPPONENT CALLED");
		Pitch pitch = state.getPitch();
		
		ArrayList  <Player> theList;
		ArrayList  <Player> theListOfNearest = new ArrayList <Player>();
		
		int best = 10000;
		
		if(state.getAwayTeam().getTeamName() == player.getTeamName()){
			theList = state.getHomeTeam().getPlayers();
		}else{
			theList = state.getAwayTeam().getPlayers();
		}
	//	System.out.println("theList = "+theList+"    size = "+theList.size());
		for(Player p: theList){
			
			if(p.getPosition() != null){
				if(p.getPosition().getX() > 0 && p.getPosition().getX() < 27 && p.getPosition().getY() > 0 && p.getPosition().getY() < 16){
			//		System.out.println("player = "+p+"    position = "+p.getPosition()+" x = "+p.getPosition().getX()+" y = "+p.getPosition().getY());
					int NumberOfMoves = aStar.findPath(player, p.getPosition(), state, false).size();
					if(NumberOfMoves < best){
						theListOfNearest.clear();
						theListOfNearest.add(p);
					}else if(NumberOfMoves == best){
						theListOfNearest.add(p);
					}
				}
			}
		}
//		System.out.println("THELIIIST = "+theList);
		return theListOfNearest;
	}
	protected int numberOfSurroundingOpponents(GameState state, Square pos){
	//	Square playerPos = player.getPosition();
		int x = pos.getX();
		int y = pos.getY();
		String myTeam = myTeam(state).getTeamName();
		int count = 0;
		
		for(int i = 0; i < 8; i++){
			switch(i){
				case 0: Square up = new Square(x, y-1);
					if(state.getPitch().getPlayerAt(up) != null)
						if(state.getPitch().getPlayerAt(up).getTeamName() != myTeam) 
							if(state.getPitch().getPlayerAt(up).getPlayerStatus().getStanding() == Standing.UP)
								count++; break;
				case 1: Square upRight = new Square(x+1, y-1); 
					if(state.getPitch().getPlayerAt(upRight) != null)
						if(state.getPitch().getPlayerAt(upRight).getTeamName() != myTeam)
							if(state.getPitch().getPlayerAt(upRight).getPlayerStatus().getStanding() == Standing.UP)
								count++; break;
				case 2: Square right = new Square(x+1, y);
					if(state.getPitch().getPlayerAt(right) != null)
						if(state.getPitch().getPlayerAt(right).getTeamName() != myTeam) 
							if(state.getPitch().getPlayerAt(right).getPlayerStatus().getStanding() == Standing.UP)
								count++; break;
				case 3: Square downRight = new Square(x+1, y+1); 
					if(state.getPitch().getPlayerAt(downRight) != null)
						if(state.getPitch().getPlayerAt(downRight).getTeamName() != myTeam)
							if(state.getPitch().getPlayerAt(downRight).getPlayerStatus().getStanding() == Standing.UP)
								count++; break;
				case 4: Square down = new Square(x, y+1);
					if(state.getPitch().getPlayerAt(down) != null)
						if(state.getPitch().getPlayerAt(down).getTeamName() != myTeam) 
							if(state.getPitch().getPlayerAt(down).getPlayerStatus().getStanding() == Standing.UP)
								count++; break;
				case 5: Square downLeft = new Square(x-1, y+1); 
					if(state.getPitch().getPlayerAt(downLeft) != null)
						if(state.getPitch().getPlayerAt(downLeft).getTeamName() != myTeam)
							if(state.getPitch().getPlayerAt(downLeft).getPlayerStatus().getStanding() == Standing.UP)
								count++; break;
				case 6: Square left = new Square(x-1, y);
					if(state.getPitch().getPlayerAt(left) != null)
						if(state.getPitch().getPlayerAt(left).getTeamName() != myTeam)
							if(state.getPitch().getPlayerAt(left).getPlayerStatus().getStanding() == Standing.UP)
								count++; break;
				case 7: Square upLeft = new Square(x-1, y-1);
					if(state.getPitch().getPlayerAt(upLeft) != null)
						if(state.getPitch().getPlayerAt(upLeft).getTeamName() != myTeam)
							if(state.getPitch().getPlayerAt(upLeft).getPlayerStatus().getStanding() == Standing.UP)
								count++; break;
				default: break;
			}
		}
		return count;
	}
	
	protected abstract Action turn(GameState state);
	protected abstract Action decideReroll(GameState state);
	protected abstract Action pickIntercepter(GameState state);
	protected abstract Action decidePush(GameState state);
	protected abstract Action decideFollowUp(GameState state);
	protected abstract Action pickCoinSide(GameState state);
	protected abstract Action pickCoinSideEffect(GameState state);
	protected abstract Action setup(GameState state);
	protected abstract Action placeKick(GameState state);
	protected abstract Action placeBallOnPlayer(GameState state);
	protected abstract Action blitz(GameState state);
	protected abstract Action quickSnap(GameState state);
	protected abstract Action highKick(GameState state);
	protected abstract Action perfectDefense(GameState state);
	
	//inner class
	public class AStar{ 
			
			public AStar(){
				
			}
			
			protected ArrayList <Square> findPath(Player player, Square goalPosition, GameState state, boolean goingForTouchdown){
				
				int goalX = goalPosition.getX();
				int goalY = goalPosition.getY();
				Pitch pitch = state.getPitch();
				Square playerPos = player.getPosition();
				Mover curMover;
//				System.out.println("HOMETEAM = "+homeTeam);
				if(goingForTouchdown == true){
					if(homeTeam == true){
						curMover = new Mover(playerPos.getX(), playerPos.getY(),  26, goalY, 0, null, state, goingForTouchdown);
					}else{
						curMover = new Mover(playerPos.getX(), playerPos.getY(),  1, goalY, 0, null, state, goingForTouchdown);
					}
				}else{
					curMover = new Mover(playerPos.getX(), playerPos.getY(),  goalX, goalY, 0, null, state, goingForTouchdown);
				}
				
				
				Queue <Mover> pq = new PriorityQueue <Mover>();
				Set <String> aStarVisited = new HashSet <String>();
				
				
			
				pq.add(curMover);
				aStarVisited.add(curMover.toString());
				
		//		System.out.println("findPath called - going for square");
		//		System.out.println("pitch = "+pitch+" player = "+player+" player position = ("+playerPos.getX()+","+playerPos.getY()+")  goalX="+goalX+" goalY="+goalY);

		//		System.out.println("BEFORE WHILE");
				
				if(!goingForTouchdown){
					while(!curMover.isGoal()){
						for(int i = 1; i <=8; i++){
		//					System.out.println("GOING FOR BALL");
							
							if(curMover.cloneMover(i).isGoal() && state.getPitch().getPlayerAt(curMover.cloneMover(i).toSquare()) != null){
								return getFinalPath(curMover);
							}
							
							if(curMover.isMoveLegal(i, curMover.toSquare()) && !aStarVisited.contains(curMover.cloneMover(i).toString())){
		//						System.out.print(" is legal and visited not contains   curMover.cloneMover(i) ="+curMover.cloneMover(i)+"  pos="+curMover.cloneMover(i).getX()+","+curMover.cloneMover(i).getY());
								aStarVisited.add(curMover.cloneMover(i).toString());	
								pq.add(curMover.cloneMover(i));
							}
						}
//						System.out.println("pq.remove = "+pq.peek());
						if(pq.peek() == null){
							break;
						}
						curMover = pq.remove();
					}
				}else{
					
//						System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
					
					while(!curMover.isTouchdown()){
						for(int i = 1; i <=8; i++){
//							System.out.println("GOING FOR TOUCHDOWN.... WTF?");
							if(curMover.isMoveLegal(i, curMover.toSquare()) && !aStarVisited.contains(curMover.cloneMover(i).toString())){ //make this to toSquare
				//				System.out.print(" is legal and visited not contains   curMover.cloneMover(i) ="+curMover.cloneMover(i)+"  pos="+curMover.cloneMover(i).getX()+","+curMover.cloneMover(i).getY());
								aStarVisited.add(curMover.cloneMover(i).toString());	
								pq.add(curMover.cloneMover(i));
							}
						}
						if(pq.peek() == null){
							break;
						}
				//		System.out.println("pq.remove = "+pq.peek());
						curMover = pq.remove();
					}
				}
						
		//		System.out.println("with player position = "+player.getPosition().getX()+","+player.getPosition().getY()+" going to "+goalX+","+goalY);
				return getFinalPath(curMover);
			}
			
			private ArrayList <Square> getFinalPath(Mover curMover){
				int counter = 0;
				ArrayList <Square> finalPath = new ArrayList <Square>();
				while(curMover.getParent() != null){
					finalPath.add(new Square(curMover.getX(), curMover.getY()));
					curMover = curMover.getParent();
					counter++;
//					System.out.println("with "+counter+" squares in it");
//					System.out.println("finalPath = "+finalPath);
				}
//				System.out.println("___!!!  FINALPATH RETURNED  !!!___    finalPath = ");
//				for(int i = 0; i < finalPath.size(); i++){
		//			System.out.println(" "+finalPath.get(i).getX()+","+finalPath.get(i).getY());
//				}
				return finalPath;
			}
			
			//inner inner class
			protected class Mover implements Comparable{
				
				private int currentX;
				private int currentY;
				private int goalX;
				private int goalY;
				private int pitchWidth = 26;
				private int pitchHeight = 15;
				private int cost;
				private Mover parent;
				private GameState state;
				private Pitch p;
				private boolean goingForTouchdown;
				
				
				public Mover(int x, int y, int goalX, int goalY, int cost, Mover parent, GameState state, boolean goingForTouchdown){
					this.parent = parent;
					this.cost = cost;
					this.currentX = x;
					this.currentY = y;
					this.goalX = goalX;
					this.goalY = goalY;				
					this.state = state;
					this.p = state.getPitch();
		//			System.out.println("new Mover created  x = "+x+" y = "+y+" goalX = "+goalX+" goalY = "+goalY+" cost = "+cost+" parent = "+parent);
				}
				
				public Mover cloneMover(int i){
						switch(i){
							//clone up
							case 1: return new Mover(currentX,currentY-1,goalX,goalY,cost+1 + (numberOfSurroundingOpponents(state, new Square(currentX,currentY-1)) * 5),this, state, goingForTouchdown);
							//clone upRight
							case 2: return new Mover(currentX+1,currentY-1,goalX,goalY,cost+1 + (numberOfSurroundingOpponents(state, new Square(currentX+1,currentY-1)) * 5),this, state, goingForTouchdown);
							//clone right
							case 3: return new Mover(currentX+1,currentY,goalX,goalY,cost+1 + (numberOfSurroundingOpponents(state, new Square(currentX+1,currentY)) * 5),this, state, goingForTouchdown);
							//clone rightDown
							case 4: return new Mover(currentX+1,currentY+1,goalX,goalY,cost+1 + (numberOfSurroundingOpponents(state, new Square(currentX+1,currentY+1)) * 5),this, state, goingForTouchdown); 
							//clone down
							case 5: return new Mover(currentX,currentY+1,goalX,goalY,cost+1 + (numberOfSurroundingOpponents(state, new Square(currentX,currentY+1)) * 5),this, state, goingForTouchdown);
							//clone downLeft
							case 6: return new Mover(currentX-1,currentY+1,goalX,goalY,cost+1 + (numberOfSurroundingOpponents(state, new Square(currentX-1,currentY+1)) * 5),this, state, goingForTouchdown);
							//clone left
							case 7: return new Mover(currentX-1,currentY,goalX,goalY,cost+1 + (numberOfSurroundingOpponents(state,new Square( currentX-1,currentY)) * 5),this, state, goingForTouchdown);
							//clone upLeft
							case 8: return new Mover(currentX-1,currentY-1,goalX,goalY,cost+1 + (numberOfSurroundingOpponents(state, new Square(currentX-1,currentY-1)) * 5),this, state, goingForTouchdown); 
							default: System.out.println("clone error");return null;
						}
				}
				
				public boolean isGoal(){
					if(currentX == goalX && currentY == goalY)
						return true;
					else return false;
				}
				
				public boolean isTouchdown(){
					if(currentX == goalX)
						return true;
					else return false;
				}
				
				public boolean isMoveLegal(int i, Square sq){
				//	System.out.println("IS MOVE LEGAL???   sq = ("+sq.getX()+","+sq.getY()+")");
				
					switch(i){
					//check up
					case 1: if(!isGridOccupied(sq.getX(), sq.getY()-1)){
					//	System.out.println("move "+i+" is legal");
						return true;} break;
					//check upRight
					case 2: if(!isGridOccupied(sq.getX()+1, sq.getY()-1)){
					//	System.out.println("move "+i+" is legal");
						return true;} break;
					//check Right
					case 3: if(!isGridOccupied(sq.getX()+1, sq.getY())){
					//	System.out.println("move "+i+" is legal");
						return true;} break;
					//check downRight
					case 4: if(!isGridOccupied(sq.getX()+1, sq.getY()+1)){
					//	System.out.println("move "+i+" is legal");
						return true;} break;
					//check down
					case 5: if(!isGridOccupied(sq.getX(), sq.getY()+1)){
					//	System.out.println("move "+i+" is legal");
						return true;} break;
					//check downLeft
					case 6: if(!isGridOccupied(sq.getX()-1, sq.getY()+1)){
					//	System.out.println("move "+i+" is legal");
						return true;} break;
					//check left
					case 7: if(!isGridOccupied(sq.getX()-1, sq.getY())){
					//	System.out.println("move "+i+" is legal");
						return true;} break;
					//check upLeft
					case 8: if(!isGridOccupied(sq.getX()-1, sq.getY()-1)){
					//	System.out.println("move "+i+" is legal");
						return true;} break;
					default: System.out.println("illegal direction"); break;
					}
				return false;
				}
				
				protected boolean isGridOccupied(int x, int y){
					
					if(x > 0 && x <= pitchWidth && y > 0 && y <= pitchHeight){
						Player[][] playerArr = p.getPlayerArr();
					
						if(playerArr[y][x] == null){
							return false;
						}
					}
					return true;
				}
				
				public double manhattanTotalValue(boolean goingForTouchdown){
					return cost+manhattanHeuristicValue(goingForTouchdown);
				}

				public double manhattanHeuristicValue(boolean goingForTouchdown){
					
					if(!goingForTouchdown){
						int difX = (Math.abs(currentX-goalX));
						int difY = (Math.abs(currentY-goalY));
						return difX+difY;
					}else{
						return (Math.abs(currentX-goalX));
					}
				}
				
				@Override
				public int compareTo(Object o) {
					if(goingForTouchdown){
						if(manhattanTotalValue(true) < ((Mover) o).manhattanTotalValue(true))
							return -1;
						else if(manhattanTotalValue(true) > ((Mover) o).manhattanTotalValue(true))
							return 1;
						
					}else{
						if(manhattanTotalValue(false) < ((Mover) o).manhattanTotalValue(false))
							return -1;
						else if(manhattanTotalValue(false) > ((Mover) o).manhattanTotalValue(false))
							return 1;
					}
					return 0;
				}
				
				public String toString(){
					return currentX+" "+currentY;
				}
				
				public Square toSquare(){
					return new Square(currentX, currentY);
				}
				
				public Mover getParent(){
					return parent;
				}
				
				public int getX(){
					return currentX;
				}
				
				public int getY(){
					return currentY;
				}
			}
		}
	
}
