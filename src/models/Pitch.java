package models;

import java.util.ArrayList;
import java.util.Arrays;

import models.actions.Pass;

import game.GameLog;
import view.InputManager;

public class Pitch {
	private Player[][] playerArr = new Player[17][28];
	private Ball ball;
	private Dugout homeDogout;
	private Dugout awayDogout;
	private Team homeTeam;
	private Team awayTeam;
	private InputManager inputManager;
	private ArrayList<Player> dungeon;
	
	public Pitch(Team home, Team away){
		homeDogout = new Dugout(home);
		awayDogout = new Dugout(away);
		this.homeTeam = home;
		this.awayTeam = away;
		ball = new Ball();
		this.dungeon = new ArrayList<Player>();
	}

	public boolean isSetupLegal(Team team, int half) {
		
		if (!requiredNumberOnPitch(team)){
			GameLog.push("Illegal number of players on the field!");
			return false;
		}

		if (!onlyTeamPlayersOnTeamHalf(team, half)){
			GameLog.push("Some players are on the wrong side of the field!");
			return false;
		}
			
		if (!legalWideZones(team)){
			GameLog.push("A maximum of two players are allowed in each flank zone.");
			return false;
		}
			
		if (!legalScrimmage(team)){
			GameLog.push("A minimum of three players are required on the line of scrimmage.");
			return false;
		}
		
		return true;
	}
	
	public boolean ballCorreclyPlaced(Team kickingTeam) {
		if (kickingTeam == homeTeam && ball.getSquare().getX() >= 14){
			return true;
		} if (kickingTeam == awayTeam && ball.getSquare().getX() <= 13){
			return true;
		}
		return false;
	}
	
	public int playersOnPitch(Team team) {
		
		int playersOnPitch = 0;

		// Count players
		for(int y = 0; y < playerArr.length; y++){
			for(int x = 0; x < playerArr[0].length; x++){
				
				// If the found player is on the team
				if (playerArr[y][x] != null && team.getPlayers().contains(playerArr[y][x])){
					
					playersOnPitch++;
					
				}
			}
		}
		
		return playersOnPitch;
	}
	
	private boolean requiredNumberOnPitch(Team team) {
		
		int playersOnPitch = 0;

		// Count players
		for(int y = 0; y < playerArr.length; y++){
			for(int x = 0; x < playerArr[0].length; x++){
				
				// If the found player is on the team
				if (playerArr[y][x] != null && team.getPlayers().contains(playerArr[y][x])){
					
					playersOnPitch++;
					
				}
			}
		}
		
		// Test if legal
		if (playersOnPitch == 11){
			return true;
		} if (playersOnPitch > 11){
			return false;
		} else if (playersOnPitch < 11 && getDogout(team).getReserves().size() == 0){
			return true;
		}
		
		return false;
	}

	private boolean legalScrimmage(Team team) {
		
		int playersOnScrimmage = 0;

		// Count players
		for(int y = 5; y <= 11; y++){
			for(int x = 13; x <= 14; x++){
				
				// If the found player is on the team
				if (playerArr[y][x] != null && team.getPlayers().contains(playerArr[y][x])){
					
					playersOnScrimmage++;
					
				}
			}
		}
		
		if (playersOnScrimmage >= 3){
			return true;
		}
		
		return false;
	}

	private boolean legalWideZones(Team team) {
		
		int playersInTopWideZones = 0;
		int playersInBottomWideZones = 0;

		// Count players in top
		for(int y = 1; y <= 4; y++){
			for(int x = 2; x <= 25; x++){
				
				// If the found player is on the team
				if (playerArr[y][x] != null && team.getPlayers().contains(playerArr[y][x])){
					
					playersInTopWideZones++;
					
				}
			}
		}
		
		// Count players in bottom
		for(int y = 12; y <= 15; y++){
			for(int x = 2; x <= 25; x++){
				
				// If the found player is on the team
				if (playerArr[y][x] != null && team.getPlayers().contains(playerArr[y][x])){
					
					playersInBottomWideZones++;
					
				}
			}
		}
		
		if (playersInTopWideZones > 2 || playersInBottomWideZones > 2){
			return false;
		}
		
		return true;
		
	}

