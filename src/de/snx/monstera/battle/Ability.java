package de.snx.monstera.battle;

public class Ability {

	public static final int STATUS_SUCCESS = 0;
	public static final int STATUS_MISSED = 1;
	public static final int STATUS_EFFECTIVE = 2;
	public static final int STATUS_NOT_EFFECTIVE = 3;
	public static final int STATUS_IMMUNE = 4;
	public static final int STATUS_CRIT = 10;

	public final String name;
	public final String desc;
	public final Type type;
	public final int cat;
	public final int power;
	public final int acc;
	public final int AP;
	public final int priority;
	public final int critMultiplyer;

	private Ability(String name, String desc, Type type, int cat, int power, int acc, int ap, int ptiority,
			int critMultiplyer) {
		this.name = name;
		this.desc = desc;
		this.type = type;
		this.cat = cat;
		this.power = power;
		this.acc = acc;
		this.AP = ap;
		this.priority = ptiority;
		this.critMultiplyer = critMultiplyer;
	}

	public static class Builder {

		public static final int CAT_PHYSICAL = 0;
		public static final int CAT_SPECIAL = 1;
		public static final int CAT_STATUS = 2;

		private String name;
		private String desc;
		private Type type;
		private int cat;
		private int power = 100;
		private int acc = 100;
		private int AP = 20;
		private int priority = 0;
		private int critMultiplyer = 1;

		public Builder(String name, Type type) {
			this(name, type, -1);
		}

		public Builder(String name, Type type, int cat) {
			this.name = name;
			this.type = type;
			if (cat == -1)
				this.cat = type.isSpecial ? CAT_SPECIAL : CAT_PHYSICAL;
			else
				this.cat = cat;
		}

		public Builder setDescription(String desc) {
			this.desc = desc;
			return this;
		}

		public Builder setPower(int power) {
			this.power = power;
			return this;
		}

		public Builder setAccuracy(int acc) {
			this.acc = acc;
			return this;
		}

		public Builder setAP(int ap) {
			this.AP = ap;
			return this;
		}

		public Builder setPriority(int priority) {
			this.priority = priority;
			return this;
		}

		public Builder setCritMultiplyer(int critMultiplyer) {
			this.critMultiplyer = critMultiplyer;
			return this;
		}

		public Ability build() {
			return new Ability(name, desc, type, cat, power, acc, AP, priority, critMultiplyer);
		}

	}

}
