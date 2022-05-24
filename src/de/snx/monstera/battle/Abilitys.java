package de.snx.monstera.battle;

public class Abilitys {

	public static final Ability NULL = new Ability.Builder("---", Type.NONE).setDescription("---").setPower(0)
			.setAccuracy(0).setAP(0).build();
	public static final Ability TACKLE = new Ability.Builder("Tackle", Type.NORMAL).setPower(40).setAccuracy(100)
			.setAP(35).build();
	public static final Ability QUICK_ATTACK = new Ability.Builder("Queick Attack", Type.NORMAL).setPower(40)
			.setAccuracy(100).setAP(30).setPriority(1).build();
	public static final Ability AMBER = new Ability.Builder("Amber", Type.FIRE).setPower(40).setAccuracy(100).setAP(25)
			.build();

}
