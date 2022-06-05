package de.snx.monstera.battle.action;

import de.snx.monstera.state.BattleState;

/**
 * Animate Monster to the Field
 */
public class ActionUseNextMonster extends BattleAction {

	private int timer, timerMax = 30;
	private int offset;
	private boolean player;
	private int bIndex;

	public ActionUseNextMonster(BattleState state, boolean player, int bIndex) {
		super(state);
		this.player = player;
		this.bIndex = bIndex;
		offset = player ? -300 : 300;
	}

	@Override
	public void prepare() {
		if (bIndex == -1)
			offset = -offset;
		else {
			if (player) {
				state.setPlayerBattler(bIndex);
				state.setPOffsetX(offset);
			} else {
				state.setEnemyBattler(bIndex);
				state.setEOffsetX(offset);
			}
		}
		if (player) {
			state.setShowPlayer(true);
			state.setShowPlayerGUI(false);
		} else {
			state.setShowEnemy(true);
			state.setShowEnemyGUI(false);
		}
	}

	@Override
	public void keys() {

	}

	@Override
	public void update() {
		int pos = (int) ((bIndex == -1 ? 0 : offset) - offset * ((double) timer / timerMax));
		if (player)
			state.setPOffsetX(pos);
		else
			state.setEOffsetX(pos);
		if (timer == timerMax) {
			if (bIndex != -1)
				if (player)
					state.setShowPlayerGUI(true);
				else
					state.setShowEnemyGUI(true);
			finished = true;
		}
		timer++;
	}

}
