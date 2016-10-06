package main;

import java.util.Date;

import ai.AIAgent;
import ai.BaseLineAI;
import ai.RandomAI;
import ai.RandomMoveAI;
import ai.RandomMoveTouchdownAI;
import ai.RandomTouchdownAI;
import ai.monteCarlo.FlatMonteCarloAI;
import ai.monteCarlo.MctsDetermAi;

import models.GameState;
import models.Pitch;
import models.Team;
import models.TeamFactory;

import game.GameMaster;
import sound.FakeSoundManager;
import sound.SoundManager;
import test.DiceTester;
import view.InputManager;
import view.Renderer;

public class Main {

	private static InputManager inputManager;
	private static Renderer renderer;
	private static GameMaster gameMaster;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("Welcome to Blood Bowl!");
		
		boolean h = false;
		boolean a = false;
		boolean restart = false;
		boolean fast = false;
		
		Team homeTeam = TeamFactory.getOrcTeam();
		Team awayTeam = TeamFactory.getHumanTeam();

		AIAgent homeAgent = null;
		AIAgent awayAgent = null;
		
		for(String str : args){
			
			if (str.toLowerCase().equals("randomai")){
				if (h)
					homeAgent = new RandomAI(true);
				else if (a)
					awayAgent = new RandomAI(false);
			}
			
			if (str.toLowerCase().equals("randommovetouchdownai")){
				if (h)
					homeAgent = new RandomMoveTouchdownAI(true);
				else if (a)
					awayAgent = new RandomMoveTouchdownAI(false);
			}
			
			if (str.toLowerCase().equals("baselineai")){
				if (h)
					homeAgent = new BaseLineAI(true);
				else if (a)
					awayAgent = new BaseLineAI(false);
			}
			
			if (str.toLowerCase().equals("flatmontecarloai-random")){
				if (h)
					homeAgent = new FlatMonteCarloAI(true, true, new RandomAI(true), new RandomAI(false));
				else if (a)
					awayAgent = new FlatMonteCarloAI(false, true, new RandomAI(true), new RandomAI(false));
			}
			
			if (str.toLowerCase().equals("flatmontecarloai-touchdown")){
				if (h)
					homeAgent = new FlatMonteCarloAI(true, true, new RandomMoveTouchdownAI(true), new RandomMoveTouchdownAI(false));
				else if (a)
					awayAgent = new FlatMonteCarloAI(false, true, new RandomMoveTouchdownAI(true), new RandomMoveTouchdownAI(false));
			}
			
			if (str.toLowerCase().equals("mctsai-random")){
				if (h)
					homeAgent = new MctsDetermAi(true, true, new RandomAI(true), new RandomAI(false));
				else if (a)
					awayAgent = new MctsDetermAi(false, true, new RandomAI(true), new RandomAI(false));
			}
			
			if (str.toLowerCase().equals("mctsai-touchdown")){
				if (h)
					homeAgent = new MctsDetermAi(true, true, new RandomMoveTouchdownAI(true), new RandomMoveTouchdownAI(false));
				else if (a)
					awayAgent = new MctsDetermAi(false, true, new RandomMoveTouchdownAI(true), new RandomMoveTouchdownAI(false));
			}
			
			if (str.toLowerCase().equals("orc") || str.toLowerCase().equals("orcs")){
				if (h)
					homeTeam = TeamFactory.getOrcTeam();
				else if (a)
					awayTeam = TeamFactory.getOrcTeam();
			}
			
			if (str.toLowerCase().equals("human") || str.toLowerCase().equals("humans")){
				if (h)
					homeTeam = TeamFactory.getHumanTeam();
				else if (a)
					awayTeam = TeamFactory.getHumanTeam();
			}
			
			if (str.toLowerCase().equals("-home")){
				h = true;
				a = false;
			}
			if (str.toLowerCase().equals("-away")){
				a = true;
				h = false;
			}
			if (str.toLowerCase().equals("-restart")){
				h = false;
				a = false;
				restart = true;
			}
			if (str.toLowerCase().equals("-fast")){
				h = false;
				a = false;
				fast = true;
			}
		}
		
		initialize(homeTeam, awayTeam, homeAgent, awayAgent, fast, restart);
		startGame();
	}
	
	public static void test(){
		
		DiceTester.testDices();
		
	}

	public static void initialize(Team home, Team away, AIAgent homeAgent, AIAgent awayAgent, boolean fast, boolean restart){

		Pitch pitch = new Pitch(home, away);
		gameMaster = new GameMaster(new GameState(home, away, pitch), homeAgent, awayAgent, fast, restart);
		gameMaster.enableLogging();
		//gameMaster.setSoundManager(new SoundManager());
		gameMaster.setSoundManager(new FakeSoundManager());
		inputManager = new InputManager(gameMaster);
		renderer = new Renderer(600, gameMaster, inputManager);
	}
	
	public static void startGame(){

		long startTime = new Date().getTime();
		
		loop(startTime);
	
	}

	private static void loop(long startTime) {

		while(true){
			
			//startTime = new Date().getTime();
			if (renderer != null){
				renderer.renderFrame();
				//renderer.paintComponent(renderer.getGraphics());
			}
			
			gameMaster.update();
			
			/*
			long delta = new Date().getTime() - startTime;
			try {
				
				Thread.sleep(Math.max(250,(1000/renderer.getFps() - delta)));
				
			} catch (InterruptedException e) {e.printStackTrace();}
			*/
		}
	}
}
