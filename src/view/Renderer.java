package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import models.GameStage;
import models.Player;
import models.PlayerTurn;
import models.Race;
import models.Square;
import models.Standing;
import models.Team;
import models.Weather;
import models.actions.Block;
import models.actions.Pass;
import models.actions.Push;
import models.dice.DiceFace;
import models.dice.DiceRoll;
import models.humans.HumanBlitzer;
import models.humans.HumanCatcher;
import models.humans.HumanLineman;
import models.humans.HumanThrower;
import models.orcs.OrcBlackOrc;
import models.orcs.OrcBlitzer;
import models.orcs.OrcLineman;
import models.orcs.OrcThrower;
import game.GameLog;
import game.GameMaster;

public class Renderer extends JPanel{

	private static int fps;
	private static JFrame screen;
	
	//layout
	private static int screenWidth;
	private static int screenHeight;
	private static int pitchSquareSize;
	private static int actionButtonWidth;
	private static int actionButtonHeight;
	private static int logLength = 200;
	private static InputManager inputManager;
	private static Point2D pitchOrigin;
	private static Point2D rerollButtonOrigin;	
	
	/////////////////////////
	/////// graphics ////////
	////////////////////////
	
	//action-buttons
	private BBImage actionOn = new BBImage("actionButtonOn2.jpg");
	private BBImage actionOff = new BBImage("actionButtonOff.jpg");
	private BBImage run = new BBImage("footprints2.png");
	private BBImage block = new BBImage("block.png");
	private BBImage blitz = new BBImage("blitz.png");
	private BBImage foul = new BBImage("foul.png");
	private BBImage pass = new BBImage("pass.png");
	private BBImage handoff = new BBImage("handoff.png");
	private BBImage greenGlow = new BBImage("greenGlow.png");
	//dice-button
	private BBImage endTurnOn = new BBImage("endturnon.png");
	private BBImage endTurnOff = new BBImage("endturnoff.png");
	private BBImage endSetupOn = new BBImage("endsetupon.png");
	private BBImage endSetupOff = new BBImage("endsetupoff.png");
	private BBImage placeBallOn = new BBImage("placeballon.png");
	private BBImage placeBallOff = new BBImage("placeballoff.png");
	private BBImage startGameOn = new BBImage("startgameon.png");
	private BBImage startGameOff = new BBImage("startgameoff.png");
	//ball
	private BBImage ball = new BBImage("ball.png");
	//coin
	private BBImage heads = new BBImage("heads.png");
	private BBImage tails = new BBImage("tails.png");
	private BBImage kick = new BBImage("kick.png");
	private BBImage receive = new BBImage("receive.png");
	private BBImage kickGlow = new BBImage("kickGlow.png");
	private BBImage receiveGlow = new BBImage("receiveGlow.png");
	//dice-images
	private BBImage bbDice1 = new BBImage("bbdiceSkull.png");
	private BBImage bbDice2 = new BBImage("bbdiceBothDown.png");
	private BBImage bbDice3 = new BBImage("bbdiceArrow.png");
	private BBImage bbDice4 = new BBImage("bbdiceEx.png");
	private BBImage bbDice5 = new BBImage("bbdiceBoom.png");
	private BBImage dice1 = new BBImage("dice1.png");
	private BBImage dice2 = new BBImage("dice2.png");
	private BBImage dice3 = new BBImage("dice3.png");
	private BBImage dice4 = new BBImage("dice4.png");
	private BBImage dice5 = new BBImage("dice5.png");
	private BBImage dice6 = new BBImage("dice6.png");
	//background
	private BBImage background = new BBImage("interface.jpg");	
	//animations
	private BBAnimation greenTile = new BBAnimation("greenTile", true, 10);
	private BBAnimation whiteTile = new BBAnimation("whiteTile", true, 10);
	private BBAnimation selectedPlayer = new BBAnimation("selectedTile", true, 20);
	private BBAnimation roll = new BBAnimation("roll", true, 30);
	private BBAnimation greenDot = new BBAnimation("greenDot", true, 5);
	private BBAnimation redDot = new BBAnimation("redDot", true, 5);
	private BBAnimation yellowDot = new BBAnimation("yellowDot", true, 5);
	//orcs
	private BBImage olineman = new BBImage("olineman.png");
	private BBImage oblackorc = new BBImage("oblackorc.png");
	private BBImage othrower = new BBImage("othrower.png");
	private BBImage oblitzer = new BBImage("oblitzer.png");
	//humans
	private BBImage hlineman = new BBImage("hlineman.png");
	private BBImage hthrower = new BBImage("hthrower.png");
	private BBImage hcatcher = new BBImage("hcatcher1b.png");
	private BBImage hblitzer = new BBImage("hblitzer.png");
	
	private BBImage weather = new BBImage();
	private static InputStream is;
	Font f;
	Font standard = new Font("Arial", Font.PLAIN, 25);
	private GameMaster gameMaster;
	
	String fullMessage = "";
	
