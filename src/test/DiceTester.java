package test;

import models.dice.BB;
import models.dice.D6;
import models.dice.D8;

public class DiceTester {
	
	public static void testDices(){
		
		// D6
		for (int i = 0; i < 1000; i++){
			D6 dice = new D6();
			dice.roll();
			System.out.println(dice.getResultAsInt());
			
		}
		
		// D8
		for (int i = 0; i < 1000; i++){
			D8 dice = new D8();
			dice.roll();
			System.out.println(dice.getResultAsInt());
			
		}
		
		// BB
		for (int i = 0; i < 1000; i++){
			BB dice = new BB();
			dice.roll();
			System.out.println(dice.getResult());
			
		}
		
	}
	
}
