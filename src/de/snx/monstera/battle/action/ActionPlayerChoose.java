package de.snx.monstera.battle.action;

import de.snx.monstera.Keys;
import de.snx.monstera.battle.Battler.AbilityData;
import de.snx.monstera.state.BattleState;

public class ActionPlayerChoose extends BattleAction {

	private static final int MAIN_MENU = 0;
	private static final int ATTACK_MENU = 1;
	private static final int MONSTER_MENU = 2;
	private static final int ITEM_MENU = 3;

	private int menu = MAIN_MENU;
	private int mainPointer, attackPointer, monsterPointer, itemPointer;

	private AbilityData[] playerMoves, enemyMoves;

	public ActionPlayerChoose(BattleState state) {
		super(state);
		playerMoves = state.getPlayerMoves();
		enemyMoves = state.getEnemyMoves();
	}

	@Override
	public void keys() {
		switch (menu) {
		case MAIN_MENU:
			if (Keys.UP.isPressed() && mainPointer > 0)
				mainPointer--;
			if (Keys.DOWN.isPressed() && mainPointer < 3)
				mainPointer++;
			if (Keys.CONFIRM.isPressed())
				switch (mainPointer) {
				case 0:
					menu = ATTACK_MENU;
					break;
				case 1:
					menu = MONSTER_MENU;
					break;
				case 2:
					menu = ITEM_MENU;
					break;
				case 3:
					// TODO Run
					break;
				default:
					break;
				}
			break;
		case ATTACK_MENU:
			if (Keys.UP.isPressed() && attackPointer > 0)
				attackPointer--;
			if (Keys.DOWN.isPressed() && attackPointer < playerMoves.length - 1)
				attackPointer++;
			if (Keys.CONFIRM.isPressed())
				attack(attackPointer);
			if (Keys.CANCEL.isPressed())
				menu = MAIN_MENU;
			break;
		case MONSTER_MENU:
			if (Keys.CANCEL.isPressed())
				menu = MAIN_MENU;
			break;
		case ITEM_MENU:
			if (Keys.CANCEL.isPressed())
				menu = MAIN_MENU;
			break;
		default:
			break;
		}
	}

	@Override
	public void update() {
		String[] texts;
		switch (menu) {
		case MAIN_MENU:
			texts = new String[4];
			texts[0] = (mainPointer == 0 ? "►" : " ") + "Attack";
			texts[1] = (mainPointer == 1 ? "►" : " ") + "Monster";
			texts[2] = (mainPointer == 2 ? "►" : " ") + "Item";
			texts[3] = (mainPointer == 3 ? "►" : " ") + "Run";
			break;
		case ATTACK_MENU:
			texts = new String[playerMoves.length];
			for (int i = 0; i < playerMoves.length; i++)
				texts[i] = (attackPointer == i ? "►" : " ") + playerMoves[i].getAp() + " / " + playerMoves[i].getApMax()
						+ " | " + playerMoves[i].getName() + " (" + playerMoves[i].getType().name + ")";
			break;
		default:
			texts = new String[0];
			break;
		}
		state.setText(texts);
	}

	private void attack(int move) {
		finished = true;
		AbilityData pAb = playerMoves[move];
		AbilityData eAb = enemyMoves[(int) (Math.random() * enemyMoves.length)];
		state.addNextAction(new ActionShowText(state, state.getPlayer().getName() + " uses " + pAb.getName() + "!"));
		state.addNextAction(new ActionUseAbility(state, state.getPlayer(), state.getEnemy(), pAb, true));
		state.addNextAction(new ActionShowText(state, state.getEnemy().getName() + " uses " + eAb.getName() + "!"));
		state.addNextAction(new ActionUseAbility(state, state.getEnemy(), state.getPlayer(), eAb, false));
		state.addNextAction(new ActionPlayerChoose(state));
	}

}
