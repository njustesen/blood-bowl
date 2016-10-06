package sound;

public class SoundManager {

	BBSound buttonClick;
	BBSound beginTurn;
	BBSound knockedDown;
	BBSound cheer;
	BBSound diceRoll;
	BBSound whistle;
	
	public SoundManager(){
		/*
		buttonClick = new BBSound("buttonClick.wav");
		beginTurn = new BBSound("beginTurn.wav");
		knockedDown = new BBSound("knockedDown.wav");
		cheer = new BBSound("cheer.wav"); //from http://www.mediacollege.com/downloads/sound-effects/audience/
		diceRoll = new BBSound("diceRoll.wav");//from http://soundbible.com/181-Roll-Dice-2.html
		whistle = new BBSound("whistle.wav");
		*/
	}
	
	public void playSound(Sound sound){
		/*
		// Ignore loud sounds when developing
		if (sound == Sound.CHEER){
			return;
		}
		
		switch(sound){
			case BUTTONCLICK: buttonClick.start(); break;
			case BEGINTURN: beginTurn.start(); break;
			case KNOCKEDDOWN: knockedDown.start(); break;
			case CHEER: cheer.start(); break;
			case DICEROLL: diceRoll.start(); break;
			case WHISTLE: whistle.start(); break;
		}
		*/
	}
}
