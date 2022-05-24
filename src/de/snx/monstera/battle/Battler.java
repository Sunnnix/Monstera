package de.snx.monstera.battle;

import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;

public class Battler {

	@Getter
	private String name = "MissingNo";

	private Type type1 = Type.NONE, type2;

	// Default attribute
	private int baseHP = 20, baseATK = 20, baseDEF = 20, baseSATK = 20, baseSDEF = 20, baseSPEED = 20;
	private double hp, atk, def, s_atk, s_def, speed;
	@Getter
	private byte level = 5;

	private double currentHP;

	private byte de_ATK, de_DEF, de_SATK, de_SDEF, de_SPEED;

	@Getter
	@Setter
	private int xp, xpToNextLv = 25, xpDrop = 8;

	private BattlerImage img = Battlers.NULL;

	private AbilityData[] abilitys = new AbilityData[] { new AbilityData(null), new AbilityData(null),
			new AbilityData(null), new AbilityData(null) };

	@Setter
	private double graphicHPMod, graphicXPMod;

	public Battler(String name, int level) {
		this.name = name;
		this.level = (byte) level;
		generateStats();
		setAbilitys();
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
			hp = baseHP / 3;
			currentHP = hp;
			atk = baseATK / 3;
			def = baseDEF / 3;
			s_atk = baseSATK / 3;
			s_def = baseDEF / 3;
			speed = baseSPEED / 3;
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
			hp += baseHP * .15 * (1 + level * .15);
			currentHP += baseHP * .15 * (1 + level * .15);
			atk = baseATK * .075 * (1 + level * .15);
			def = baseDEF * .075 * (1 + level * .15);
			s_atk = baseSATK * .075 * (1 + level * .15);
			s_def = baseSDEF * .075 * (1 + level * .15);
			speed = baseSPEED * .075 * (1 + level * .15);
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
		abilitys[0] = new AbilityData(Abilitys.TACKLE);
		abilitys[1] = new AbilityData(Abilitys.QUICK_ATTACK);
		abilitys[2] = new AbilityData(Abilitys.AMBER);
	}

	public BufferedImage getImage(int pos) {
		return img.get(pos);
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

	public class AbilityData {

		private Ability ability = Abilitys.NULL;
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
			if (ability == Abilitys.NULL)
				return "---";
			return ability.name;
		}

		public Type getType() {
			if (ability == Abilitys.NULL)
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

}