	private boolean onlyTeamPlayersOnTeamHalf(Team team, int half) {
		
		boolean playersOnLeftSide = false;
		boolean playersOnRightSide = false;
		
		for(int y = 0; y < 16; y++){
			
			// Count players on left side
			for(int x = 0; x < 13; x++){
				
				// If the found player is on the team
				if (playerArr[y][x] != null && team.getPlayers().contains(playerArr[y][x])){
					
					playersOnLeftSide = true;
					
				}
			}
			
			// Count players on right side
			for(int x = 14; x < 28; x++){
				
				// If the found player is on the team
				if (playerArr[y][x] != null && team.getPlayers().contains(playerArr[y][x])){
					
					playersOnRightSide = true;
					
				}
			}
			
		}
		
		// Home players on the left side
		if (playersOnRightSide && isTeamHome(team)){
			return false;
		}
		// Away players on the right
		if (playersOnLeftSide && !isTeamHome(team)){
			return false;
		}
		
		return true;
	}

	public void removePlayer(Player player) {
		
		if (player.getPosition() != null){
			playerArr[player.getPosition().getY()][player.getPosition().getX()] = null;
			player.setPosition(null);
		}
		
	}

	public boolean isOnPitch(Player player) {
		
		if (player.getPosition() == null)
			return false;
		
		return isOnPitch(player.getPosition());
		
	}
	
	public boolean isOnPitch(Square square) {
		
		if (square.getX() > 0 && square.getX() < 27 && square.getY() > 0 && square.getY() < 16){
			return true;
		}
		
		return false;
	}
	
	public int teamPlayersOnPitch(Team team) {

		int playersOnPitch = 0;

		// Count players
		for(int y = 0; y < playerArr.length; y++){
			for(int x = 0; x < playerArr[0].length; x++){
				
				// If the found player is on the team
				if (playerArr[y][x] != null && team.getPlayers().contains(playerArr[y][x])){
					
					playersOnPitch++;
					
				}
			}
		}
		
		return playersOnPitch;
		
	}

	private boolean isTeamHome(Team team) {
		if (getDogout(team) == getHomeDogout())
			return true;
		return false;
	}
	
	public InputManager getInputManager() {
		return inputManager;
	}

	public Player[][] getPlayerArr() {
		return playerArr;
	}
	public void setPlayerArr(Player[][] playerArr) {
		this.playerArr = playerArr;
	}
	public Ball getBall() {
		return ball;
	}
	public void setBall(Ball ball) {
		this.ball = ball;
	}
	public Dugout getHomeDogout() {
		return homeDogout;
	}
	public void setHomeDogout(Dugout homeDogout) {
		this.homeDogout = homeDogout;
		
	}
	public Dugout getAwayDogout() {
		return awayDogout;
	}
	public Dugout getDogout(Team team){
		if (homeDogout.getTeam() == team){
			return homeDogout;
		} 
		return awayDogout;
	}
	
	public void setAwayDogout(Dugout awayDogout) {
		this.awayDogout = awayDogout;
	}

	public boolean isBallInsidePitch() {
		if (ball.getSquare().getX() >=1 &&
				ball.getSquare().getX() < playerArr[0].length-1){
			
			if (ball.getSquare().getY() >=1 &&
					ball.getSquare().getY() < playerArr.length-1){
				
				return true;
				
			}
			
		}
		return false;
	}

	public boolean isBallOnTeamSide(Team team) {
		
		// Ball on left side
		if (ball.getSquare().getX() <= 13){
			
			if (isTeamHome(team)){
				return true;
			} else {
				return false;
			}
			
		} else {
			
			if (isTeamHome(team)){
				return false;
			} else {
				return true;
			}
			
		}
		
	}

	public int playersOnScrimmage(Team team) {

		int playersOnScrimmage = 0;
		int scrimmageLine = 13;
		
		if (!isTeamHome(team)){
			scrimmageLine = 14;
		}

		// Count players
		for(int y = 5; y <= 11; y++){
				
			// If the found player is on the team
			if (playerArr[y][scrimmageLine] != null && team.getPlayers().contains(playerArr[y][scrimmageLine])){
				
				playersOnScrimmage++;
				
			}
		}
		
		return playersOnScrimmage;
		
	}

	public int playersOnTopWideZones(Team team) {
		
		int playersInTopWideZones = 0;

		// Count players in top
		for(int y = 1; y <= 4; y++){
			for(int x = 1; x <= 25; x++){
				
				// If the found player is on the team
				if (playerArr[y][x] != null && team.getPlayers().contains(playerArr[y][x])){
					
					playersInTopWideZones++;
					
				}
			}
		}
		
		return playersInTopWideZones;
		
	}
	
