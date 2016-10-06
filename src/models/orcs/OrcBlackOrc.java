package models.orcs;

import models.Player;
import models.Race;
import models.Skill;

public class OrcBlackOrc extends Player{

	public OrcBlackOrc(int number, String teamName){
		super(Race.ORCS, "Black Orc", number, teamName);
		this.title = "black orc";
		this.cost = 80000;
		this.MA = 4;
		this.ST = 4;
		this.AG = 2;
		this.AV = 9;
		this.skills.add(Skill.NONE);
	}
}