	private ArrayList <GameStage> inactivePitch = new ArrayList();
	
	
	public Renderer(int fps, GameMaster gm, InputManager im){

		inactivePitch.add(GameStage.START_UP);
		inactivePitch.add(GameStage.COIN_TOSS);
		inactivePitch.add(GameStage.PICK_COIN_TOSS_EFFECT);
		this.fps = fps;
		inputManager = im;
		screenWidth = InputManager.getScreenWidth();
		screenHeight = InputManager.getScreenHeight();
		pitchSquareSize = InputManager.getPitchSquareSize();
		actionButtonWidth = im.getActionButtonWidth();
		actionButtonHeight = im.getActionButtonHeight();
		pitchOrigin = InputManager.getPitchOrigin();
		rerollButtonOrigin = InputManager.getDiceButtonOrigin();
		gameMaster = gm;
		
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		screen = new JFrame();
		//screen.setSize(screenWidth, screenHeight);
		
		screen.setTitle("BLOOD BOWL");
		screen.setVisible(true);
		screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		screen.add(this);
		screen.pack();
		this.addKeyListener(inputManager);
		this.addMouseListener(inputManager);
		this.addMouseMotionListener(inputManager);
			
		pitchOrigin = InputManager.getPitchOrigin();
		pitchSquareSize = InputManager.getPitchSquareSize();
		greenTile.loopAnimation();
		whiteTile.loopAnimation();
		roll.loopAnimation();
		selectedPlayer.loopAnimation();
		redDot.loopAnimation();
		greenDot.loopAnimation();
		yellowDot.loopAnimation();
	}
	
	private void drawWeather(Graphics g){
		Weather w = gameMaster.getState().getWeather();
		switch(w){
			case SWELTERING_HEAT: weather.setImage("heat.png"); break;
			case VERY_SUNNY: weather.setImage("sun.png"); break;
			case NICE: weather.setImage("nice.png"); break;
			case POURING_RAIN: weather.setImage("rain.png"); break;
			case BLIZZARD: weather.setImage("blizzard.png"); break;
			//default: System.out.println("WEATHER?");
		}
		
		g.drawImage(weather.getBufferedImage(), 845, 545, null);
	}
	
	private int getFps(){
		return fps;
	}
	
	public void renderFrame(){
		this.repaint();
	}
	
	private void hoverSquare(Graphics g, int x, int y){
		if(inputManager.getMouseX() > pitchOrigin.getX() && inputManager.getMouseX() < 26*pitchSquareSize + pitchOrigin.getX() &&
				inputManager.getMouseY() > pitchOrigin.getY() && inputManager.getMouseY() < 15*pitchSquareSize + pitchOrigin.getY()){
			 
		//	 System.out.println("playerArray ["+inputManager.mouseOverPlayerArrIndex().getX()+"]["+inputManager.mouseOverPlayerArrIndex().getY()+"]");
			//System.out.println("hover  x = "+x+" y = "+y);
			if(!inactivePitch.contains(gameMaster.getState().getGameStage())){
				g.drawImage(greenTile.getBufferedImage(),inputManager.mouseOverArray().getX(),inputManager.mouseOverArray().getY(), null);
			}
		}
	}
	
	private void drawActionButtons(Graphics g){
		
		for(int i = 0; i < 6; i++){
			if(inputManager.getMouseX() > i*actionButtonWidth && inputManager.getMouseX() < (actionButtonWidth * i)+actionButtonWidth &&
					inputManager.getMouseY() > 517 && inputManager.getMouseY() < 517+actionButtonHeight){
				if(inputManager.mouse1Down){
					int n = i+1;
					//System.out.println("ActionButton "+n+" pressed");
				}
				g.drawImage(actionOn.getImage(), i*actionButtonWidth, 517, null);
				switch(i){
				case 0: g.drawImage(run.getBufferedImage(), i*actionButtonWidth-2, inputManager.getActionButtonOrigin().getY()+3, null); break;
				case 1: g.drawImage(block.getBufferedImage(), i*actionButtonWidth-2, inputManager.getActionButtonOrigin().getY()+3, null); break;
				case 2: g.drawImage(blitz.getBufferedImage(), i*actionButtonWidth-2, inputManager.getActionButtonOrigin().getY()+3, null); break;
				case 3: g.drawImage(foul.getBufferedImage(), i*actionButtonWidth-2, inputManager.getActionButtonOrigin().getY()+3, null); break;
				case 4: g.drawImage(pass.getBufferedImage(), i*actionButtonWidth-2, inputManager.getActionButtonOrigin().getY()+3, null); break;
				case 5: g.drawImage(handoff.getBufferedImage(), i*actionButtonWidth-2, inputManager.getActionButtonOrigin().getY()+3, null); break;
				//default: System.out.println("dont have that image");
				}
			}else{
				g.drawImage(actionOff.getImage(), i*actionButtonWidth, 517, null);
				switch(i){
				case 0: g.drawImage(run.getBufferedImage(),inputManager.getActionButtonOrigin().getX() + i*actionButtonWidth-4, inputManager.getActionButtonOrigin().getY(), null); break;
				case 1: g.drawImage(block.getBufferedImage(),inputManager.getActionButtonOrigin().getX() +  i*actionButtonWidth-4, inputManager.getActionButtonOrigin().getY(), null); break;
				case 2: g.drawImage(blitz.getBufferedImage(),inputManager.getActionButtonOrigin().getX() +  i*actionButtonWidth-4, inputManager.getActionButtonOrigin().getY(), null); break;
				case 3: g.drawImage(foul.getBufferedImage(),inputManager.getActionButtonOrigin().getX() +  i*actionButtonWidth-4, inputManager.getActionButtonOrigin().getY(), null); break;
				case 4: g.drawImage(pass.getBufferedImage(),inputManager.getActionButtonOrigin().getX() +  i*actionButtonWidth-4, inputManager.getActionButtonOrigin().getY(), null); break;
				case 5: g.drawImage(handoff.getBufferedImage(),inputManager.getActionButtonOrigin().getX() +  i*actionButtonWidth-4, 517, null); break;
				//default: System.out.println("dont have that image");
				}
			}
			Player p = gameMaster.getSelectedPlayer();
			if(p != null){
				if(p.getPlayerStatus().getTurn() != null){
					switch(p.getPlayerStatus().getTurn()){
						case MOVE_ACTION: g.drawImage(greenGlow.getBufferedImage(), 0*actionButtonWidth-12, 501, null); break;
						case BLOCK_ACTION: g.drawImage(greenGlow.getBufferedImage(), 1*actionButtonWidth-12, 501, null); break;
						case BLITZ_ACTION: g.drawImage(greenGlow.getBufferedImage(), 2*actionButtonWidth-12, 501, null); break;
						case FOUL_ACTION: g.drawImage(greenGlow.getBufferedImage(), 3*actionButtonWidth-12, 501, null); break;
						case PASS_ACTION: g.drawImage(greenGlow.getBufferedImage(), 4*actionButtonWidth-12, 501, null); break;
						case HAND_OFF_ACTION: g.drawImage(greenGlow.getBufferedImage(), 5*actionButtonWidth-12, 501, null); break;
						default: break;
					}
				}
			}
		}			
	}
	
