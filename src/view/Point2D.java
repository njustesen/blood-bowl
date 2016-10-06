package view;

public class Point2D {

	private int x, y;

	public Point2D(int x, int y) {

		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return x;
	}
	
	public void setX(int newX){
		this.x = newX;
	}
	
	public int getY(){
		return y;
	}
	
	public void setY(int newY){
		this.y = newY;
	}
	
	public Point2D add(Point2D other){
		return new Point2D(x+other.x,y+other.y);
	}
	
}
