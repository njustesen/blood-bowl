package ai.monteCarlo;

import game.GameMaster;

import java.util.Date;

import sound.FakeSoundManager;

import models.GameStage;
import models.GameState;
import ai.AIAgent;
import ai.RandomAI;
import ai.actions.Action;
import ai.actions.SelectCoinSideAction;
import ai.util.GameStateCloner;

public class MctsAi extends AIAgent {

	public static final double C = 5f;
	
	private static final int MULTIPLIER = 20;
	private static final int FEW = 1 * MULTIPLIER;
	private static final int MEDIUM = 2 * MULTIPLIER;
	private static final int MANY = 4 * MULTIPLIER;
	private static final int INSANE = 10 * MULTIPLIER;
	
	public MctsAi(boolean homeTeam) {
		super(homeTeam);
	}
	
	protected Action MctsSearch(GameState state, long ms){
		
		MctsNode root = new MctsNode(null, state, null);
		
		long startTime = new Date().getTime();
		
		while(new Date().getTime() < startTime + ms){
			
			// Selection
			MctsNode v1 = select(root);
			
			// expansion
			MctsNode v2 = expand(root);
			
			// Simulation
			int result = simulatedResult(v1);
			
			// Backpropagate
			backpropagate(v1, result);
			
		}
		
		return bestChild(root).getAction();
	}

	private MctsNode select(MctsNode node) {
		
		if (node.getChildren().isEmpty())
			return node;
			
		// UTC
		double bestUTC = UTC(node, node.getParent());
		MctsNode bestChild = node.getChildren().get(0);
		
		for(MctsNode child : node.getChildren()){
			
			double utc = UTC(node, node.getParent());
			
			if (utc >= bestUTC){
				bestUTC = utc;
				bestChild = child;
			}
			
		}
		
		return bestChild;
	}
	
	private double UTC(MctsNode node, MctsNode parent) {
		double x = node.getAverageResult();
		double n = 1;
		if (parent != null)
			n = parent.getVisited();
		double nj = node.getVisited();
		if (nj == 0.0)
			nj = 0.001;
		
		return x + 2 * C * Math.sqrt( (2 * Math.log(n)) / nj );
	}

	private MctsNode expand(MctsNode node) {
		
		Action action = selectRandomAction(node.getState());
		
		GameState state = new GameStateCloner().clone(node.getState());
		
		GameMaster master = new GameMaster(state, new RandomAI(true), new RandomAI(false), true, false);
		master.setSoundManager(new FakeSoundManager());
		master.performAIAction(action);
		
		return new MctsNode(node, state, action);
		
	}

	private Action selectRandomAction(GameState state) {
		// TODO Auto-generated method stub
		return null;
	}

	private int simulatedResult(MctsNode node) {
		
		int result = 0;
		
		GameState state = new GameStateCloner().clone(node.getState());
		
		GameMaster master = new GameMaster(state, new RandomAI(true), new RandomAI(false), true, false);
		master.setSoundManager(new FakeSoundManager());
		
		while(state.getGameStage() != GameStage.GAME_ENDED){
			master.update();
		}
		
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
		
		return result;
		
	}

	private void backpropagate(MctsNode node, int result) {
		
		MctsNode current = node;
		
		while(current.getParent() != null){
			
			double sum = current.getAverageResult() * current.getVisited() + result;
			current.setVisited(current.getVisited() + 1);
			current.setAverageResult(sum / current.getVisited());
			
			current = current.getParent();
			
		}
		
	}

	private MctsNode bestChild(MctsNode node) {
		
		double bestAvg = -100.0;
		MctsNode bestNode = null;
		
		for(MctsNode child : node.getChildren() ){
			
			if (child.getAverageResult() > bestAvg){
				
				bestAvg = child.getAverageResult();
				bestNode = child;
				
			}
		
		}
		
		return bestNode;
		
	}

	@Override
	protected Action turn(GameState state) {
		return MctsSearch(state, INSANE);
	}

	@Override
	protected Action decideReroll(GameState state) {
		return MctsSearch(state, INSANE);
	}

	@Override
	protected Action pickIntercepter(GameState state) {
		return MctsSearch(state, INSANE);
	}

	@Override
	protected Action decidePush(GameState state) {
		return MctsSearch(state, INSANE);
	}

	@Override
	protected Action decideFollowUp(GameState state) {
		return MctsSearch(state, INSANE);
	}

	@Override
	protected Action pickCoinSide(GameState state) {
		return new SelectCoinSideAction(true);
	}

	@Override
	protected Action pickCoinSideEffect(GameState state) {
		return MctsSearch(state, INSANE);
	}

	@Override
	protected Action setup(GameState state) {
		return MctsSearch(state, FEW);
	}

	@Override
	protected Action placeKick(GameState state) {
		return MctsSearch(state, MEDIUM);
	}

	@Override
	protected Action placeBallOnPlayer(GameState state) {
		return MctsSearch(state, INSANE);
	}

	@Override
	protected Action blitz(GameState state) {
		return MctsSearch(state, MANY);
	}

	@Override
	protected Action quickSnap(GameState state) {
		return MctsSearch(state, MANY);
	}

	@Override
	protected Action highKick(GameState state) {
		return MctsSearch(state, INSANE);
	}

	@Override
	protected Action perfectDefense(GameState state) {
		return MctsSearch(state, INSANE);
	}

}
