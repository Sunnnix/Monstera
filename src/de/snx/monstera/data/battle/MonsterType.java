package de.snx.monstera.data.battle;

import java.util.ArrayList;

import de.snx.monstera.data.IValueID;
import de.snx.monstera.util.Pair;
import de.snx.psf.PSFFileIO;
import lombok.Getter;

public class MonsterType implements IValueID {

	public static final MonsterType MISSINGNO = new MonsterType.Builder(0, "---").build();

	@Getter
	public final int ID;
	public final String name;

	public final Type type1, type2;

	// Default attribute
	public final int hp, atk, def, s_atk, s_def, speed;

	public final int xpDrop, xpNeed, xpInc; // for xp calculations
	public final double xpInc2;

	public final Pair<Byte, Ability>[] abilities;

	public final MonsterImage img;

	@SuppressWarnings("unchecked")
	public MonsterType(PSFFileIO file) {
		this.ID = file.readInt("id");
		this.name = file.readString("name");
		this.type1 = Type.getTypeFromString(file.readString("type1"));
		this.type2 = Type.getTypeFromString(file.readString("type2"));
		this.hp = file.readInt("hp");
		this.atk = file.readInt("atk");
		this.def = file.readInt("def");
		this.s_atk = file.readInt("s_atk");
		this.s_def = file.readInt("s_def");
		this.speed = file.readInt("speed");
		this.xpDrop = file.readInt("xp_drop");
		this.xpNeed = file.readInt("xp_need");
		this.xpInc = file.readInt("xp_inc");
		this.xpInc2 = file.readDouble("xp_inc2");
		ArrayList<Pair<Byte, Ability>> abilities = new ArrayList<>();
//		file.room("abilities", _s -> {
//			int size = file.readInt("size");
//			for (int i = 0; i < size; i++) {
//				file.room("a" + i, __s -> {
//					abilities.add(
//							new Pair<Byte, Ability>(file.readByte("lv"), Ability.getAbility(file.readString("name"))));
//				});
//			}
//		});
		this.abilities = abilities.toArray(new Pair[0]);
		this.img = MonsterImage.NULL;
	}

	public void save(PSFFileIO file) {
		file.write("id", ID);
		file.write("name", name);
		file.write("type1", type1.name);
		file.write("type2", type2.name);
		file.write("hp", hp);
		file.write("atk", atk);
		file.write("def", def);
		file.write("s_atk", s_atk);
		file.write("s_def", s_def);
		file.write("speed", speed);
		file.write("xp_drop", xpDrop);
		file.write("xp_need", xpNeed);
		file.write("xp_inc", xpInc);
		file.write("xp_inc2", xpInc2);
		file.room("abilities", _s -> {
			file.write("size", abilities.length);
			for (int i = 0; i < abilities.length; i++) {
				Pair<Byte, Ability> ability = abilities[i];
				file.room("a" + i, __s -> {
					file.write("lv", ability.object1);
					file.write("name", ability.object2.getName());
				});
			}
		});
	}

	@SuppressWarnings("unchecked")
	private MonsterType(int id, String name, Type type1, Type type2, int hp, int atk, int def, int s_atk, int s_def,
			int speed, int xpDrop, int xpToNextLevel, int xpInc, double xpInc2, ArrayList<Pair<Byte, Ability>> abilOnLv,
			MonsterImage img) {
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
		this.abilities = abilOnLv.toArray(new Pair[0]);
		this.img = img;
	}

	public static class Builder {

		private int id;
		private String name;
		private Type type1 = Type.NORMAL, type2 = Type.NONE;
		private int hp = 20, atk = 20, def = 20, s_atk = 20, s_def = 20, speed = 20;
		private int xpDrop = 8, xpNeed = 25, xpInc1 = 5;
		private double xpInc2 = 1.1;
		private MonsterImage img = MonsterImage.NULL;
		private ArrayList<Pair<Byte, Ability>> abilities = new ArrayList<>();

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

		public Builder setImage(MonsterImage img) {
			this.img = img;
			return this;
		}

		public Builder addAb(int level, Ability ability) {
			this.abilities.add(new Pair<Byte, Ability>((byte) level, ability));
			return this;
		}

		public MonsterType build() {
			return new MonsterType(id, name, type1, type2, hp, atk, def, s_atk, s_def, speed, xpDrop, xpNeed, xpInc1,
					xpInc2, abilities, img);
		}

	}

	public Ability[] getAbilitiesOnLevelUp(byte level) {
		ArrayList<Ability> a = new ArrayList<>();
		for (Pair<Byte, Ability> abLv : abilities)
			if (abLv.object1 == level)
				a.add(abLv.object2);
		return a.toArray(new Ability[0]);
	}

	@Override
	public String toString() {
		return (ID >= 100 ? ID : ID >= 10 ? "0" + ID : "00" + ID) + " - " + name;
	}

	/**
	 * @deprecated id is final and can't be changed
	 */
	@Deprecated
	@Override
	public void setID(int id) {
	}

}
