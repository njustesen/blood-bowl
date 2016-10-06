package ai.monteCarlo;

import ai.actions.Action;

public class MctsIntermediateNode extends MctsAbstractNode {
	
	private Action action;
	
	public MctsIntermediateNode(Action action, MctsAbstractNode parent) {
		super(parent);
		this.action = action;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

}
