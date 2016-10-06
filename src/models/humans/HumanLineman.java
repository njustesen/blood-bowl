package models.humans;

import models.Player;
import models.Race;
import models.Skill;

public class HumanLineman extends Player{

	public HumanLineman(int number, String teamName){
		super(Race.HUMANS, "Lineman", number, teamName);
		this.cost = 50000;
		this.MA = 6;
		this.ST = 3;
		this.AG = 3;
		this.AV = 8;
		this.skills.add(Skill.NONE);
	}
}
