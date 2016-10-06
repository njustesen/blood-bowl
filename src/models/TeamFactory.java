package models;

import java.util.ArrayList;
import java.util.Arrays;

import models.humans.HumanBlitzer;
import models.humans.HumanCatcher;
import models.humans.HumanLineman;
import models.humans.HumanThrower;
import models.orcs.OrcBlackOrc;
import models.orcs.OrcBlitzer;
import models.orcs.OrcLineman;
import models.orcs.OrcThrower;

public class TeamFactory {

	public static Team getHumanTeam() {
		
		String teamName = "Humans";
		
		Player p1 = new HumanThrower(1, teamName); 
		Player p2 = new HumanBlitzer(2, teamName); 
		Player p3 = new HumanBlitzer(3, teamName); 
		Player p4 = new HumanBlitzer(4, teamName); 
		Player p5 = new HumanBlitzer(5, teamName); 
		Player p6 = new HumanCatcher(6, teamName); 
		Player p7 = new HumanCatcher(7, teamName); 
		Player p8 = new HumanLineman(8, teamName); 
		Player p9 = new HumanLineman(9, teamName); 
		Player p10 = new HumanLineman(10, teamName); 
		Player p11 = new HumanLineman(11, teamName); 
		Player p12 = new HumanLineman(12, teamName); 
		ArrayList<Player> players = new ArrayList<Player>(
				Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12)
			);
		

		return new Team(players, 4, 5, 0, teamName);
	}

	public static Team getOrcTeam() {
		
		String teamName = "Orcses";
		
		Player p1 = new OrcThrower(1, teamName); 
		Player p2 = new OrcBlackOrc(2, teamName); 
		Player p3 = new OrcBlackOrc(3, teamName); 
		Player p4 = new OrcBlitzer(4, teamName); 
		Player p5 = new OrcBlitzer(5, teamName); 
		Player p6 = new OrcLineman(6, teamName); 
		Player p7 = new OrcLineman(7, teamName); 
		Player p8 = new OrcLineman(8, teamName); 
		Player p9 = new OrcLineman(9, teamName); 
		Player p10 = new OrcLineman(10, teamName); 
		Player p11 = new OrcLineman(11, teamName); 
		Player p12 = new OrcLineman(12, teamName); 
		ArrayList<Player> players = new ArrayList<Player>(
				Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12)
			);
		

		return new Team(players, 3, 6, 0, teamName);
	}
	
}