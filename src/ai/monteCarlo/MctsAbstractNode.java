package ai.monteCarlo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.GameStage;

public class MctsAbstractNode {

	protected int visits;
	protected double value;
	protected MctsAbstractNode parent;
	protected List<MctsAbstractNode> children;
	
	public MctsAbstractNode(MctsAbstractNode parent) {
		this.visits = 0;
		this.value = 0.0;
		this.children = new ArrayList<MctsAbstractNode>();
		this.parent = parent;
	}
	
	public MctsAbstractNode getParent() {
		return parent;
	}

	public void setParent(MctsAbstractNode parent) {
		this.parent = parent;
	}
	
	public List<MctsAbstractNode> getChildren() {
		return children;
	}

	public void setChildren(List<MctsAbstractNode> children) {
		this.children = children;
	}

	public int getVisits() {
		return visits;
	}
	public void setVisits(int visits) {
		this.visits = visits;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}

	public MctsAbstractNode randomChild() {

		// Get non-terminal nodes
		List<MctsAbstractNode> nonTerminal = new ArrayList<MctsAbstractNode>();
		for (MctsAbstractNode child : children){
			if (child instanceof MctsStateNode){
				if (((MctsStateNode)child).getState().getGameStage() != GameStage.GAME_ENDED){
					nonTerminal.add(child);
					continue;
				}
			} else {
				nonTerminal.add(child);
			}
		}
		
		
		// Get probability index
		int probabilityIdx = 0;
		for (MctsAbstractNode n : nonTerminal){
		
			if (n instanceof MctsStateNode){
				probabilityIdx += ((MctsStateNode)n).getProbability();
				continue;
			}
			probabilityIdx += 1; 
			
		}
		
		if (probabilityIdx == 0)
			return null;
		
		// Get node using probability
		int idx = new Random().nextInt(probabilityIdx);
		int p = 0;
		for (MctsAbstractNode n : nonTerminal){
		
			if (n instanceof MctsStateNode){
				p += ((MctsStateNode)n).getProbability();
			} else {
				p += 1; 
			}
			if (idx <= p)
				return n;
			
		}
		
		return null;
		
	}
	
	public double UCT(double C) {
		double x = (value + 1 / 2);
		double n = 1;
		if (parent != null)
			n = parent.getVisits();
		double nj = visits;
		if (nj == 0.0)
			return 10000;
		
		return x + 2 * C * Math.sqrt( (2 * Math.log(n)) / nj );
	}
	
}
