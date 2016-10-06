package view;

import game.GameMaster;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import models.GameStage;
import models.Player;
import models.PlayerTurn;
import models.Square;
import models.actions.Block;
import models.actions.Push;

public class InputManager implements KeyListener, MouseListener, MouseMotionListener{
	
	int mouseX;
	int mouseY;
	boolean mouse1Down;
	boolean [] keysDown = new boolean [256];
	boolean [] keysToggled = new boolean [256];
	private GameMaster gameMaster;
	
	//LAYOUT
	private static int screenWidth = 900;
	private static int screenHeight = 632;
	private static int actionButtonWidth = 66;
	private static int actionButtonHeight = 73;
	private static Point2D actionButtonOrigin = new Point2D(0,517);
	private static Point2D rerollButtonOrigin = new Point2D(742,517);
	private static int rerollButtonWidth = 31;
	private static int rerollButtonHeight = 72;
	private static Point2D endTurnButtonOrigin = new Point2D(rerollButtonOrigin.getX()+32,517);
	private static Point2D pitchOrigin = new Point2D(60,57);
	private static Point2D headsCenter = new Point2D(300,300);
	private static Point2D tailsCenter = new Point2D(600,300);
	private static int pitchSquareSize = 30;
	
	public InputManager(GameMaster gameMaster){
		this.gameMaster = gameMaster;
	}
	
	public static Point2D getPitchOrigin() {
		return pitchOrigin;
	}
	
	public static Point2D getEndTurnButtonOrigin() {
		return endTurnButtonOrigin;
	}

	public static Point2D getActionButtonOrigin() {
		return actionButtonOrigin;
	}

	public static int getPitchSquareSize() {
		return pitchSquareSize;
	}

	public boolean isKeyDown(String key){
		char c = key.charAt(0);

		return keysDown[((int)c)];
		
	}
	
	public boolean isKeyToggled(String key){
			char c = key.charAt(0);
			//if(keysDown[((int)c)])
			//	System.out.println(key+" is down");
			return keysToggled[((int)c)];	
		}
	
	public Point2D getMousePosition() {
		return new Point2D(mouseX, mouseY);
	}
	
	public int getMouseX(){return mouseX;}
	
	public int getMouseY(){return mouseY;}
	
	@Override
	public void mouseClicked(MouseEvent e) {	
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
		
	}