	public int playersOnBottomWideZones(Team team) {
		
		int playersInBottomWideZones = 0;

		for(int y = 12; y <= 15; y++){
			for(int x = 1; x <= 25; x++){
				
				// If the found player is on the team
				if (playerArr[y][x] != null && team.getPlayers().contains(playerArr[y][x])){
					
					playersInBottomWideZones++;
					
				}
			}
		}
		
		return playersInBottomWideZones;
		
	}

	public void placePlayerInTopWideZone(Player p, Team team) {
		
		int start;
		int end;
		
		if (!isTeamHome(team)){
			start = 14;
			end = 25;
		} else {
			end = 13;
			start = 2;
		}

		for(int y = 1; y <= 4; y++){
			for(int x = start; x <= end; x++){
				
				if (playerArr[y][x] == null){
					
					playerArr[y][x] = p;
					p.setPosition(new Square(x, y));
					getDogout(team).getReserves().remove(p);
					return;
				}
			}
		}
		
	}
	
	public void placePlayerOnScrimmage(Player p, Team team) {
		
		int scrimmageLine = 13;
		
		if (!isTeamHome(team)){
			scrimmageLine = 14;
		}

		// Count players
		for(int y = 5; y <= 11; y++){
				
			// If empty place player
			if (playerArr[y][scrimmageLine] == null){
				
				playerArr[y][scrimmageLine] = p;
				p.setPosition(new Square(scrimmageLine, y));
				
				getDogout(team).getReserves().remove(p);
				return;
			}
		}
		
	}
	
	public void placePlayerInBottomWideZone(Player p, Team team) {

		int start;
		int end;
		
		if (!isTeamHome(team)){
			start = 14;
			end = 25;
		} else {
			end = 13;
			start = 2;
		}
		
		for(int y = 12; y <= 15; y++){
			for(int x = start; x <= end; x++){
				
				if (playerArr[y][x] == null){
					
					playerArr[y][x] = p;
					p.setPosition(new Square(x, y));
					getDogout(team).getReserves().remove(p);
					return;
					
				}
			}
		}
		
	}

	public void placePlayerInMidfield(Player p, Team team) {
		
		int start;
		int end;
		
		if (!isTeamHome(team)){
			start = 15;
			end = 25;
		} else {
			end = 12;
			start = 2;
		}
		
		for(int y = 5; y <= 11; y++){
			for(int x = start; x <= end; x++){
				
				if (playerArr[y][x] == null){
					
					playerArr[y][x] = p;

					p.setPosition(new Square(x, y));
					getDogout(team).getReserves().remove(p);
					return;
					
				}
			}
		}
		
	}

	public boolean isBallInEndzone(Team team) {
		
		if (isTeamHome(team) && 
				ball.getSquare().getX() == 1){
			
			return true;
			
		} else if (!isTeamHome(team) && 
				ball.getSquare().getX() == 26){
			
			return true;
			
		}
		
		return false;
	}

	public Player getPlayerAt(Square sq) {
		if (sq == null)
			return null;
		
		if (sq.getY() >= 0 && sq.getY() <= 16){
			if (sq.getX() >= 0 && sq.getX() <= 27){
				return playerArr[sq.getY()][sq.getX()];
			}
		}
		return null;
	}

	public ArrayList<Player> getDungeoun() {
		return dungeon;
	}

	public ArrayList<Player> interceptionPlayers(Pass pass) {
		
		Square from = pass.getPasser().getPosition();
		Square to = pass.getCatcher().getPosition();
		
		ArrayList<Square> line = line(from, to);
		line = includeManhattanNeighbors(line);
		line = excludeByDistance(line, from, to);
		
		ArrayList<Player> players = new ArrayList<Player>();
		for(Square s : line){
			Player p = getPlayerAt(s);
			if (p != null && playerOwner(pass.getPasser()) != playerOwner(p) && p.getPlayerStatus().getStanding() == Standing.UP){
				players.add(p);
			}
		}
		
		return players;
		
	}
	
	private ArrayList<Square> excludeByDistance(ArrayList<Square> line, Square from, Square to) {
		
		ArrayList<Square> newList = new ArrayList<Square>();
 		
		for(Square s : line){
			
			// Closer to catcher than thrower is
			if (distance(s, to) < distance(from, to)){
				
				// Closer to thrower than catcher is
				if (distance(s, from) < distance(to, from)){
					newList.add(s);
				}
				
			}
			
		}
		
		return newList;
		
	}

