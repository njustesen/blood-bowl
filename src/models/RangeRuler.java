package models;

public class RangeRuler {

	private static final int quickPass = 3;
	private static final int shortPass = 7;
	private static final int longPass = 10;
	private static final int longBomb = 13;
	
	public static PassRange getPassRange(int distance){
		
		if (distance > longBomb){
			return PassRange.OUT_OF_RANGE;
		} else if (distance > longPass){
			return PassRange.LONG_BOMB;
		} else if (distance > shortPass){
			return PassRange.LONG_PASS;
		} else if (distance > quickPass){
			return PassRange.SHORT_PASS;
		} else {
			return PassRange.QUICK_PASS;
		}
		
	}
	
}
