package de.snx.monstera.battle;

public class Abilities {

	public static final Ability NULL;
	public static final Ability TACKLE;
	public static final Ability QUICK_ATTACK;
	public static final Ability AMBER;

	static {
		NULL = new Ability.Builder("---", Type.NONE).setDescription("---").setPower(0).setAccuracy(0).setAP(0).build();
		TACKLE = new Ability.Builder("Tackle", Type.NORMAL).setPower(40).setAccuracy(100).setAP(35).build();
		QUICK_ATTACK = new Ability.Builder("Quick Attack", Type.NORMAL).setPower(40).setAccuracy(100).setAP(30)
				.setPriority(1).build();
		AMBER = new Ability.Builder("Amber", Type.FIRE).setPower(40).setAccuracy(100).setAP(25).build();
	}

}
