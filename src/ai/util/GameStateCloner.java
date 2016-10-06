package ai.util;

import java.util.ArrayList;
import java.util.Date;

import models.Ball;
import models.CoinToss;
import models.Dugout;
import models.GameState;
import models.Pitch;
import models.Player;
import models.PlayerStatus;
import models.Race;
import models.Square;
import models.Team;
import models.TeamStatus;
import models.actions.Block;
import models.actions.Catch;
import models.actions.Dodge;
import models.actions.Foul;
import models.actions.GoingForIt;
import models.actions.HandOff;
import models.actions.Pass;
import models.actions.PickUp;
import models.actions.Push;
import models.dice.BB;
import models.dice.D3;
import models.dice.D6;
import models.dice.D8;
import models.dice.DiceRoll;
import models.dice.IDice;
import models.humans.HumanBlitzer;
import models.humans.HumanCatcher;
import models.humans.HumanLineman;
import models.humans.HumanThrower;
import models.orcs.OrcBlackOrc;
import models.orcs.OrcBlitzer;
import models.orcs.OrcLineman;
import models.orcs.OrcThrower;

public class GameStateCloner {
	
	public GameStateCloner(){
		
	}

	public GameState clone(GameState state) {
		
		Date now = new Date();
		
		Team home = cloneTeam(state.getHomeTeam());
		Team away = cloneTeam(state.getAwayTeam());
		
		GameState s = new GameState(home, away, clonePitch(state.getPitch(), home, away));
		s.setAwaitFollowUp(state.isAwaitingFollowUp());
		s.setAwaitPush(state.isAwaitingPush());
		s.setAwaitReroll(state.isAwaitingReroll());
		s.setAwayTurn(state.getAwayTurn());
		s.setCoinToss(cloneCoinToss(state.getCoinToss()));
		s.setCurrentBlock(cloneBlock(state.getCurrentBlock(), home, away));
		s.setCurrentCatch(cloneCatch(state.getCurrentCatch(), home, away));
		s.setCurrentDiceRoll(cloneDiceRoll(state.getCurrentDiceRoll()));
		s.setCurrentDodge(cloneDodge(state.getCurrentDodge(), home, away));
		s.setCurrentFoul(cloneFoul(state.getCurrentFoul(), home, away));
		s.setCurrentGoingForIt(cloneGoingForIt(state.getCurrentGoingForIt(), home, away));
		s.setCurrentHandOff(cloneHandOff(state.getCurrentHandOff(), home, away));
		s.setCurrentPass(clonePass(state.getCurrentPass(), home, away));
		s.setCurrentPickUp(clonePickUp(state.getCurrentPickUp(), home, away));
		s.setGameStage(state.getGameStage());
		s.setGust(state.isGust());
		s.setHalf(state.getHalf());
		s.setHomeTurn(state.getHomeTurn());
		if(state.getKickingTeam() == state.getHomeTeam()){
			s.setKickingTeam(home);
			s.setReceivingTeam(away);
		} else if(state.getKickingTeam() == state.getAwayTeam()){
			s.setKickingTeam(away);
			s.setReceivingTeam(home);
		}
		s.setPlayerPlaced(state.isPlayerPlaced());
		s.setRefAgainstAwayTeam(state.isRefAgainstAwayTeam());
		s.setRefAgainstHomeTeam(state.isRefAgainstHomeTeam());
		s.setWeather(state.getWeather());
		
		Date newNow = new Date();
		
		//System.out.println("clonetime: " + (newNow.getTime() - now.getTime()));
		
		return s;
	}

	private PickUp clonePickUp(PickUp pickUp, Team home, Team away) {
		if (pickUp == null)
			return null;
		
		Player player = null;
		if (pickUp.getPlayer() != null)
			player = getPlayer(pickUp.getPlayer(), home, away);
		
		return new PickUp(player, new Square(pickUp.getSquare().getX(), pickUp.getSquare().getY()), pickUp.getSuccess());
		
	}