	private void drawEndTurnButton(Graphics g){

			if(inputManager.getMouseX() > inputManager.getEndTurnButtonOrigin().getX() && inputManager.getMouseX() < inputManager.getEndTurnButtonOrigin().getX()+actionButtonWidth &&
					inputManager.getMouseY() > inputManager.getEndTurnButtonOrigin().getY() && inputManager.getMouseY() < inputManager.getEndTurnButtonOrigin().getX()+actionButtonHeight){
			
				switch(gameMaster.getState().getGameStage()){
					case START_UP: g.drawImage(startGameOn.getImage(), inputManager.getEndTurnButtonOrigin().getX(), inputManager.getEndTurnButtonOrigin().getY(), null); break;
					case KICKING_SETUP: g.drawImage(endSetupOn.getImage(), inputManager.getEndTurnButtonOrigin().getX(), inputManager.getEndTurnButtonOrigin().getY(), null); break;
					case RECEIVING_SETUP:  g.drawImage(endSetupOn.getImage(), inputManager.getEndTurnButtonOrigin().getX(), inputManager.getEndTurnButtonOrigin().getY(), null); break;
					case KICK_PLACEMENT: g.drawImage(placeBallOn.getImage(), inputManager.getEndTurnButtonOrigin().getX(), inputManager.getEndTurnButtonOrigin().getY(), null); break;
					default: g.drawImage(endTurnOn.getImage(), inputManager.getEndTurnButtonOrigin().getX(), inputManager.getEndTurnButtonOrigin().getY(), null); break;
				}
				
				}else{
					switch(gameMaster.getState().getGameStage()){
					case START_UP: g.drawImage(startGameOff.getImage(), inputManager.getEndTurnButtonOrigin().getX(), inputManager.getEndTurnButtonOrigin().getY(), null); break;
					case KICKING_SETUP: g.drawImage(endSetupOff.getImage(), inputManager.getEndTurnButtonOrigin().getX(), inputManager.getEndTurnButtonOrigin().getY(), null); break;
					case RECEIVING_SETUP:  g.drawImage(endSetupOff.getImage(), inputManager.getEndTurnButtonOrigin().getX(), inputManager.getEndTurnButtonOrigin().getY(), null); break;
					case KICK_PLACEMENT: g.drawImage(placeBallOff.getImage(), inputManager.getEndTurnButtonOrigin().getX(), inputManager.getEndTurnButtonOrigin().getY(), null); break;
					default: g.drawImage(endTurnOff.getImage(), inputManager.getEndTurnButtonOrigin().getX(), inputManager.getEndTurnButtonOrigin().getY(), null); break;
					}
				}	
		}
		
	
	private void drawRerollNamesAndScore(Graphics g){
			//rerolls
			g.drawImage(roll.getBufferedImage(), rerollButtonOrigin.getX(), rerollButtonOrigin.getY(), null);		
			Font font = new Font("Arial", Font.PLAIN, 15);	    
		    g.setFont(font); //<--
			g.drawString("rerolls: " + gameMaster.getState().getHomeTeam().getTeamStatus().getRerolls(), 100, 27);
			g.drawString("rerolls: " + gameMaster.getState().getAwayTeam().getTeamStatus().getRerolls(),  screenWidth-153, 27);
			//turn
			font = new Font("Arial", Font.PLAIN, 12);
		    g.setFont(font);
			g.drawString("turn "+gameMaster.getState().getHomeTurn()+"/8", 100, 45);
			g.drawString("turn "+gameMaster.getState().getAwayTurn()+"/8", screenWidth-153, 45);
			//half
			g.drawString("half "+gameMaster.getState().getHalf()+"/2", screenWidth-50, 540);
			//score	
			Integer homeScore = gameMaster.getState().getHomeTeam().getTeamStatus().getScore();
			Integer awayScore = gameMaster.getState().getAwayTeam().getTeamStatus().getScore();
			font = new Font("Arial", Font.PLAIN, 22);
		    g.setFont(font);
			g.drawString(homeScore.toString(), 23, 47);
			g.drawString(awayScore.toString(), screenWidth-38, 47);
			
			//team names
			font = new Font("Arial", Font.PLAIN, 32);	    
		    g.setFont(font);
		    GameStage stage = gameMaster.getState().getGameStage();
		    if(stage == GameStage.START_UP || stage == GameStage.COIN_TOSS || stage == GameStage.PICK_COIN_TOSS_EFFECT){
		    	g.drawString(gameMaster.getState().getHomeTeam().getTeamName(), 245, 37);
		    	g.drawString(gameMaster.getState().getAwayTeam().getTeamName(), screenWidth-378, 37);	
		    }else if(stage == GameStage.HOME_TURN || 
		    		(gameMaster.getState().getKickingTeam() == gameMaster.getState().getAwayTeam() && stage == GameStage.RECEIVING_SETUP) ||
		    		(gameMaster.getState().getKickingTeam() == gameMaster.getState().getAwayTeam() && stage == GameStage.HIGH_KICK) ||
		    		(gameMaster.getState().getKickingTeam() == gameMaster.getState().getAwayTeam() && stage == GameStage.QUICK_SNAP) ||
		    		(gameMaster.getState().getKickingTeam() == gameMaster.getState().getHomeTeam() && stage == GameStage.PERFECT_DEFENSE) ||
		    		(gameMaster.getState().getKickingTeam() == gameMaster.getState().getHomeTeam() && stage == GameStage.KICK_PLACEMENT) ||
		    		(gameMaster.getState().getKickingTeam() == gameMaster.getState().getHomeTeam() && stage == GameStage.BLITZ) ||
		    		(gameMaster.getState().getKickingTeam() == gameMaster.getState().getHomeTeam() && stage == GameStage.KICKING_SETUP)){
		    	g.drawString(gameMaster.getState().getHomeTeam().getTeamName(), 245, 37);
		    	g.setColor(Color.GRAY);
		    	g.drawString(gameMaster.getState().getAwayTeam().getTeamName(), screenWidth-378, 37);
		    }else if(stage == GameStage.AWAY_TURN ||
		    		(gameMaster.getState().getKickingTeam() == gameMaster.getState().getHomeTeam() && stage == GameStage.RECEIVING_SETUP) ||
		    		(gameMaster.getState().getKickingTeam() == gameMaster.getState().getHomeTeam() && stage == GameStage.HIGH_KICK) ||
		    		(gameMaster.getState().getKickingTeam() == gameMaster.getState().getHomeTeam() && stage == GameStage.QUICK_SNAP) ||
		    		(gameMaster.getState().getKickingTeam() == gameMaster.getState().getAwayTeam() && stage == GameStage.PERFECT_DEFENSE) ||
		    		(gameMaster.getState().getKickingTeam() == gameMaster.getState().getAwayTeam() && stage == GameStage.KICK_PLACEMENT) ||
		    		(gameMaster.getState().getKickingTeam() == gameMaster.getState().getAwayTeam() && stage == GameStage.BLITZ) ||
		    		(gameMaster.getState().getKickingTeam() == gameMaster.getState().getAwayTeam() && stage == GameStage.KICKING_SETUP)){
		    	g.drawString(gameMaster.getState().getAwayTeam().getTeamName(), screenWidth-378, 37);
		    	g.setColor(Color.GRAY);
		    	g.drawString(gameMaster.getState().getHomeTeam().getTeamName(), 245, 37);
		    }
			g.setColor(Color.WHITE);
			g.setFont(standard);
	}
	
