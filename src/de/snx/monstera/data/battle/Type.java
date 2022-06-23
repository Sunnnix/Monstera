package de.snx.monstera.data.battle;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Supplier;

public class Type {

	// The Types weak, strong and immune stats are used as attack.
	// If an attack has an immune status means that this attack would do nothing
	public static Type NONE;
	public static Type NORMAL;
	public static Type FLYING;
	public static Type FIGHTING;
	public static Type FIRE;
	public static Type BUG;
	public static Type ICE;
	public static Type WATER;
	public static Type POISON;
	public static Type PSYCHIC;
	public static Type ELECTRIC;
	public static Type ROCK;
	public static Type GHOST;
	public static Type GRASS;
	public static Type GROUND;
	public static Type DRAGON;

	static {
		NONE = new Type.Builder("None").build();
		NORMAL = new Type.Builder("Normal").addWeak(() -> new Type[] { ROCK }).addImmune(() -> new Type[] { GHOST })
				.build();
		FLYING = new Type.Builder("Flying").addWeak(() -> new Type[] { ROCK, ELECTRIC })
				.addStrong(() -> new Type[] { FIGHTING, BUG, GRASS }).build();
		FIGHTING = new Type.Builder("Fighting").addWeak(() -> new Type[] { FLYING, POISON, BUG, PSYCHIC })
				.addStrong(() -> new Type[] { NORMAL, ROCK, ICE }).build();
		FIRE = new Type.Builder("Fire", true).addWeak(() -> new Type[] { ROCK, FIRE, WATER, DRAGON })
				.addStrong(() -> new Type[] { BUG, GRASS, ICE }).build();
		BUG = new Type.Builder("Bug").addWeak(() -> new Type[] { FIGHTING, FLYING, POISON, GHOST, FIRE })
				.addStrong(() -> new Type[] { GRASS, PSYCHIC }).build();
		ICE = new Type.Builder("Ice", true).addWeak(() -> new Type[] { FIRE, WATER, ICE })
				.addStrong(() -> new Type[] { FLYING, GROUND, GRASS, DRAGON }).build();
		WATER = new Type.Builder("Water", true).addWeak(() -> new Type[] { WATER, GRASS, DRAGON })
				.addStrong(() -> new Type[] { GROUND, ROCK, FIRE }).build();
		POISON = new Type.Builder("Poison").addWeak(() -> new Type[] { POISON, GROUND, ROCK, GHOST })
				.addStrong(() -> new Type[] { GRASS }).build();
		PSYCHIC = new Type.Builder("Psychic", true).addWeak(() -> new Type[] { PSYCHIC })
				.addStrong(() -> new Type[] { FIGHTING, POISON }).build();
		ELECTRIC = new Type.Builder("Elictric", true).addWeak(() -> new Type[] { GRASS, ELECTRIC, DRAGON })
				.addStrong(() -> new Type[] { FLYING, WATER }).addImmune(() -> new Type[] { GROUND }).build();
		ROCK = new Type.Builder("Rock").addWeak(() -> new Type[] { FIGHTING, GROUND })
				.addStrong(() -> new Type[] { FLYING, BUG, FIRE, ICE }).build();
		GHOST = new Type.Builder("Ghost").addStrong(() -> new Type[] { GHOST, PSYCHIC })
				.addImmune(() -> new Type[] { NORMAL }).build();
		GRASS = new Type.Builder("Grass", true).addWeak(() -> new Type[] { FLYING, POISON, BUG, FIRE, GRASS, DRAGON })
				.addStrong(() -> new Type[] { GROUND, ROCK, WATER }).build();
		GROUND = new Type.Builder("Ground").addWeak(() -> new Type[] { BUG, GRASS })
				.addStrong(() -> new Type[] { POISON, ROCK, FIRE, ELECTRIC }).addImmune(() -> new Type[] { FLYING })
				.build();
		DRAGON = new Type.Builder("Dragon", true).addStrong(() -> new Type[] { DRAGON }).build();
	}

	public static String[] getTypes() {
		return Arrays.asList(Type.class.getFields()).stream().filter(f -> f.getType().equals(Type.class)).map(f -> {
			try {
				return ((Type) f.get(null)).name;
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw new RuntimeException(e);
			}
		}).toArray(String[]::new);
	}

	public static Type getTypeFromString(String name) {
		try {
			Field[] f = Type.class.getFields();
			for (Field field : f) {
				Type t = (Type) field.get(null);
				if (t.name.equals(name))
					return t;
			}
			return Type.NONE;
		} catch (Exception e) {
			return null;
		}
	}

	public final String name;
	public final boolean isSpecial;
	public final Supplier<Type[]> weak, strong, immune;

	private Type(String name, boolean isSpecial, Supplier<Type[]> weak, Supplier<Type[]> strong,
			Supplier<Type[]> immune) {
		this.name = name;
		this.isSpecial = isSpecial;
		this.weak = weak;
		this.strong = strong;
		this.immune = immune;
	}

	public static class Builder {

		private String name;
		private boolean isSpecial;
		private Supplier<Type[]> weak = () -> new Type[0], strong = () -> new Type[0], immune = () -> new Type[0];

		public Builder(String name) {
			this.name = name;
		}

		public Builder(String name, boolean isSpecial) {
			this.name = name;
			this.isSpecial = isSpecial;
		}

		public Builder addWeak(Supplier<Type[]> types) {
			weak = types;
			return this;
		}

		public Builder addStrong(Supplier<Type[]> types) {
			strong = types;
			return this;
		}

		public Builder addImmune(Supplier<Type[]> types) {
			immune = types;
			return this;
		}

		public Type build() {
			return new Type(name, isSpecial, weak, strong, immune);
		}

	}

}
