package de.snx.monstera.data.battle;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

import de.snx.monstera.data.IValueID;
import de.snx.psf.PSFFileIO;
import lombok.Getter;

@Getter
public class Ability implements IValueID {

	public static final Ability EMPTY_ABILITY = new Ability.Builder(0, "Strugle", Type.NORMAL).setPower(20).build();

	private int id;
	private String name;
	private String desc;
	private Type type;
	private Category cat;
	private int power;
	private int acc;
	private int AP;
	private int priority;
	private double critMultiplyer;

	public enum Category {
		NULL, PHYSICAL, SPECIAL, STATUS
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}

	private Ability(int id, String name, String desc, Type type, Category cat, int power, int acc, int ap, int ptiority,
			double critMultiplyer) {
		this.id = id;
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

	public Ability(PSFFileIO file) {
		this.name = file.readString("name");
		this.desc = file.readString("desc");
		Type t;
		try {
			Stream<Field> fields = Arrays.asList(Type.class.getDeclaredFields()).stream();
			Stream<Field> filtered = fields.filter(f -> {
				try {
					return f.get(null) instanceof Type && ((Type) f.get(null)).name.equals(file.readString("type"));
				} catch (Exception e) {
					System.err.println(e.getLocalizedMessage());
					return false;
				}
			});
			t = (Type) filtered.findFirst().get().get(null);
		} catch (Exception e) {
			t = Type.NONE;
		}
		this.type = t;
		this.cat = Category.values()[file.readInt("cat")];
		this.power = file.readInt("power");
		this.acc = file.readInt("acc");
		this.AP = file.readInt("ap");
		this.priority = file.readInt("priority");
		this.critMultiplyer = file.readDouble("critM");
	}

	public void save(PSFFileIO file) {
		file.write("name", name);
		file.write("desc", desc);
		file.write("type", type.name);
		file.write("cat", cat.ordinal());
		file.write("power", power);
		file.write("acc", acc);
		file.write("ap", AP);
		file.write("priority", priority);
		file.write("critM", critMultiplyer);
	}

	@Override
	public String toString() {
		return name;
	}

	public static class Builder {

		private int id;
		private String name;
		private String desc;
		private Type type;
		private Category cat;
		private int power = 0;
		private int acc = 100;
		private int AP = 20;
		private int priority = 0;
		private double critMultiplyer = 1;

		public Builder(int id, String name, Type type) {
			this(id, name, type, Category.NULL);
		}

		public Builder(int id, String name, Type type, Category cat) {
			this.id = id;
			this.name = name;
			this.type = type;
			if (cat == Category.NULL)
				this.cat = type.isSpecial ? Category.SPECIAL : Category.PHYSICAL;
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

		public Builder setCritMultiplyer(double critMultiplyer) {
			this.critMultiplyer = critMultiplyer;
			return this;
		}

		public Ability build() {
			return new Ability(id, name, desc, type, cat, power, acc, AP, priority, critMultiplyer);
		}

	}

}