	private void drawPlayer(Graphics g, Player p, int x, int y){
		int screenX = inputManager.arrayToScreen(x, y).getX();
		int screenY = inputManager.arrayToScreen(x, y).getY();
		if(p.getRace() == Race.HUMANS){
			if(p.getTitle() == "Blitzer"){
				g.drawImage(hblitzer.getBufferedImage(), screenX, screenY, null);
			}else if(p.getTitle() == "Lineman"){
				g.drawImage(hlineman.getBufferedImage(), screenX, screenY, null);
			}else if(p.getTitle() == "Thrower"){
				g.drawImage(hthrower.getBufferedImage(), screenX, screenY, null);
			}else if(p.getTitle() == "Catcher"){
				g.drawImage(hcatcher.getBufferedImage(), screenX, screenY, null);
			}
		}else if(p.getRace() == Race.ORCS){
			if(p.getTitle() == "Blitzer"){
				g.drawImage(oblitzer.getBufferedImage(), screenX, screenY, null);
			}else if(p.getTitle() == "Lineman"){
				g.drawImage(olineman.getBufferedImage(), screenX, screenY, null);
			}else if(p.getTitle() == "Thrower"){
				g.drawImage(othrower.getBufferedImage(), screenX, screenY, null);
			}else if(p.getTitle() == "black orc"){
				g.drawImage(oblackorc.getBufferedImage(), screenX, screenY, null);
			}
		}
		
		// Selected
		if(p == gameMaster.getSelectedPlayer()){
			g.drawImage(selectedPlayer.getBufferedImage(), screenX-1, screenY, null);
		}
		//down and stunned
		if(p.getPlayerStatus().getStanding() == Standing.STUNNED){
			Graphics2D g2d = (Graphics2D) g;
			BasicStroke stroke = new BasicStroke(3);
			g2d.setStroke(stroke);
			g2d.setColor(Color.BLACK);
			g2d.drawLine(screenX, screenY, screenX+30, screenY+30);
			g2d.drawLine(screenX+30, screenY, screenX, screenY+30);	
			stroke = new BasicStroke(1);
			g2d.setStroke(stroke);
			g.setColor(Color.RED);
			g.drawLine(screenX, screenY, screenX+30, screenY+30);
			g.drawLine(screenX+30, screenY, screenX, screenY+30);		
			
		}else if(p.getPlayerStatus().getStanding() == Standing.DOWN){
			Graphics2D g2d = (Graphics2D) g;
			BasicStroke stroke = new BasicStroke(3);
			g2d.setStroke(stroke);
			g2d.setColor(Color.BLACK);
			g2d.drawLine(screenX, screenY, screenX+30, screenY+30);	
			stroke = new BasicStroke(1);
			g2d.setStroke(stroke);
			g.setColor(Color.RED);
			g.drawLine(screenX, screenY, screenX+30, screenY+30);	
		}
		GameStage stage = gameMaster.getState().getGameStage();
		if(p.getPlayerStatus().getTurn() != null){	
			if((stage == GameStage.HOME_TURN && gameMaster.getState().getHomeTeam().getPlayers().contains(p)) || 
					(stage == GameStage.BLITZ && gameMaster.getState().getKickingTeam().getPlayers().contains(p)) || 
					(stage == GameStage.QUICK_SNAP && gameMaster.getState().getReceivingTeam().getPlayers().contains(p)) || 
					(stage == GameStage.AWAY_TURN  && gameMaster.getState().getAwayTeam().getPlayers().contains(p))){
				
				if (gameMaster.getState().getPitch().isOnPitch(p)){
					switch(p.getPlayerStatus().getTurn()){
						case UNUSED: g.drawImage(greenDot.getBufferedImage(), screenX, screenY, null); break;
						case USED: g.drawImage(redDot.getBufferedImage(), screenX, screenY, null); break;
						case BLITZ_ACTION: g.drawImage(yellowDot.getBufferedImage(), screenX, screenY, null);break;
						case BLOCK_ACTION: g.drawImage(yellowDot.getBufferedImage(), screenX, screenY, null);break;
						case FOUL_ACTION: g.drawImage(yellowDot.getBufferedImage(), screenX, screenY, null);break;
						case HAND_OFF_ACTION: g.drawImage(yellowDot.getBufferedImage(), screenX, screenY, null);break;
						case MOVE_ACTION: g.drawImage(yellowDot.getBufferedImage(), screenX, screenY, null);break;
						case PASS_ACTION: g.drawImage(yellowDot.getBufferedImage(), screenX, screenY, null);break;
						default: break;	
					}
				}
		    }
		}
		g.setFont(standard);
		g.setColor(Color.WHITE);
	}
	
