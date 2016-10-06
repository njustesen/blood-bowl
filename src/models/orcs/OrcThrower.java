package models.orcs;

import models.Player;
import models.Race;
import models.Skill;

public class OrcThrower extends Player{

	public OrcThrower(int number, String teamName){
		super(Race.ORCS, "Thrower", number, teamName);
		this.cost = 70000;
		this.MA = 5;
		this.ST = 3;
		this.AG = 3;
		this.AV = 8;
		this.skills.add(Skill.SURE_HANDS);
		this.skills.add(Skill.PASS);
	}
}