	private int distance(Square a, Square b) {
		return Math.max( Math.abs(a.getX() - b.getX()), Math.abs(a.getY() - b.getY()) );
	}

	private Team playerOwner(Player player) {
		if (homeTeam.players.contains(player)){
			return homeTeam;
		} else if (awayTeam.players.contains(player)){
			return awayTeam;
		}
		return null;
	}

	private ArrayList<Square> includeManhattanNeighbors(ArrayList<Square> line) {
		
		ArrayList<Square> withNeighbors = new ArrayList<Square>();
		
		for (Square s : line){
			withNeighbors.add(s);
			Square right = new Square(s.getX() + 1, s.getY());
			Square left = new Square(s.getX() - 1, s.getY());
			Square up = new Square(s.getX() + 1, s.getY() - 1);
			Square down = new Square(s.getX() + 1, s.getY() + 1);
			if (isOnPitch(right) && !line.contains(right) && !withNeighbors.contains(right)){
				withNeighbors.add(right);
			}
			if (isOnPitch(left) && !line.contains(left) && !withNeighbors.contains(left)){
				withNeighbors.add(left);
			}
			if (isOnPitch(up) && !line.contains(up) && !withNeighbors.contains(up)){
				withNeighbors.add(up);
			}
			if (isOnPitch(down) && !line.contains(down) && !withNeighbors.contains(down)){
				withNeighbors.add(down);
			}
		}
		
		return withNeighbors;
		
	}

	private ArrayList<Square> line(Square a, Square b){
		
		ArrayList<Square> line = new ArrayList<Square>();
		
		int x0 = a.getX();
		int y0 = a.getY();
		int x1 = b.getX();
		int y1 = b.getY();
		
		boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
		
	    if (steep){
	    	// swap(x0, y0);
	    	int x0n = y0;
	    	int y0n = x0;
	    	x0 = x0n;
	    	y0 = y0n;
	        
	        //swap(x1, y1);
	    	int x1n = y1;
	    	int y1n = x1;
	    	x1 = x1n;
	    	y1 = y1n;
	    }
	    if (x0 > x1){
	        //swap(x0, x1);
	    	int x0n = x1;
	    	int x1n = x0;
	    	x0 = x0n;
	    	x1 = x1n;
	    	
	        //swap(y0, y1);
	    	int y0n = y1;
	    	int y1n = y0;
	    	y0 = y0n;
	    	y1 = y1n;
	    }
	    
		int deltaX = x1 - x0;
		int deltaY = Math.abs(y1 - y0);
		double error = (double) deltaX / 2.0;
		
		int ystep;
		if (y0 < y1){ 
			ystep = 1;
		} else {
			ystep = -1;
		}
				
		int y = y0;
		for(int x = x0; x <= x1; x++){
			if (steep){
				line.add(new Square(y, x));
			} else {
				line.add(new Square(x, y));
			}
	        error = error - deltaY;
	        if (error < 0){
	             y = y + ystep;
	             error = error + deltaX;
	        }
		}
		
		return line;
		
	}

	public Square getRandomFreeScrimmageSquare(Team team) {
		
		boolean found = false;
		int x = 1;
		int y = 1;
		
		while (!found){
			y = 5 + (int) Math.floor((Math.random() * 6));
			x = 13;
			if (!isTeamHome(team)){
				x = 14;
			}
			if (playerArr[y][x] == null){
				found = true;
			}
		}
		
		return new Square(x,y);
			
	}
	
	public Square getRandomFreeFlankSquare(Team team, boolean up) {
		
		boolean found = false;
		int x = 1;
		int y = 1;
		
		while (!found){
			x = 1 + (int) Math.floor((Math.random() * 13));
			y = 1 + (int) Math.floor((Math.random() * 4));
			if (!isTeamHome(team)){
				x+=13;
			}
			if (!up){
				y+=8;
			}
			if (playerArr[y][x] == null){
				found = true;
			}
		}
		return new Square(x,y);
			
	}
	
	public Square getRandomFreeMiddleSquare(Team team) {
		
		boolean found = false;
		int x = 1;
		int y = 1;
		
		while (!found){
			x = 1 + (int) Math.floor((Math.random() * 13));
			y = 5 + (int) Math.floor((Math.random() * 7));
			if (!isTeamHome(team)){
				x+=13;
			}
			if (playerArr[y][x] == null){
				found = true;
			}
		}
		return new Square(x,y);
			
	}
	