	private void drawPushSquares(Graphics g){
		Block b = gameMaster.getState().getCurrentBlock();
		if(gameMaster.getState().getCurrentBlock() != null){
			Push p = b.getPush();
			if(b.getPush() != null){
				while(p.getFollowingPush() != null){
					p = p.getFollowingPush();
				}
				for(Square s: p.getPushSquares()){
					g.drawImage(whiteTile.getBufferedImage(),inputManager.arrayToScreen(s.getX(), s.getY()).getX(),inputManager.arrayToScreen(s.getX(), s.getY()).getY(), null);
				}
			}
		}
	}
	
	private void drawInterceptingPlayers(Graphics g){
		Pass pass = gameMaster.getState().getCurrentPass();
		if(pass != null){
			if(pass.getInterceptionPlayers() != null)
				for(Player p: pass.getInterceptionPlayers()){
					Square s = p.getPosition();
					g.drawImage(whiteTile.getBufferedImage(), inputManager.arrayToScreen(s.getX(), s.getY()).getX(),inputManager.arrayToScreen(s.getX(), s.getY()).getY(), null);
			}	
		}
	}
	
	private void drawFollowUpSquares(Graphics g){
		Block block = gameMaster.getState().getCurrentBlock();
		if (gameMaster.getState().isAwaitingFollowUp() && block != null){
				Square notFollow = block.getAttacker().getPosition();
				Square followUp = block.getFollowUpSquare();
				if(followUp != null){
					g.drawImage(whiteTile.getBufferedImage(), inputManager.arrayToScreen(followUp.getX(),followUp.getY()).getX(),inputManager.arrayToScreen(followUp.getX(),followUp.getY()).getY(),null);
				}
				if(notFollow != null){
					g.drawImage(whiteTile.getBufferedImage(), inputManager.arrayToScreen(notFollow.getX(),notFollow.getY()).getX(),inputManager.arrayToScreen(notFollow.getX(),notFollow.getY()).getY(),null);
				}		
			}
	}

