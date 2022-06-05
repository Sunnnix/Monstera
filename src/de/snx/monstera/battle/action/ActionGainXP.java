package de.snx.monstera.battle.action;

import de.snx.monstera.battle.Battler;
import de.snx.monstera.global_data.Keys;
import de.snx.monstera.state.BattleState;

public class ActionGainXP extends BattleAction {

	private int wholeXP, partXP;
	private int timer, max = 45;

	private boolean waitForInput;

	public ActionGainXP(BattleState state, int xp) {
		super(state);
		this.wholeXP = xp;
	}

	@Override
	public void prepare() {

	}

	@Override
	public void keys() {
		if (waitForInput && Keys.CONFIRM.isPressed()) {
			if (state.hasNextMonster(false)) {
				state.nextEnemy();
				state.addNextAction(new ActionShowText(state, "Enemy sent out " + state.getEnemy().getName() + "!"));
				state.addNextAction(new ActionUseNextMonster(state, false, state.getEnemyIndex()));
				state.addNextAction(new ActionPlayerChoose(state));
			}
			finished = true;
		}
	}

	@Override
	public void update() {
		if (partXP == 0) {
			if (wholeXP <= 0) {
				waitForInput = true;
				return;
			}
			Battler p = state.getPlayer();
			int neededXP = p.getXpToNextLv() - p.getXp();
			if (neededXP >= wholeXP) {
				partXP = wholeXP;
				wholeXP = 0;
			} else {
				partXP = neededXP;
				wholeXP -= neededXP;
			}
		}
		if (timer < max) {
			double a = ((double) timer / max);
			state.getPlayer().setGraphicXPMod(partXP * a);
			timer++;
		} else {
			state.getPlayer().setGraphicXPMod(0);
			state.getPlayer().addXP(partXP, state);
			partXP = 0;
			timer = 0;
		}
	}

}
