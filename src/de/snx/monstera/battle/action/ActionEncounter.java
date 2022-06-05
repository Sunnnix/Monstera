package de.snx.monstera.battle.action;

import de.snx.monstera.state.BattleState;

/**
 * First Animation to blend in player and enemy
 */
public class ActionEncounter extends BattleAction {

	private boolean first;
	private int timer, timerMax = 60;

	public ActionEncounter(BattleState state, boolean first) {
		super(state);
		this.first = first;
	}
	
	@Override
	public void prepare() {
	}

	@Override
	public void keys() {
	}

	@Override
	public void update() {
		if (!first) {
			finished = true;
			state.setShowPlayerGUI(true);
			state.addNextAction(new ActionPlayerChoose(state));
		} else if (timer < timerMax) {
			state.setEOffsetX((int) (-700 * (1 - (double) timer / timerMax)));
			state.setShowEnemy(true);
			state.setPOffsetX((int) (700 * (1 - (double) timer / timerMax)));
			state.setShowPlayer(true);
			timer++;
		} else {
			finished = true;
			state.setShowEnemyGUI(true);
			state.addNextAction(new ActionShowText(state, "A Wild " + state.getEnemy().getName() + " appeared!"));
			state.addNextAction(new ActionShowText(state, "Go " + state.getPlayer().getName() + "!"));
			state.addNextAction(new ActionEncounter(state, false));
		}
	}

}