	private void drawDice(Graphics g, int diceOne, int diceTwo, String type){
		if(type == "D6"){
			switch(diceOne){
			case 1: g.drawImage(dice1.getBufferedImage(), rerollButtonOrigin.getX()-110, rerollButtonOrigin.getY()+10, null); break;
			case 2: g.drawImage(dice2.getBufferedImage(), rerollButtonOrigin.getX()-110, rerollButtonOrigin.getY()+10, null); break;
			case 3: g.drawImage(dice3.getBufferedImage(), rerollButtonOrigin.getX()-110, rerollButtonOrigin.getY()+10, null); break;
			case 4: g.drawImage(dice4.getBufferedImage(), rerollButtonOrigin.getX()-110, rerollButtonOrigin.getY()+10, null); break;
			case 5: g.drawImage(dice5.getBufferedImage(), rerollButtonOrigin.getX()-110, rerollButtonOrigin.getY()+10, null); break;
			case 6: g.drawImage(dice6.getBufferedImage(), rerollButtonOrigin.getX()-110, rerollButtonOrigin.getY()+10, null); break;
			//default: System.out.println("invalid diceRoll");
		}
		switch(diceTwo){
			case 1: g.drawImage(dice1.getBufferedImage(), rerollButtonOrigin.getX()-60, rerollButtonOrigin.getY()+10, null); break;
			case 2: g.drawImage(dice2.getBufferedImage(), rerollButtonOrigin.getX()-60, rerollButtonOrigin.getY()+10, null); break;
			case 3: g.drawImage(dice3.getBufferedImage(), rerollButtonOrigin.getX()-60, rerollButtonOrigin.getY()+10, null); break;
			case 4: g.drawImage(dice4.getBufferedImage(), rerollButtonOrigin.getX()-60, rerollButtonOrigin.getY()+10, null); break;
			case 5: g.drawImage(dice5.getBufferedImage(), rerollButtonOrigin.getX()-60, rerollButtonOrigin.getY()+10, null); break;
			case 6: g.drawImage(dice6.getBufferedImage(), rerollButtonOrigin.getX()-60, rerollButtonOrigin.getY()+10, null); break;
			//default: System.out.println("invalid diceRoll");
		}
			
		}else if(type == "BB"){
			switch(diceOne){
				case 1: g.drawImage(bbDice1.getBufferedImage(), rerollButtonOrigin.getX()-110, rerollButtonOrigin.getY()+10, null); break;
				case 2: g.drawImage(bbDice2.getBufferedImage(), rerollButtonOrigin.getX()-110, rerollButtonOrigin.getY()+10, null); break;
				case 3: g.drawImage(bbDice3.getBufferedImage(), rerollButtonOrigin.getX()-110, rerollButtonOrigin.getY()+10, null); break;
				case 4: g.drawImage(bbDice4.getBufferedImage(), rerollButtonOrigin.getX()-110, rerollButtonOrigin.getY()+10, null); break;
				case 5: g.drawImage(bbDice5.getBufferedImage(), rerollButtonOrigin.getX()-110, rerollButtonOrigin.getY()+10, null); break;
				//default: System.out.println("invalid diceRoll");
			}
			switch(diceTwo){
				case 1: g.drawImage(bbDice1.getBufferedImage(), rerollButtonOrigin.getX()-60, rerollButtonOrigin.getY()+10, null); break;
				case 2: g.drawImage(bbDice2.getBufferedImage(), rerollButtonOrigin.getX()-60, rerollButtonOrigin.getY()+10, null); break;
				case 3: g.drawImage(bbDice3.getBufferedImage(), rerollButtonOrigin.getX()-60, rerollButtonOrigin.getY()+10, null); break;
				case 4: g.drawImage(bbDice4.getBufferedImage(), rerollButtonOrigin.getX()-60, rerollButtonOrigin.getY()+10, null); break;
				case 5: g.drawImage(bbDice5.getBufferedImage(), rerollButtonOrigin.getX()-60, rerollButtonOrigin.getY()+10, null); break;
				//default: System.out.println("invalid diceRoll");
			}
		}
	}


	private void drawDiceRoll(Graphics g, DiceRoll currentDiceRoll) {

		if(gameMaster.getState().getCurrentDiceRoll() != null){	
			int i = 0;
			for(DiceFace face : gameMaster.getState().getCurrentDiceRoll().getFaces()){
				BufferedImage img = getDiceImage(face);
				g.drawImage(img, rerollButtonOrigin.getX()-55-i*55, rerollButtonOrigin.getY()+10, null);
				i++;
			}
			if(gameMaster.getState().getCurrentBlock() != null){
				if (gameMaster.getState().getCurrentBlock().getSelectTeam() != null){
					String pickingTeam = gameMaster.getState().getCurrentBlock().getSelectTeam().getTeamName();
					Font choice = new Font("Arial", Font.PLAIN, 12);
					g.setFont(choice);
					g.drawString(pickingTeam, inputManager.getDiceButtonOrigin().getX()-(i*56)-3, inputManager.getDiceButtonOrigin().getY()+68);
					g.setFont(standard);
				}
			}
			g.drawRect(inputManager.getDiceButtonOrigin().getX()-(i*56)-3, inputManager.getDiceButtonOrigin().getY()+2, i*56, 68);
		}	
	}

	private BufferedImage getDiceImage(DiceFace face) {
		switch(face){
			case ONE : return dice1.getBufferedImage();
			case TWO : return dice2.getBufferedImage();
			case THREE : return dice3.getBufferedImage();
			case FOUR : return dice4.getBufferedImage();
			case FIVE : return dice5.getBufferedImage();
			case SIX : return dice6.getBufferedImage();
			case SKULL : return bbDice1.getBufferedImage();
			case BOTH_DOWN : return bbDice2.getBufferedImage();
			case PUSH : return bbDice3.getBufferedImage();
			case DEFENDER_STUMBLES : return bbDice4.getBufferedImage();
			case DEFENDER_KNOCKED_DOWN: return bbDice5.getBufferedImage();
			default:
				return null;
		}

	}
	
	private void drawDice(Graphics g, int dice, String type){
		if(type == "D6"){
			switch(dice){
				case 1: g.drawImage(dice1.getBufferedImage(), rerollButtonOrigin.getX()-85, rerollButtonOrigin.getY()+10, null); break;
				case 2: g.drawImage(dice2.getBufferedImage(), rerollButtonOrigin.getX()-85, rerollButtonOrigin.getY()+10, null); break;
				case 3: g.drawImage(dice3.getBufferedImage(), rerollButtonOrigin.getX()-85, rerollButtonOrigin.getY()+10, null); break;
				case 4: g.drawImage(dice4.getBufferedImage(), rerollButtonOrigin.getX()-85, rerollButtonOrigin.getY()+10, null); break;
				case 5: g.drawImage(dice5.getBufferedImage(), rerollButtonOrigin.getX()-85, rerollButtonOrigin.getY()+10, null); break;
				case 6: g.drawImage(dice6.getBufferedImage(), rerollButtonOrigin.getX()-85, rerollButtonOrigin.getY()+10, null); break;
				//default: System.out.println("invalid diceRoll");
			}
		}else if(type == "BB"){
			switch(dice){
				case 1: g.drawImage(bbDice1.getBufferedImage(), rerollButtonOrigin.getX()-85, rerollButtonOrigin.getY()+10, null); break;
				case 2: g.drawImage(bbDice2.getBufferedImage(), rerollButtonOrigin.getX()-85, rerollButtonOrigin.getY()+10, null); break;
				case 3: g.drawImage(bbDice3.getBufferedImage(), rerollButtonOrigin.getX()-85, rerollButtonOrigin.getY()+10, null); break;
				case 4: g.drawImage(bbDice4.getBufferedImage(), rerollButtonOrigin.getX()-85, rerollButtonOrigin.getY()+10, null); break;
				case 5: g.drawImage(bbDice5.getBufferedImage(), rerollButtonOrigin.getX()-85, rerollButtonOrigin.getY()+10, null); break;
				//default: System.out.println("invalid diceRoll");
			}
		}
	}

