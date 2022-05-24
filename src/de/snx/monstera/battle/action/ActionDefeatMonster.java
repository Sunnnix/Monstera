package de.snx.monstera.battle.action;

import de.snx.monstera.battle.Battler;
import de.snx.monstera.state.BattleState;

public class ActionDefeatMonster extends BattleAction {

	private Battler target;
	private boolean isEnemy;
	private int xp;
	private int timer, max = 60;

	public ActionDefeatMonster(BattleState state, Battler target) {
		super(state);
		this.target = target;
		isEnemy = state.getEnemy().equals(target);
	}

	@Override
	public void keys() {
	}

	@Override
	public void update() {
		if (timer < max / 2) {
			if (isEnemy)
				state.setEOffsetY(20 * timer);
			else
				state.setPOffsetY(20 * timer);
			timer++;
		} else {
			state.addNextAction(new ActionShowText(state, target.getName() + " was defeated!"));
			if (isEnemy) {
				state.setShowEnemy(false);
				state.setEOffsetY(0);
				xp = target.getXpDrop();
				state.addNextAction(new ActionShowText(state, "Gained " + xp + " XP"));
				state.addNextAction(new ActionGainXP(state, xp));
			} else {
				state.setShowPlayer(false);
				state.setPOffsetY(0);
				state.addNextAction(new ActionShowText(state, "Player is out of Monster!", "Player Blacked out!"));
			}
			finished = true;
		}
	}

}
