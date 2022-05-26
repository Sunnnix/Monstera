package de.snx.monstera.battle.monstertype;

import de.snx.monstera.battle.BattlerImage;
import de.snx.monstera.battle.Battlers;
import de.snx.monstera.battle.Type;

public class MonsterType {

	public static final MonsterType NULL;
	public static final MonsterType BULBASAUR, IVYSAUR, VENUSAUR;

	public final int ID;
	public final String name;

	public final Type type1, type2;

	// Default attribute
	public final int hp, atk, def, s_atk, s_def, speed;

	public final int xpDrop, xpNeed, xpInc; // for xp calculations
	public final double xpInc2;

	public final BattlerImage img;

	private MonsterType(int id, String name, Type type1, Type type2, int hp, int atk, int def, int s_atk, int s_def,
			int speed, int xpDrop, int xpToNextLevel, int xpInc, double xpInc2, BattlerImage img) {
		this.ID = id;
		this.name = name;
		this.type1 = type1;
		this.type2 = type2;
		this.hp = hp;
		this.atk = atk;
		this.def = def;
		this.s_atk = s_atk;
		this.s_def = s_def;
		this.speed = speed;
		this.xpDrop = xpDrop;
		this.xpNeed = xpToNextLevel;
		this.xpInc = xpInc;
		this.xpInc2 = xpInc2;
		this.img = img;
	}

	static {
		NULL = new Builder(0, "MissingNo").build();
		BULBASAUR = new Builder(1, "Bulbasaur").setType(Type.GRASS, Type.POISON).setStats(45, 49, 49, 65, 65, 45)
				.build();
		IVYSAUR = new Builder(2, "Ivysaur").setType(Type.GRASS, Type.POISON).setStats(60, 62, 63, 80, 80, 60).build();
		VENUSAUR = new Builder(3, "Venusaur").setType(Type.GRASS, Type.POISON).setStats(80, 82, 83, 100, 100, 80)
				.build();
	}

	public static class Builder {

		private int id;
		private String name;
		private Type type1 = Type.NONE, type2;
		private int hp = 20, atk = 20, def = 20, s_atk = 20, s_def = 20, speed = 20;
		private int xpDrop = 8, xpNeed = 25, xpInc1 = 5;
		private double xpInc2 = 1.1;
		private BattlerImage img = Battlers.NULL;

		public Builder(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public Builder setType(Type type1, Type type2) {
			this.type1 = type1;
			this.type2 = type2;
			return this;
		}

		public Builder setType(Type type) {
			return setType(type, null);
		}

		public Builder setStats(int hp, int atk, int def, int s_atk, int s_def, int speed) {
			this.hp = hp;
			this.atk = atk;
			this.def = def;
			this.s_atk = s_atk;
			this.s_def = s_def;
			this.speed = speed;
			return this;
		}

		public Builder setXPStats(int drop, int needed, int inc1, double inc2) {
			this.xpDrop = drop;
			this.xpNeed = needed;
			this.xpInc1 = inc1;
			this.xpInc2 = inc2;
			return this;
		}

		public Builder setImage(BattlerImage img) {
			this.img = img;
			return this;
		}

		public MonsterType build() {
			return new MonsterType(id, name, type1, type2, hp, atk, def, s_atk, s_def, speed, xpDrop, xpNeed, xpInc1,
					xpInc2, img);
		}

	}

}