	private void drawPlayers(Graphics g) {
		
		// Draw pitch
		Player[][] playerArr = gameMaster.getState().getPitch().getPlayerArr();
		
		for(int y = 0; y < playerArr.length; y++){
			for(int x = 0; x < playerArr[0].length; x++){
				
				if (playerArr[y][x] != null){
					drawPlayer(g, playerArr[y][x], x, y);
				}
				
			}
		}
		
		// Draw home dugout
		for (int i = 0; i < gameMaster.getState().getPitch().getHomeDogout().getReserves().size(); i++){
			
			Player p = gameMaster.getState().getPitch().getHomeDogout().getReserves().get(i);
			drawPlayer(g, p, i%2 - 1, i/2 + 1);
			
		}
		
		// Draw home knockedout
		for(Player p : gameMaster.getState().getPitch().getHomeDogout().getKnockedOut()){
					
			int index = gameMaster.getState().getPitch().getHomeDogout().getKnockedOut().indexOf(p);
					
			drawPlayer(g, p, index%2 - 1, index/2 + 9);
					
		}
		
		// Draw home dead and injured
		for(Player p : gameMaster.getState().getPitch().getHomeDogout().getDeadAndInjured()){
			
			int index = gameMaster.getState().getPitch().getHomeDogout().getDeadAndInjured().indexOf(p);
			
			drawPlayer(g, p, index%2 + 1 -1, index/2 + 13);
			
		}
		
		// Draw away dugout
		for (int i = 0; i < gameMaster.getState().getPitch().getAwayDogout().getReserves().size(); i++){
			
			Player p = gameMaster.getState().getPitch().getAwayDogout().getReserves().get(i);
			drawPlayer(g, p, i%2 + 1 + 26, i/2 + 1);
			
		}

		// Draw away knockedout
		for(Player p : gameMaster.getState().getPitch().getAwayDogout().getKnockedOut()){
			
			int index = gameMaster.getState().getPitch().getAwayDogout().getKnockedOut().indexOf(p);
			
			drawPlayer(g, p, index%2 + 1 + 26, index/2 + 9);
			
		}
		
		// Draw away dead and injured
				for(Player p : gameMaster.getState().getPitch().getAwayDogout().getDeadAndInjured()){
					
					int index = gameMaster.getState().getPitch().getAwayDogout().getDeadAndInjured().indexOf(p);
					
					drawPlayer(g, p, index%2 + 1 + 26, index/2 + 13);
					
				}
	}
	
	private void drawCoinToss(Graphics g){
		if(gameMaster.getState().getGameStage() == GameStage.COIN_TOSS){
			g.drawString(gameMaster.getState().getAwayTeam().getTeamName()+" pick a coin", 350, 420);
			g.drawImage(heads.getBufferedImage(), inputManager.getHeadsCenter().getX()-heads.getWidth()/2, inputManager.getHeadsCenter().getY()-heads.getHeight()/2, null);
			g.drawImage(tails.getBufferedImage(), inputManager.getTailsCenter().getX()-tails.getWidth()/2, inputManager.getTailsCenter().getY()-tails.getHeight()/2, null);
		}else if(gameMaster.getState().getGameStage() == GameStage.PICK_COIN_TOSS_EFFECT){
			if(inputManager.getMouseX() < inputManager.getHeadsCenter().getX()+heads.getWidth()/2 && inputManager.getMouseX() > inputManager.getHeadsCenter().getX()-heads.getWidth()/2 &&
					inputManager.getMouseY() < inputManager.getHeadsCenter().getY()+heads.getHeight()/2 && inputManager.getMouseY() > inputManager.getHeadsCenter().getY()-heads.getHeight()/2){
				g.drawImage(kickGlow.getBufferedImage(), inputManager.getHeadsCenter().getX()-heads.getWidth()/2, inputManager.getHeadsCenter().getY()-heads.getHeight()/2, null);
			}else{g.drawImage(kick.getBufferedImage(), inputManager.getHeadsCenter().getX()-heads.getWidth()/2, inputManager.getHeadsCenter().getY()-heads.getHeight()/2, null);}
			
			if(inputManager.getMouseX() < inputManager.getTailsCenter().getX()+tails.getWidth()/2 && inputManager.getMouseX() > inputManager.getTailsCenter().getX()-tails.getWidth()/2 &&
					inputManager.getMouseY() < inputManager.getTailsCenter().getY()+tails.getHeight()/2 && inputManager.getMouseY() > inputManager.getTailsCenter().getY()-tails.getHeight()/2){
				g.drawImage(receiveGlow.getBufferedImage(), inputManager.getTailsCenter().getX()-tails.getWidth()/2, inputManager.getTailsCenter().getY()-tails.getHeight()/2, null);
			}else{
				g.drawImage(receive.getBufferedImage(), inputManager.getTailsCenter().getX()-tails.getWidth()/2, inputManager.getTailsCenter().getY()-tails.getHeight()/2, null);
			}
			
			Font font = new Font("Arial", Font.PLAIN, 32);	    
		    g.setFont(font);
			if(gameMaster.getState().getCoinToss().isResultHeads()){
				g.drawString("HEADS", 300, 120);
			}else{
				g.drawString("TAILS", 300, 120);
			}
			
			font = new Font("Arial", Font.PLAIN, 15);	    
		    g.setFont(font);
		    
		    String coinTossWinner;
			if(gameMaster.getState().getCoinToss().hasAwayPickedHeads() == gameMaster.getState().getCoinToss().isResultHeads()){
				coinTossWinner = gameMaster.getState().getAwayTeam().getTeamName()+" won the coin toss";
			}else{
				coinTossWinner = gameMaster.getState().getHomeTeam().getTeamName()+" won the coin toss";
			}
			g.drawString(coinTossWinner, 300, 150);
		}
	}
	
