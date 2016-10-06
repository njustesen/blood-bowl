package models.dice;

public class BB implements IDice{

	DiceFace result;
	boolean rolled;
	
	public BB() {
		super();
		this.result = null;
		this.rolled = false;
	}
	
	public void roll(){
		rolled = true;
		int roll = (int)(Math.random()*6 + 1);
		
		result = BB.intToBBRoll(roll);
		
	}
	
	public static DiceFace intToBBRoll(int i){
		switch(i){
			case 1: return DiceFace.SKULL;
			case 2: return DiceFace.PUSH;
			case 3: return DiceFace.PUSH;
			case 4: return DiceFace.BOTH_DOWN;
			case 5: return DiceFace.DEFENDER_STUMBLES;
			case 6: return DiceFace.DEFENDER_KNOCKED_DOWN;
			default: break;
		}
		return null;
	}
	
	public int getResultAsInt() {
		// Doesn't make sense
		return 1000;
	}

	public DiceFace getResult() {
		return result;
	}

	public boolean isRolled() {
		return rolled;
	}

	public void setResult(DiceFace result) {
		this.result = result;
	}

	public void setRolled(boolean rolled) {
		this.rolled = rolled;
	}
	
}