	private Player getPlayer(Player player, Team home, Team away) {
		
		Team t = null;
		
		if (player.getRace() == home.getPlayers().get(0).getRace()){
			
			t = home;
			
		} else if (player.getRace() == away.getPlayers().get(0).getRace()){
			
			t = away;
			
		}
		
		if (t != null){
			int number = player.getNumber();
			
			for(Player p : t.getPlayers()){
				if (number == p.getNumber()){
					return p;
				}
			}
		}
		
		return null;
		
	}

	private Pass clonePass(Pass pass, Team home, Team away) {
		
		if (pass == null)
			return null;
		
		Player passer = null;
		Player catcher = null;
		if (pass.getPasser() != null)
			passer = getPlayer(pass.getPasser(), home, away);
		if (pass.getCatcher() != null)
			catcher = getPlayer(pass.getCatcher(), home, away);
		
		return new Pass(passer, catcher, pass.getSuccess());
	}

	private HandOff cloneHandOff(HandOff handOff, Team home, Team away) {
		
		if (handOff == null)
			return null;
		
		Player passer = null;
		Player catcher = null;
		if (handOff.getPasser() != null)
			passer = getPlayer(handOff.getPasser(), home, away);
		if (handOff.getCatcher() != null)
			catcher = getPlayer(handOff.getCatcher(), home, away);
		
		return new HandOff(passer, catcher);
	}

	private GoingForIt cloneGoingForIt(GoingForIt goingForIt, Team home, Team away) {
		
		if (goingForIt == null)
			return null;
		
		Player player = null;
		if (goingForIt.getPlayer() != null)
			player = getPlayer(goingForIt.getPlayer(), home, away);
		
		return new GoingForIt(player, new Square(goingForIt.getSquare().getX(), goingForIt.getSquare().getY()), goingForIt.getSuccess());
	}

	private Foul cloneFoul(Foul foul, Team home, Team away) {
		
		if (foul == null)
			return null;
		
		Player fouler = null;
		Player target = null;
		if (foul.getFouler() != null)
			fouler = getPlayer(foul.getFouler(), home, away);
		if (foul.getTarget() != null)
			target = getPlayer(foul.getTarget(), home, away);
		
		return new Foul(fouler, target);
	}

	private Dodge cloneDodge(Dodge dodge, Team home, Team away) {
		
		if (dodge == null)
			return null;
		
		Player player = null;
		if (dodge.getPlayer() != null)
			player = getPlayer(dodge.getPlayer(), home, away);
		
		return new Dodge(player, new Square(dodge.getSquare().getX(), dodge.getSquare().getY()), dodge.getSuccess());
	}

	private DiceRoll cloneDiceRoll(DiceRoll diceRoll) {
		
		if (diceRoll == null)
			return null;
		
		DiceRoll newDiceRoll = new DiceRoll();
		for(IDice d : diceRoll.getDices()){
			newDiceRoll.addDice(cloneDice(d));
		}
		return newDiceRoll;
	}

	private IDice cloneDice(IDice d) {
		
		if (d == null)
			return null;
		
		IDice dice = null;
		if (d instanceof BB){
			dice = new BB();
			((BB)dice).setResult(d.getResult());
			((BB)dice).setRolled(((BB) d).isRolled());
		} else if (d instanceof D6){
			dice = new D6();
			((D6)dice).setResult(d.getResult());
			((D6)dice).setRolled(((D6) d).isRolled());
		} else if (d instanceof D3){
			dice = new D3();
			((D3)dice).setResult(d.getResult());
			((D6)dice).setRolled(((D3) d).isRolled());
		} else if (d instanceof D8){
			dice = new D8();
			((D8)dice).setResult(d.getResult());
			((D8)dice).setRolled(((D8) d).isRolled());
		}
		return dice;
	}

	private Catch cloneCatch(Catch ccatch, Team home, Team away) {
		
		if (ccatch == null)
			return null;
		
		Player player = null;
		if (ccatch.getPlayer() != null)
			player = getPlayer(ccatch.getPlayer(), home, away);
		
		return new Catch(player, new Square(ccatch.getSquare().getX(), ccatch.getSquare().getY()), ccatch.getSuccess());
	}