	private void drawStats(Graphics g){
		if(gameMaster.getSelectedPlayer() != null){
			Player p = gameMaster.getSelectedPlayer();
			if (p == null){
				return;
			}
			int x = InputManager.getActionButtonOrigin().getX()+6*inputManager.getActionButtonWidth()+5;
			int y = InputManager.getActionButtonOrigin().getY()+2;
			g.drawRect(x, y, 140, 77);
			int movesLeft = p.getMA() - p.getPlayerStatus().getMovementUsed();
			Font font = new Font("Arial", Font.PLAIN, 16);	    
		    g.setFont(font); //<--
			g.drawString(p.getTitle(), x+15, y+17);
			
			font = new Font("Arial", Font.PLAIN, 12);	    
		    g.setFont(font); //<--
			g.drawString("MA = "+movesLeft+"/"+p.getMA(), x+15, y+37);
			g.drawString("AG = "+p.getAG(), x+75, y+35);
			g.drawString("AV = "+p.getAV(), x+15, y+55);
			g.drawString("ST = "+p.getST(), x+75, y+55);
			
			Font font2 = new Font("Arial", Font.PLAIN, 10);	    
		    g.setFont(font2); //<--
			g.drawString(p.getSkills().toString(), x+15, y+70);
			g.setFont(font);
		}
	}
	
	private void drawBall(Graphics g){
		if(gameMaster.getState().getPitch().getBall().getSquare() != null){
			int ballX = gameMaster.getState().getPitch().getBall().getSquare().getX();
			int ballY = gameMaster.getState().getPitch().getBall().getSquare().getY();
			int screenX = inputManager.arrayToScreen(ballX, ballY).getX();
			int screenY = inputManager.arrayToScreen(ballX, ballY).getY();
			g.drawImage(ball.getBufferedImage(), screenX, screenY, null);
		}
	}
	
	private void drawGameLog(Graphics g){
		
		Font f = new Font("Arial", Font.PLAIN, 14);
		g.setFont(f);
		g.setColor(Color.BLACK);
		
		while(GameLog.peek() != ""){
			String message = "<"+GameLog.poll()+">";
			fullMessage =  message + "  " + fullMessage;
		}
		if(fullMessage.length() > logLength){
			fullMessage = fullMessage.substring(0, logLength);
		}
		g.drawString(fullMessage, 50, 622);		
		f = new Font("Arial", Font.PLAIN, 16);
		g.setFont(f);
		g.drawString("log:", 20, 622);
		g.setColor(Color.WHITE);
		//System.out.println(fullMessage);
		g.setFont(standard);
	}
	
	private void drawGameEnded(Graphics g){
		if(gameMaster.getState().getGameStage() == GameStage.GAME_ENDED){
			f = new Font("Arial", Font.PLAIN, 90);
			g.setFont(f);
			g.drawString("Game Ended", 200, 140);
			g.setFont(standard);
			if(gameMaster.getState().getHomeTeam().getTeamStatus().getScore() > gameMaster.getState().getAwayTeam().getTeamStatus().getScore()){
				g.drawString(gameMaster.getState().getHomeTeam().getTeamName()+" WON!", 260, 160);
			}else if(gameMaster.getState().getHomeTeam().getTeamStatus().getScore() < gameMaster.getState().getAwayTeam().getTeamStatus().getScore()){
				g.drawString(gameMaster.getState().getAwayTeam().getTeamName()+" WON!", 260, 160);
			}else if(gameMaster.getState().getHomeTeam().getTeamStatus().getScore() == gameMaster.getState().getAwayTeam().getTeamStatus().getScore()){
				g.drawString("Draw", 220, 175);
			}
		}
	}
	
	public void paintComponent(Graphics g) {  
	    g.setFont(standard); //<--
	    g.setColor(Color.WHITE);
	    
	    g.fillRect(0,0,screenWidth,screenHeight);
		g.drawImage(background.getImage(), 0, 0, null);
		
		hoverSquare(g, inputManager.getMouseX(), inputManager.getMouseY());
		drawPushSquares(g);
		drawFollowUpSquares(g);
		drawInterceptingPlayers(g);
		
		drawPlayers(g);
		drawBall(g);
		drawActionButtons(g);
		drawStats(g);
		drawEndTurnButton(g);
		drawRerollNamesAndScore(g);
		drawDiceRoll(g, gameMaster.getState().getCurrentDiceRoll());
		drawCoinToss(g);
		drawGameLog(g);
		drawWeather(g);
		drawGameEnded(g);
		
		
		
		//System.out.println("stage = "+gameMaster.getState().getGameStage());
		
	}
}
