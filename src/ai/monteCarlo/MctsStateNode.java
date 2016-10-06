package ai.monteCarlo;

import java.util.List;
import java.util.Random;

import ai.actions.Action;

import models.GameState;

public class MctsStateNode extends MctsAbstractNode {

	private GameState state;
	private List<Action> possibleActions;
	private int probability;
	
	public MctsStateNode(GameState state, MctsAbstractNode parent, List<Action> possibleActions) {
		super(parent);
		this.state = state;
		this.possibleActions = possibleActions;
		this.probability = 1;
	}

	public List<Action> getPossibleActions() {
		return possibleActions;
	}

	public void setPossibleActions(List<Action> possibleActions) {
		this.possibleActions = possibleActions;
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public MctsIntermediateNode breedChild() {
		
		if (possibleActions == null || children.size() == possibleActions.size() || possibleActions.isEmpty())
			return null;
			
		while(true){
			
			int i = new Random().nextInt(possibleActions.size());
			Action action = possibleActions.get(i);
			boolean unique = true;
			
			for(MctsAbstractNode node : children){
				if (((MctsIntermediateNode)node).getAction().equals(action))
					unique = false;
			}
			
			if (unique){
				MctsIntermediateNode child = new MctsIntermediateNode(action, this);
				children.add(child);
				return child;
			}
			
		}
		
	}

	public int getProbability() {
		return probability;
	}

	public void setProbability(int probability) {
		this.probability = probability;
	}

	public void incProbability() {
		this.probability++;
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		MctsStateNode other = (MctsStateNode) obj;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

}