	private Block cloneBlock(Block block, Team home, Team away) {
		
		if (block == null)
			return null;
		
		Player attacker = null;
		Player defender = null;
		if (block.getAttacker() != null)
			attacker = getPlayer(block.getAttacker(), home, away);
		if (block.getDefender() != null)
			defender = getPlayer(block.getDefender(), home, away);
		
		Team select = null;
		if (block.getSelectTeam() != null){
			if (block.getSelectTeam().getPlayers().get(0).getRace() == home.getPlayers().get(0).getRace()){
				select = home;
			} else if (block.getSelectTeam().getPlayers().get(0).getRace() == away.getPlayers().get(0).getRace()){
				select = away;
			}
		}
		
		Block newBlock = new Block(attacker, defender, select);
		
		if (block.getPush() != null)
			newBlock.setPush(clonePush(block.getPush(), home, away));
		
		if (block.getFollowUpSquare() != null)
			newBlock.setFollowUpSquare(new Square(block.getFollowUpSquare().getX(), block.getFollowUpSquare().getY()));
		
		return newBlock;
	}

	private Push clonePush(Push push, Team home, Team away) {
		
		if (push == null)
			return null;
		
		Square from = null;
		if (push.getFrom() != null)
			from = new Square(push.getFrom().getX(), push.getFrom().getY());
		
		Square to = null;
		if (push.getTo() != null)
			to = new Square(push.getTo().getX(), push.getTo().getY());
		
		Push newPush = new Push(getPlayer(push.getPushedPlayer(), home, away), from, to);
		
		if (push.getFollowingPush() != null)
			newPush.setFollowingPush(clonePush(newPush.getFollowingPush(), home, away));
		
		return newPush;
	}

	private CoinToss cloneCoinToss(CoinToss coinToss) {
		
		if (coinToss == null)
			return null;
		
		CoinToss newCoinToss = new CoinToss();
		newCoinToss.setAwayPickedHeads(coinToss.hasAwayPickedHeads());
		newCoinToss.setHomeReceives(coinToss.isHomeReceives());
		newCoinToss.setResultHeads(coinToss.isResultHeads());
		newCoinToss.setTossed(coinToss.isTossed());
		return newCoinToss;
	}

	private Pitch clonePitch(Pitch pitch, Team home, Team away) {
		
		if (pitch == null)
			return null;
		
		Pitch newPitch = new Pitch(home, away);
		newPitch.setHomeDogout(cloneDogout(pitch.getHomeDogout(), home));
		newPitch.setAwayDogout(cloneDogout(pitch.getAwayDogout(), away));
		
		newPitch.setBall(cloneBall(pitch.getBall()));
		newPitch.setPlayerArr(clonePlayerArr(pitch.getPlayerArr(), home, away));
		
		return newPitch;
	}

	private Player[][] clonePlayerArr(Player[][] playerArr, Team home, Team away) {

		Player[][] arr = new Player[17][28];

		// Count players
		for(int y = 0; y < playerArr.length; y++){
			for(int x = 0; x < playerArr[0].length; x++){
				
				Team t = null;
				
				if (playerArr[y][x] != null && playerArr[y][x].getRace() == home.getPlayers().get(0).getRace()){
					
					t = home;
					
				} else if (playerArr[y][x] != null && playerArr[y][x].getRace() == away.getPlayers().get(0).getRace()){
					
					t = away;
					
				}
				
				if (t != null){
					int number = playerArr[y][x].getNumber();
					
					for(Player p : t.getPlayers()){
						if (number == p.getNumber()){
							arr[y][x] = p;
							break;
						}
					}
				}
				
			}
		}
		
		return arr;
		
	}

	private Ball cloneBall(Ball ball) {
		if (ball == null){
			return null;
		}
		Ball newBall = null;
		if (ball.getSquare() != null){
			newBall = new Ball(new Square(ball.getSquare().getX(), ball.getSquare().getY()), 
					ball.isInGame(), 
					ball.isOnGround());
		} else {
			newBall = new Ball(null,
					ball.isInGame(), 
					ball.isOnGround());
		}
		
		newBall.setUnderControl(ball.isUnderControl());
		return newBall;
	}

