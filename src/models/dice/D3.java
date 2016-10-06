package models.dice;

public class D3 implements IDice{

	DiceFace result;
	boolean rolled;
	
	public D3() {
		super();
		this.result = null;
		this.rolled = false;
	}
	
	public void roll(){
		rolled = true;
		int roll = (int)(Math.random()*3 + 1);
		
		result = D3.intToD3Roll(roll);
		
	}

	public static int d3RollToInt(DiceFace face){
		switch(face){
			case ONE: return 1;
			case TWO: return 2;
			case THREE: return 3;
			default: break;
		}
		return 10000;
	}
	
	public static DiceFace intToD3Roll(int i){
		switch(i){
			case 1: return DiceFace.ONE;
			case 2: return DiceFace.TWO;
			case 3: return DiceFace.THREE;
			default: break;
		}
		return null;
	}
	
	public int getResultAsInt() {
		
		return d3RollToInt(result);
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
