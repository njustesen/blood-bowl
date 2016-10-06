package models.orcs;

import models.Player;
import models.Race;
import models.Skill;

public class OrcBlitzer extends Player{

	public OrcBlitzer(int number, String teamName){
		super(Race.ORCS, "Blitzer", number, teamName);
		this.title = "Blitzer";
		this.cost = 80000;
		this.MA = 6;
		this.ST = 3;
		this.AG = 3;
		this.AV = 9;
		this.skills.add(Skill.BLOCK);
	}
}
