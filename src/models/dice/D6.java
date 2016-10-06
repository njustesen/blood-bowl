package models.dice;

public class D6 implements IDice{

	DiceFace result;
	boolean rolled;
	
	public D6() {
		super();
		this.result = null;
		this.rolled = false;
	}
	
	public void roll(){
		rolled = true;
		int roll = (int)(Math.random()*6 + 1);
		
		result = D6.intToD6Roll(roll);
		
	}

	public static int d6RollToInt(DiceFace face){
		switch(face){
			case ONE: return 1;
			case TWO: return 2;
			case THREE: return 3;
			case FOUR: return 4;
			case FIVE: return 5;
			case SIX: return 6;
			default: break;
		}
		return 10000;
	}
	
	public static DiceFace intToD6Roll(int i){
		switch(i){
			case 1: return DiceFace.ONE;
			case 2: return DiceFace.TWO;
			case 3: return DiceFace.THREE;
			case 4: return DiceFace.FOUR;
			case 5: return DiceFace.FIVE;
			case 6: return DiceFace.SIX;
			default: break;
		}
		return null;
	}
	
	public int getResultAsInt() {
		
		return d6RollToInt(result);
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