	public void actionButtonClicked(int x, int y){
		for(int i = 0; i < 6; i++){
			if(x > i*actionButtonWidth && x < (actionButtonWidth * i)+actionButtonWidth &&
					y > 517 && y < 517+actionButtonHeight){	
					int n = i+1;
					//System.out.println("ActionButton "+n+" pressed");
					switch(n){
						case 1: gameMaster.selectAction(PlayerTurn.MOVE_ACTION); return;
						case 2: gameMaster.selectAction(PlayerTurn.BLOCK_ACTION); return;
						case 3: gameMaster.selectAction(PlayerTurn.BLITZ_ACTION); return;
						case 4: gameMaster.selectAction(PlayerTurn.FOUL_ACTION); return;
						case 5: gameMaster.selectAction(PlayerTurn.PASS_ACTION); return;
						case 6: gameMaster.selectAction(PlayerTurn.HAND_OFF_ACTION); return;
					}
					
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		int button = e.getButton();
		
		if (button == 1){
			mouse1Down = true;
			squareClicked(e.getX(), e.getY());
		}
		
		actionButtonClicked(e.getX(), e.getY());
		
		pickCoinToss(e.getX(), e.getY());
			
			if(e.getX() < rerollButtonOrigin.getX()+rerollButtonWidth && e.getX() > rerollButtonOrigin.getX() &&
					e.getY() < rerollButtonOrigin.getY()+rerollButtonHeight && e.getY() > rerollButtonOrigin.getY()){
				
				gameMaster.reroll();
				
			}
			
			if(e.getX() < endTurnButtonOrigin.getX()+actionButtonWidth && e.getX() > endTurnButtonOrigin.getX() &&
					e.getY() < endTurnButtonOrigin.getY()+actionButtonHeight && e.getY() > endTurnButtonOrigin.getY()){
				
				if (gameMaster.getState().getGameStage() == GameStage.START_UP){
					gameMaster.startGame();
				} else {
					gameMaster.endPhase();
				}
				
			}
			
			clickReserves(e.getX(), e.getY(), true);
			selectDie(e.getX(), e.getY());
			pushedOutOfBounds(e.getX(), e.getY());
	}
	
	public void pickCoinToss(int x, int y){
		if(gameMaster.getState().getGameStage() == GameStage.COIN_TOSS){
			if(x < headsCenter.getX()+75 && x > headsCenter.getX()-75 &&
					y < headsCenter.getY()+75 && y > headsCenter.getY()-75){

				gameMaster.pickCoinSide(true);
				
			}else if(x < tailsCenter.getX()+75 && x > tailsCenter.getX()-75 &&
					y < tailsCenter.getY()+75 && y > tailsCenter.getY()-75){
				
				gameMaster.pickCoinSide(false);
				
			}
		}else if(gameMaster.getState().getGameStage() == GameStage.PICK_COIN_TOSS_EFFECT){
			if(x < headsCenter.getX()+75 && x > headsCenter.getX()-75 &&
					y < headsCenter.getY()+75 && y > headsCenter.getY()-75){
				
				gameMaster.pickCoinTossEffect(false);
				
			}else if(x < tailsCenter.getX()+75 && x > tailsCenter.getX()-75 &&
					y < tailsCenter.getY()+75 && y > tailsCenter.getY()-75){
				
				gameMaster.pickCoinTossEffect(true);
				
			}
		}
	}
	
	public void clickReserves(int x, int y, boolean home){
		if(home){
			if(x > 0 && x < 60 && y > 60 && y < 300){
				System.out.println("home reserve clicked");
			}
		}else{
			if(x > screenWidth - 60 && x < screenWidth && y > 60 && y < 300){
				System.out.println("away reserve clicked");
			}
		}
	}
	
	public static Point2D getHeadsCenter() {
		return headsCenter;
	}

	public static Point2D getTailsCenter() {
		return tailsCenter;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int button = e.getButton();
		if (button == 1){
			mouse1Down = false;
		}		
	}

	public boolean isMouse1Down() {
		return mouse1Down;
	}

	@Override
	public void keyPressed(KeyEvent e) {
	//	System.out.println("keycode is "+e.getKeyCode());
		keysDown[e.getKeyCode()]=true;		
	}

	@Override
	public void keyReleased(KeyEvent e) {
	//	System.out.println(e.getKeyCode());
		keysDown[e.getKeyCode()]=false;
		
		if(keysToggled[e.getKeyCode()])
			keysToggled[e.getKeyCode()]=false;
		else{
			keysToggled[e.getKeyCode()]=true;
		}		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public Point2D arrayToScreen(int x, int y){ 
		int screenX = (x*30-30)+getPitchOrigin().getX();
		int screenY = (y*30-30)+getPitchOrigin().getY();
		 return new Point2D(screenX, screenY);	 
	}
	
	public void squareClicked(int x, int y){
		if(getMouseX() > pitchOrigin.getX() && getMouseX() < 26*pitchSquareSize + pitchOrigin.getX() &&
				getMouseY() > pitchOrigin.getY() && getMouseY() < 15*pitchSquareSize + pitchOrigin.getY()){
			int pitchX = getMouseX()-pitchOrigin.getX();
			int pitchY = getMouseY()-pitchOrigin.getY();
			int squareX = pitchX/30+1;
			int squareY = pitchY/30+1;
			gameMaster.squareClicked( new Square(squareX, squareY));
		}else if(getMouseX() > 0 && getMouseX() < 2*pitchSquareSize + pitchOrigin.getX() &&
				getMouseY() > pitchOrigin.getY() && getMouseY() < 8*pitchSquareSize + pitchOrigin.getY()){
			int reserveX = getMouseX()/30;
			int reserveY = ((getMouseY()-pitchOrigin.getY())/30)*2;
			int reserve = reserveX + reserveY;
			gameMaster.selectHomeReserve(reserve);
		}else if(getMouseX() > getScreenWidth()-2*pitchSquareSize && getMouseX() < getScreenWidth() &&
				getMouseY() > pitchOrigin.getY() && getMouseY() < 8*pitchSquareSize + pitchOrigin.getY()){
			int reserveX = (getMouseX()-getScreenWidth()+2*pitchSquareSize)/30;
			int reserveY = ((getMouseY()-pitchOrigin.getY())/30)*2;
			int reserve = reserveX + reserveY;
			gameMaster.selectAwayReserve(reserve);
		}
	}

	public void pushedOutOfBounds(int x, int y){
		Block b = gameMaster.getState().getCurrentBlock();
		if(gameMaster.getState().getCurrentBlock() != null){
			Push p = b.getPush();
			if(b.getPush() != null){
				while(p.getFollowingPush() != null){
					p = p.getFollowingPush();
				}
				for(Square s: p.getPushSquares()){
					if(x > arrayToScreen(s.getX(), s.getY()).getX() && x < arrayToScreen(s.getX(), s.getY()).getX()+pitchSquareSize &&
							y > arrayToScreen(s.getX(), s.getY()).getY() && y < arrayToScreen(s.getX(), s.getY()).getY()+pitchSquareSize){
						System.out.println("PLAYER OUT OF BOUNDS!!!");
						//Write new methodcall
						gameMaster.squareClicked(s);
					}
				}
			}
		}
	}
	
	public Point2D mouseOverArray(){
		int pitchX = getMouseX()-pitchOrigin.getX();
		int pitchY = getMouseY()-pitchOrigin.getY();
		int fatLineY = (getMouseY()-pitchOrigin.getY())/(4*pitchSquareSize);
		int fatLineX = (getMouseY()-pitchOrigin.getY())/(13*pitchSquareSize);
		int squareX = ((pitchX/30)*30)+pitchOrigin.getX()+fatLineX;
		int squareY = ((pitchY/30)*30)+pitchOrigin.getY()+fatLineY; 
		return new Point2D(squareX, squareY);	 
	}
	
	public void selectDie(int x, int y){
		
		if(gameMaster.getState().getCurrentDiceRoll() != null){
			int diceStart = rerollButtonOrigin.getX()-5;
			int numberOfDice = gameMaster.getState().getCurrentDiceRoll().getFaces().size();
			if(y > rerollButtonOrigin.getY()+10 && y < rerollButtonOrigin.getY()+60)
				if( x < diceStart && x > diceStart-50){
					gameMaster.selectDie(0);
				}else if( x < diceStart-55 && x > diceStart-105 && numberOfDice > 1){
						gameMaster.selectDie(1);
				}else if( x < diceStart-110 && x > diceStart-160 && numberOfDice > 2){
						gameMaster.selectDie(2);
				}
		}
	}
	
	public static int getScreenWidth() {
		return screenWidth;
	}

	public static int getScreenHeight() {
		return screenHeight;
	}

	public int getActionButtonWidth() {
		return actionButtonWidth;
	}

	public int getActionButtonHeight() {
		return actionButtonHeight;
	}

	public static Point2D getDiceButtonOrigin() {
		return rerollButtonOrigin;
	}

}
