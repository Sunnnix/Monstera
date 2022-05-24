package de.snx.monstera.battle.action;

import de.snx.monstera.state.BattleState;
import lombok.Getter;

public abstract class BattleAction {

	@Getter
	protected boolean finished;
	protected BattleState state;

	public BattleAction(BattleState state) {
		this.state = state;
	}

	public abstract void keys();

	public abstract void update();

}
