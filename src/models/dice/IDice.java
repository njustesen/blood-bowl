package models.dice;


public interface IDice {

	public void roll();
	
	public DiceFace getResult();
	
	public int getResultAsInt();
	
}
