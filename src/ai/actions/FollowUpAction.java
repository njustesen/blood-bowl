package ai.actions;

public class FollowUpAction implements Action {

	boolean followUp;

	public FollowUpAction(boolean followUp) {
		super();
		this.followUp = followUp;
	}

	public boolean isFollowUp() {
		return followUp;
	}

	public void setFollowUp(boolean followUp) {
		this.followUp = followUp;
	}
	
}