	public Square getRandomFreeSquare(Team team) {
		
		boolean found = false;
		int x = 1;
		int y = 1;
		
		while (!found){
			x = 1 + (int) Math.floor((Math.random() * 13));
			y = 1 + (int) Math.floor((Math.random() * 15));
			if (!isTeamHome(team)){
				x+=13;
			}
			if (playerArr[y][x] == null){
				found = true;
			}
		}
		return new Square(x,y);
			
	}

	public Square getRandomOpposingSquare(Team team) {
		
		int x = 1;
		int y = 1;

		x = (int) (1 + Math.floor( (Math.random() * 13) ));
		y = (int) (1 + Math.floor( (Math.random() * 15) ));
		if (isTeamHome(team)){
			x+=13;
		}
		
		return new Square(x,y);
	}

	public ArrayList<Player> getPlayersOnPitch(Team team) {
		
		ArrayList<Player> playersOnPitch = new ArrayList<Player>();

		// Count players
		for(int y = 0; y < playerArr.length; y++){
			for(int x = 0; x < playerArr[0].length; x++){
				
				// If the found player is on the team
				if (playerArr[y][x] != null && team.getPlayers().contains(playerArr[y][x])){
					
					playersOnPitch.add(playerArr[y][x]);
					
				}
			}
		}
		
		return playersOnPitch;
		
	}

	public ArrayList<Square> getOpposingSquares(Team team) {
		
		ArrayList<Square> squares = new ArrayList<Square>();
		
		int xx = 0;
		if (isTeamHome(team)){
			xx+=13;
		}
		
		for(int x = 1; x <= 13; x++){
			for(int y = 1; y <= 15; y++){
				squares.add(new Square(x+xx,y));
			}
		}
		
		return squares;
	}
	
	public ArrayList<Square> getTeamSquares(Team team) {
		
		ArrayList<Square> squares = new ArrayList<Square>();
		
		int xx = 0;
		if (!isTeamHome(team)){
			xx=13;
		}
		
		for(int x = 1; x <= 13; x++){
			for(int y = 1; y <= 15; y++){
				squares.add(new Square(x+xx,y));
			}
		}
		
		return squares;
	}

	public ArrayList<Square> getScrimmageSquares(Team team) {

		ArrayList<Square> squares = new ArrayList<Square>();
		int x = 13;
		if (!isTeamHome(team)){
			x = 14;
		}
		
		for(int y = 5; y <= 11; y++){
			squares.add(new Square(x,y));
		}
		
		return squares;
	}

	public boolean isInTopWideZone(Square square) {
		
		if (square.getY() <= 4){
			return true;
		}
		
		return false;
		
	}
	
	public boolean isInBottomWideZone(Square square) {
		
		if (square.getY() >= 12){
			return true;
		}
		
		return false;
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((awayDogout == null) ? 0 : awayDogout.hashCode());
		result = prime * result
				+ ((awayTeam == null) ? 0 : awayTeam.hashCode());
		result = prime * result + ((ball == null) ? 0 : ball.hashCode());
		result = prime * result + ((dungeon == null) ? 0 : dungeon.hashCode());
		result = prime * result
				+ ((homeDogout == null) ? 0 : homeDogout.hashCode());
		result = prime * result
				+ ((homeTeam == null) ? 0 : homeTeam.hashCode());
		result = prime * result
				+ ((inputManager == null) ? 0 : inputManager.hashCode());
		result = prime * result + Arrays.hashCode(playerArr);
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
		Pitch other = (Pitch) obj;
		if (awayDogout == null) {
			if (other.awayDogout != null)
				return false;
		} else if (!awayDogout.equals(other.awayDogout))
			return false;
		if (awayTeam == null) {
			if (other.awayTeam != null)
				return false;
		} else if (!awayTeam.equals(other.awayTeam))
			return false;
		if (ball == null) {
			if (other.ball != null)
				return false;
		} else if (!ball.equals(other.ball))
			return false;
		if (dungeon == null) {
			if (other.dungeon != null)
				return false;
		} else if (!dungeon.equals(other.dungeon))
			return false;
		if (homeDogout == null) {
			if (other.homeDogout != null)
				return false;
		} else if (!homeDogout.equals(other.homeDogout))
			return false;
		if (homeTeam == null) {
			if (other.homeTeam != null)
				return false;
		} else if (!homeTeam.equals(other.homeTeam))
			return false;
		if (inputManager == null) {
			if (other.inputManager != null)
				return false;
		}
		if (!Arrays.deepEquals(playerArr, other.playerArr))
			return false;
		return true;
	}
    
}


	