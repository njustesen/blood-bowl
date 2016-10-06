package game;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class GameLog {
	
	public static LinkedList<String> messeges = new LinkedList<String>();

	public static void push(String msg){
		
		messeges.add(msg);
		
	}
	
	public static String poll(){
		
		if (messeges.size() > 0){
			try {
				return messeges.removeFirst();
			} catch (NoSuchElementException e){
				return "";
			}
			
		}
		
		return "";
	}
	
	public static String peek(){
		if (messeges.size() > 0){
			return messeges.peek();
		}
		
		return "";
	}
	
}
