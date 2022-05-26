package de.snx.monstera.battle;

import java.awt.image.BufferedImage;

import de.snx.monstera.battle.monstertype.MonsterType;
import lombok.Getter;
import lombok.Setter;

public class Battler {

	private String name;

	private MonsterType type = MonsterType.NULL;

	private double hp, atk, def, s_atk, s_def, speed;
	@Getter
	private byte level = 5;

	private double currentHP;

	private byte de_ATK, de_DEF, de_SATK, de_SDEF, de_SPEED;

	@Getter
	@Setter
	private int xp, xpToNextLv = 25, xpDrop = 8;

	private AbilityData[] abilitys = new AbilityData[] { new AbilityData(null), new AbilityData(null),
			new AbilityData(null), new AbilityData(null) };

	@Setter
	private double graphicHPMod, graphicXPMod;

	public Battler(int level) {
		this.level = (byte) level;
		generateStats();
		setAbilitys();
	}

	public Battler setType(MonsterType type) {
		this.type = type;
		return this;
	}

	/**
	 * for initialisations
	 */
	private void generateStats() {
		int lv = level;
		level = 0;
		for (int i = 0; i < lv; i++)
			onLevelUp();
	}

	private int[] onLevelUp() {
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
			hp += type.hp * .15 * (1 + level * .15);
			currentHP += type.hp * .15 * (1 + level * .15);
			atk = type.atk * .075 * (1 + level * .15);
			def = type.def * .075 * (1 + level * .15);
			s_atk = type.s_atk * .075 * (1 + level * .15);
			s_def = type.s_def * .075 * (1 + level * .15);
			speed = type.speed * .075 * (1 + level * .15);
			increase[0] = (int) (hp - prefState[0]);
			increase[1] = (int) (atk - prefState[1]);
			increase[2] = (int) (def - prefState[2]);
			increase[3] = (int) (s_atk - prefState[3]);
			increase[4] = (int) (s_def - prefState[4]);
			increase[5] = (int) (speed - prefState[5]);
			return increase;
		}
	}

	private void setAbilitys() {
		abilitys[0] = new AbilityData(Abilities.TACKLE);
		abilitys[1] = new AbilityData(Abilities.QUICK_ATTACK);
		abilitys[2] = new AbilityData(Abilities.AMBER);
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
		for (AbilityData abilityData : abilitys)
			if (abilityData != null && abilityData.getType() != null)
				count++;
		AbilityData[] a = new AbilityData[count];
		count = 0;
		for (AbilityData abilityData : abilitys)
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

	public void addXP(int xp) {
		this.xp += xp;
		if (this.xp >= xpToNextLv) {
			this.xp = 0;
			xpToNextLv *= 1.15;
			onLevelUp();
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

		public int attack(Battler a, Battler b) {
			double dmg = ability.power / 100d * a.atk * 2.5;
			boolean crit = Math.random() < .25;
			b.currentHP -= (int) (dmg * (crit ? 2 : 1));
			ap--;
			return Ability.STATUS_SUCCESS + (crit ? Ability.STATUS_CRIT : 0);
		}

		public double getPower() {
			return ability.power;
		}

		public void useAP() {
			ap--;
		}

	}

	public double getEffectifeLevel(Type type) {
		double effectness = 1;
		// Type 1
		Type[] immune = this.type.type1.immune.get();
		for (Type t : immune)
			if (t.equals(type))
				effectness = 0;
		Type[] strong = this.type.type1.strong.get();
		for (Type t : strong)
			if (t.equals(type))
				effectness *= .5;
		Type[] weak = this.type.type1.weak.get();
		for (Type t : weak)
			if (t.equals(type))
				effectness *= 2;
		// Type 2
		if (this.type.type2 != null) {
			immune = this.type.type2.immune.get();
			for (Type t : immune)
				if (t.equals(type))
					effectness = 0;
			strong = this.type.type2.strong.get();
			for (Type t : strong)
				if (t.equals(type))
					effectness *= .5;
			weak = this.type.type2.weak.get();
			for (Type t : weak)
				if (t.equals(type))
					effectness *= 2;
		}
		return effectness;
	}

}
