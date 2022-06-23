package de.snx.monstera.battle.action;

import de.snx.monstera.data.battle.Battler;
import de.snx.monstera.state.BattleState;

/**
 * Animate defeated Monster and set the next stages for gaining XP or choosing
 * next Monster for the player
 */
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
	public void prepare() {
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
				state.setShowEnemyGUI(false);
				state.setEOffsetY(0);
				xp = target.getXpDrop();
				state.addNextAction(new ActionShowText(state, "Gained " + xp + " XP"));
				state.addNextAction(new ActionGainXP(state, xp));
			} else {
				state.setShowPlayer(false);
				state.setShowPlayerGUI(false);
				state.setPOffsetY(0);
				if (state.hasNextMonster(true))
					state.addNextAction(new ActionForceMonsterChooser(state));
				else
					state.addNextAction(new ActionShowText(state, "Player is out of Monster!", "Player Blacked out!"));
			}
			finished = true;
		}
	}

}
