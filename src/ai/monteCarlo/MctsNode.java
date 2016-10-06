package ai.monteCarlo;

import java.util.ArrayList;

import models.GameState;

import ai.actions.Action;

public class MctsNode {
	
	private MctsNode parent;
	private ArrayList<MctsNode> children;
	private Action action;
	private GameState state;
	private int visited;
	private double averageResult;
	
	public MctsNode(MctsNode parent, GameState state, Action action) {
		super();
		this.parent = parent;
		this.state = state;
		this.action = action;
		this.children = new ArrayList<MctsNode>();
		this.visited = 0;
		this.averageResult = 0.0f;
	}
	
	public double getAverageResult() {
		return averageResult;
	}

	public void setAverageResult(double averageResult) {
		this.averageResult = averageResult;
	}

	public int getVisited() {
		return visited;
	}

	public void setVisited(int visited) {
		this.visited = visited;
	}
	
	public GameState getState() {
		return state;
	}
	
	public void setState(GameState state) {
		this.state = state;
	}

	public ArrayList<MctsNode> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<MctsNode> children) {
		this.children = children;
	}

	public MctsNode getParent() {
		return parent;
	}

	public void setParent(MctsNode parent) {
		this.parent = parent;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
	
	public boolean isRoot(){
		if (parent == null)
			return true;
		else
			return false;
	}
	
}
