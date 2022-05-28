package de.snx.monstera.battle;

import java.awt.image.BufferedImage;

import de.snx.monstera.battle.action.ActionShowText;
import de.snx.monstera.battle.monstertype.MonsterType;
import de.snx.monstera.state.BattleState;
import lombok.Getter;
import lombok.Setter;

public class Battler {

	private String name;

	private MonsterType type = MonsterType.NULL;

	private double hp, atk, def, s_atk, s_def, speed;
	@Getter
	private byte level;

	private double currentHP;

	private byte de_ATK, de_DEF, de_SATK, de_SDEF, de_SPEED;

	@Getter
	@Setter
	private int xp, xpToNextLv = 25, xpDrop = 28;

	private AbilityData[] abilities = new AbilityData[] { new AbilityData(null), new AbilityData(null),
			new AbilityData(null), new AbilityData(null) };

	@Setter
	private double graphicHPMod, graphicXPMod;

	public Battler(int level, MonsterType type) {
		this.level = (byte) level;
		this.type = type;
		generateStats();
	}

	/**
	 * for initialisations
	 */
	private void generateStats() {
		int lv = level;
		level = 0;
		for (int i = 0; i < lv; i++)
			onLevelUp(null);
	}

	private int[] onLevelUp(BattleState state) {
		level++;
		if (level == 1) {
			hp = type.hp / 3;
			currentHP = hp;
			atk = type.atk / 3;
			def = type.def / 3;
			s_atk = type.s_atk / 3;
			s_def = type.s_def / 3;
			speed = type.speed / 3;
			return new int[6];
		} else {
			int[] prefState = new int[6];
			int[] increase = new int[6];
			prefState[0] = (int) hp;
			prefState[1] = (int) atk;
			prefState[2] = (int) def;
			prefState[3] = (int) s_atk;
			prefState[4] = (int) s_def;
			prefState[5] = (int) speed;
			double lvMult = .01;
			double hpMult = .1;
			double mult = .075;
			hp += type.hp * hpMult * (1 + level * lvMult);
			currentHP += type.hp * hpMult * (1 + level * lvMult);
			atk = type.atk * mult * (1 + level * lvMult);
			def = type.def * mult * (1 + level * lvMult);
			s_atk = type.s_atk * mult * (1 + level * lvMult);
			s_def = type.s_def * mult * (1 + level * lvMult);
			speed = type.speed * mult * (1 + level * lvMult);
			increase[0] = (int) (hp - prefState[0]);
			increase[1] = (int) (atk - prefState[1]);
			increase[2] = (int) (def - prefState[2]);
			increase[3] = (int) (s_atk - prefState[3]);
			increase[4] = (int) (s_def - prefState[4]);
			increase[5] = (int) (speed - prefState[5]);
			Ability[] ability = type.getAbilitiesOnLevelUp(level);
			for (Ability a2l : ability)
				for (int i = 0; i < abilities.length; i++)
					if (abilities[i].ability.equals(Abilities.NULL)) {
						abilities[i] = new AbilityData(a2l);
						if (state != null)
							state.setNextAction(
									new ActionShowText(state, getName() + " has learned " + a2l.name + "!"));
						break;
					}
			return increase;
		}
	}

	public BufferedImage getImage(int pos) {
		return type.img.get(pos);
	}

	public int getHp() {
		int hp = (int) ((int) currentHP + graphicHPMod);
		return hp < 0 ? 0 : hp;
	}

	public double getDHP() {
		return (int) currentHP + graphicHPMod;
	}

	public int getMaxHP() {
		return (int) hp;
	}

	public int getAtk() {
		return (int) (atk * (1 + de_ATK / 15));
	}

	public int getDef() {
		return (int) (def * (1 - de_DEF / 15));
	}

	public int getS_atk() {
		return (int) (s_atk * (1 - de_SATK / 15));
	}

	public int getS_def() {
		return (int) (s_def * (1 - de_SDEF / 15));
	}

	public int getSpeed() {
		return (int) (speed * (1 - de_SPEED / 15));
	}

	public AbilityData[] getAbilitys() {
		int count = 0;
		for (AbilityData abilityData : abilities)
			if (abilityData != null && abilityData.getType() != null)
				count++;
		AbilityData[] a = new AbilityData[count];
		count = 0;
		for (AbilityData abilityData : abilities)
			if (abilityData != null && abilityData.getType() != null) {
				a[count] = abilityData;
				count++;
			}
		return a;
	}

	public void setDamage(int damage) {
		currentHP -= damage;
		if (currentHP < 0)
			currentHP = 0;
	}

	public double getDXP() {
		return xp + graphicXPMod;
	}

	public void addXP(int xp, BattleState state) {
		this.xp += xp;
		if (this.xp >= xpToNextLv) {
			this.xp = 0;
			xpToNextLv *= 1.15;
			onLevelUp(state);
		}
	}

	public String getName() {
		if (name == null)
			return type.name;
		return name;
	}

	public class AbilityData {

		private Ability ability = Abilities.NULL;
		@Getter
		private int ap, apMax;

		public AbilityData(Ability ability) {
			if (ability != null) {
				this.ability = ability;
				apMax = ability.AP;
				ap = apMax;
			}
		}

		public String getName() {
			if (ability == Abilities.NULL)
				return "---";
			return ability.name;
		}

		public Type getType() {
			if (ability == Abilities.NULL)
				return null;
			return ability.type;
		}

		public double getPower() {
			return ability.power;
		}

		public void useAP() {
			ap--;
		}

	}

	public double getEffectiveLevel(Type type) {
		double effectiveness = 1;
		// Type 1
		Type[] immune = this.type.type1.immune.get();
		for (Type t : immune)
			if (t.equals(type))
				effectiveness = 0;
		Type[] strong = this.type.type1.strong.get();
		for (Type t : strong)
			if (t.equals(type))
				effectiveness *= .5;
		Type[] weak = this.type.type1.weak.get();
		for (Type t : weak)
			if (t.equals(type))
				effectiveness *= 2;
		// Type 2
		if (this.type.type2 != null) {
			immune = this.type.type2.immune.get();
			for (Type t : immune)
				if (t.equals(type))
					effectiveness = 0;
			strong = this.type.type2.strong.get();
			for (Type t : strong)
				if (t.equals(type))
					effectiveness *= .5;
			weak = this.type.type2.weak.get();
			for (Type t : weak)
				if (t.equals(type))
					effectiveness *= 2;
		}
		return effectiveness;
	}

}