	private Dugout cloneDogout(Dugout dugout, Team team) {
		Dugout newDugout = new Dugout(team);
		for(Player p : dugout.getReserves())
			newDugout.getReserves().add(getPlayer(p, team, team));
		for(Player p : dugout.getKnockedOut())
			newDugout.getKnockedOut().add(getPlayer(p, team, team));
		for(Player p : dugout.getDeadAndInjured())
			newDugout.getDeadAndInjured().add(getPlayer(p, team, team));
		return newDugout;
		
	}

	private Team cloneTeam(Team team) {
		
		ArrayList<Player> players = new ArrayList<Player>();
		
		for (Player p : team.getPlayers()){
			Player player = clonePlayer(p);
			players.add(player);
		}
		
		Team newTeam = new Team(players, team.getRerolls(), team.getFanFactor(), team.getAssistantCoaches(), team.getTeamName());
		
		newTeam.setTeamStatus(cloneTeamStatus(team.getTeamStatus()));
		
		for (Player p : newTeam.getPlayers()){
			p.setTeam(team);
		}
		
		return newTeam;
		
	}

	private TeamStatus cloneTeamStatus(TeamStatus teamStatus) {
		
		TeamStatus status = new TeamStatus(teamStatus.getRerolls());
		status.setBabes(teamStatus.getBabes());
		status.setFAME(teamStatus.getFAME());
		status.setFans(teamStatus.getFans());
		status.setHasBlitzed(teamStatus.hasBlitzed());
		status.setHasFouled(teamStatus.hasFouled());
		status.setHasPassed(teamStatus.hasPassed());
		status.setHasHandedOf(teamStatus.hasHandedOf());
		status.setRerolledThisTurn(teamStatus.rerolledThisTurn());
		status.setScore(teamStatus.getScore());

		return status;
		
	}

	private Player clonePlayer(Player p) {

		Player newPlayer = null;
		String teamName = p.getTeamName();
		
		if (p.getRace() == Race.HUMANS){
			if (p instanceof HumanLineman)
				newPlayer = new HumanLineman(p.getNumber(), teamName);
			else if (p instanceof HumanBlitzer)
				newPlayer = new HumanBlitzer(p.getNumber(), teamName);
			else if (p instanceof HumanCatcher)
				newPlayer = new HumanCatcher(p.getNumber(), teamName);
			else if (p instanceof HumanThrower)
				newPlayer = new HumanThrower(p.getNumber(), teamName);
		} else if (p.getRace() == Race.ORCS){
			if (p instanceof OrcLineman)
				newPlayer = new OrcLineman(p.getNumber(), teamName);
			else if (p instanceof OrcBlitzer)
				newPlayer = new OrcBlitzer(p.getNumber(), teamName);
			else if (p instanceof OrcBlackOrc)
				newPlayer = new OrcBlackOrc(p.getNumber(), teamName);
			else if (p instanceof OrcThrower)
				newPlayer = new OrcThrower(p.getNumber(), teamName);
		}
		/*
		newPlayer.setAG(p.getAG());
		newPlayer.setAV(p.getAV());
		newPlayer.setMA(p.getMA());
		newPlayer.setST(p.getST());
		
		ArrayList<Skill> skills = new ArrayList<Skill>();
		for (Skill s : p.getSkills()){
			skills.add(s);
		}
		newPlayer.setSkills(skills);
		*/
		if (p.getPosition() != null)
			newPlayer.setPosition(new Square(p.getPosition().getX(), p.getPosition().getY()));
		newPlayer.setPlayerStatus(clonePlayerStatus(p.getPlayerStatus()));
		
		return newPlayer;
		
	}

	private PlayerStatus clonePlayerStatus(PlayerStatus pstatus) {
		
		PlayerStatus status = new PlayerStatus();
		
		status.setMovedToBlock(pstatus.hasMovedToBlock());
		status.setMovementUsed(pstatus.getMovementUsed());
		status.setStanding(pstatus.getStanding());
		status.setTurn(pstatus.getTurn());
		
		return status;
		
	}
	
}
