package models.humans;

import models.Player;
import models.Race;
import models.Skill;

public class HumanBlitzer extends Player{

	public HumanBlitzer(int number, String teamName){
		super(Race.HUMANS, "Blitzer", number, teamName);
		this.cost = 90000;
		this.MA = 7;
		this.ST = 3;
		this.AG = 3;
		this.AV = 8;
		this.skills.add(Skill.BLOCK);
	}
}
