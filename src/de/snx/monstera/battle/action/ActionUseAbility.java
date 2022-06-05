package de.snx.monstera.battle.action;

import de.snx.monstera.battle.Battler;
import de.snx.monstera.battle.Battler.AbilityData;
import de.snx.monstera.state.BattleState;

public class ActionUseAbility extends BattleAction {

	private Battler attacker, target;
	private AbilityData ability;
	private boolean crit;
	private double effectiveness;
	private int total_damage;
	private double hp_animation, animTicks = .25;
	private boolean player, first;

	public ActionUseAbility(BattleState state, boolean player, AbilityData ability, boolean first) {
		super(state);
		this.ability = ability;
		this.first = first;
		this.player = player;
	}

	@Override
	public void prepare() {
		if (player) {
			attacker = state.getPlayer();
			target = state.getEnemy();
		} else {
			attacker = state.getEnemy();
			target = state.getPlayer();
		}
		this.total_damage = calculateDamage();
	}

	@Override
	public void keys() {
	}

	@Override
	public void update() {
		if (hp_animation < total_damage) {
			hp_animation += animTicks;
			target.setGraphicHPMod(-hp_animation);
		} else {
			target.setDamage(total_damage);
			target.setGraphicHPMod(0);
			if (target.getHp() == 0) {
				state.clearAction();
				state.setNextAction(new ActionDefeatMonster(state, target));
			}
			if (effectiveness == 0)
				state.setNextAction(new ActionShowText(state, ability.getName() + " has no effect."));
			else if (effectiveness > 1)
				state.setNextAction(new ActionShowText(state, "It is super effective"));
			else if (effectiveness < 1)
				state.setNextAction(new ActionShowText(state, "It is not effective"));
			if (crit)
				state.setNextAction(new ActionShowText(state, "A critical Hit!"));
			finished = true;
		}
	}

	public int calculateDamage() {
		double dmg = ability.getPower() / 100d * attacker.getAtk() * 9;
		dmg -= target.getDef() / 2;
		crit = Math.random() < .25;
		if (crit)
			dmg *= 2;
		effectiveness = target.getEffectiveLevel(ability.getType());
		dmg *= effectiveness;
		ability.useAP();
		return (int) (dmg <= 0 ? 1 : dmg);
	}

}
