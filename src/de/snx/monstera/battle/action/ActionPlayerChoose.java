package de.snx.monstera.battle.action;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;

import de.snx.monstera.battle.Battler;
import de.snx.monstera.battle.Battler.AbilityData;
import de.snx.monstera.global_data.Keys;
import de.snx.monstera.state.BattleState;
import de.snx.monstera.state.GameStateManager;

public class ActionPlayerChoose extends BattleAction implements IDrawable {

	private static final int MAIN_MENU = 0;
	private static final int ATTACK_MENU = 1;
	private static final int MONSTER_MENU = 2;
	private static final int ITEM_MENU = 3;

	private int menu = MAIN_MENU;
	private int mainPointer, attackPointer, monsterPointer, itemPointer;

	private AbilityData[] playerMoves, enemyMoves;

	private Battler[] battlers;

	public ActionPlayerChoose(BattleState state) {
		super(state);
	}

	@Override
	public void prepare() {
		playerMoves = state.getPlayerMoves();
		enemyMoves = state.getEnemyMoves();
		battlers = state.getPlayerBattlers();
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
			if (Keys.DOWN.isPressed() && monsterPointer < battlers.length - 1)
				monsterPointer++;
			if (Keys.UP.isPressed() && monsterPointer > 0)
				monsterPointer--;
			if (Keys.CONFIRM.isPressed())
				switchMonster();
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
				texts[i] = (attackPointer == i ? "►" : " ") + playerMoves[i].getAp() + " / "
						+ playerMoves[i].getApMax() + " | " + playerMoves[i].getName() + " ("
						+ playerMoves[i].getType().name + ")";
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
		state.addNextAction(new ActionUseAbility(state, true, pAb, true));
		state.addNextAction(new ActionShowText(state, state.getEnemy().getName() + " uses " + eAb.getName() + "!"));
		state.addNextAction(new ActionUseAbility(state, false, eAb, false));
		state.addNextAction(new ActionPlayerChoose(state));
	}

	private void switchMonster() {
		Battler selected = battlers[monsterPointer];
		if (selected.getHp() == 0) {
			state.addNextAction(new ActionShowText(state, "This Monster can't fight!"));
		} else if (selected.equals(state.getPlayer())) {
			state.addNextAction(new ActionShowText(state, "This Monster is currently fighting!"));
		} else {
			state.addNextAction(new ActionShowText(state, "Come back " + state.getPlayer().getName()));
			state.addNextAction(new ActionUseNextMonster(state, true, -1));
			state.addNextAction(new ActionShowText(state, "Go " + state.getPlayer(monsterPointer).getName() + "!"));
			state.addNextAction(new ActionUseNextMonster(state, true, monsterPointer));
			AbilityData eAb = enemyMoves[(int) (Math.random() * enemyMoves.length)];
			state.addNextAction(new ActionShowText(state, state.getEnemy().getName() + " uses " + eAb.getName() + "!"));
			state.addNextAction(new ActionUseAbility(state, false, eAb, false));
		}
		state.addNextAction(new ActionPlayerChoose(state));
		finished = true;
	}

	@Override
	public void render(GameStateManager gsm, Graphics2D g) {
		switch (menu) {
		case MONSTER_MENU:
			g.setColor(Color.WHITE);
			g.fillRoundRect(0, 0, gsm.windowWidth(), gsm.windowHeight(), 10, 10);
			Stroke s = g.getStroke();
			g.setStroke(new BasicStroke(5));
			g.setColor(Color.BLACK);
			g.drawRoundRect(5, 5, gsm.windowWidth() - 10, gsm.windowHeight() - 10, 10, 10);
			g.setStroke(s);
			g.setFont(new Font("Arial", Font.BOLD, 32));
			int yInc = 30;
			for (int i = 0; i < battlers.length; i++) {
				Battler b = battlers[i];
				if (i == monsterPointer)
					g.fillPolygon(new Polygon(new int[] { 20, 40, 20 },
							new int[] { 20 + yInc * i, 30 + yInc * i, 40 + yInc * i }, 3));
				g.drawString("Lv:" + b.getLevel(), 50, 40 + yInc * i);
				g.drawString(b.getName(), 50 + 120, 40 + yInc * i);
				g.drawString(
						"(" + b.getHp() + "/" + b.getMaxHP() + ")", gsm.windowWidth()
								- g.getFontMetrics().stringWidth("(" + b.getHp() + "/" + b.getMaxHP() + ")") - 20,
						40 + yInc * i);
			}
			break;
		default:
			break;
		}
	}

}
