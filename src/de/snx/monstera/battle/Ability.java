package de.snx.monstera.battle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.snx.psf.PSFFileIO;

public class Ability {

	private static HashMap<String, Ability> abilities = new HashMap<>();

	public static final Ability EMPTY_ABILITY = new Ability.Builder("---", Type.NONE).build();

	public static void loadAll(PSFFileIO file) {
		abilities.clear();
		file.room("abilities", _s -> {
			int size = file.readInt("size");
			for (int i = 0; i < size; i++)
				file.room("a" + i, __s -> {
					final Ability a = new Ability(file);
					abilities.put(a.name, a);
				});
		});
	}

	public static void saveAll(PSFFileIO file) {
		file.room("abilities", _s -> {
			file.write("size", abilities.size());
			ArrayList<Ability> list = new ArrayList<>(abilities.values());
			for (int i = 0; i < list.size(); i++) {
				final int index = i;
				file.room("a" + i, __S -> list.get(index).save(file));
			}
		});
	}

	public static String[] getAbilities() {
		return abilities.values().stream().map(a -> a.name).collect(Collectors.toList()).toArray(new String[0]);
	}

	public static Ability getAbility(String name) {
		Ability a = abilities.get(name);
		if (a == null)
			return EMPTY_ABILITY;
		else
			return a;
	}

	public static void addAbility(Ability ability, String oldName) {
		if (oldName != null && !ability.name.equals(oldName))
			abilities.remove(oldName);
		abilities.put(ability.name, ability);
	}

	public enum Category {
		NULL, PHYSICAL, SPECIAL, STATUS
	}

	public final String name;
	public final String desc;
	public final Type type;
	public final Category cat;
	public final int power;
	public final int acc;
	public final int AP;
	public final int priority;
	public final double critMultiplyer;

	private Ability(String name, String desc, Type type, Category cat, int power, int acc, int ap, int ptiority,
			double critMultiplyer) {
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

	public static class Builder {

		private String name;
		private String desc;
		private Type type;
		private Category cat;
		private int power = 0;
		private int acc = 100;
		private int AP = 20;
		private int priority = 0;
		private double critMultiplyer = 1;

		public Builder(String name, Type type) {
			this(name, type, Category.NULL);
		}

		public Builder(String name, Type type, Category cat) {
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
			return new Ability(name, desc, type, cat, power, acc, AP, priority, critMultiplyer);
		}

	}

}
