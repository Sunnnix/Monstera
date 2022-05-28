package de.snx.monstera.battle;

public class Abilities {

	public static final Ability NULL;
	public static final Ability TACKLE;
	public static final Ability GROWL;
	public static final Ability LEECH_SEED;
	public static final Ability VINE_WHIP;
	public static final Ability POISON_POWDER;
	public static final Ability RAZOR_LEAF;
	public static final Ability QUICK_ATTACK;
	public static final Ability AMBER;

	static {
		NULL = new Ability.Builder("---", Type.NONE).setDescription("---").setPower(0).setAccuracy(0).setAP(0).build();
		TACKLE = new Ability.Builder("Tackle", Type.NORMAL).setPower(35).setAccuracy(95).setAP(35).build();
		GROWL = new Ability.Builder("Growl", Type.NORMAL).setAP(40).build(); // TODO add effect
		LEECH_SEED = new Ability.Builder("Leech Seed", Type.GRASS).setAccuracy(90).setAP(10).build(); // TODO add effect
		VINE_WHIP = new Ability.Builder("Vine Whip", Type.GRASS).setPower(35).setAP(10).build();
		POISON_POWDER = new Ability.Builder("Poison Powder", Type.POISON).setAccuracy(75).setAP(35).build(); // TODO add effect
		RAZOR_LEAF = new Ability.Builder("Razor Leaf", Type.GRASS).build();
		QUICK_ATTACK = new Ability.Builder("Quick Attack", Type.NORMAL).setPower(40).setAccuracy(100).setAP(30)
				.setPriority(1).build();
		AMBER = new Ability.Builder("Amber", Type.FIRE).setPower(40).setAccuracy(100).setAP(25).build();
	}

}
