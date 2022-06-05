package de.snx.monstera.battle.action;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;

import de.snx.monstera.battle.Battler;
import de.snx.monstera.global_data.Keys;
import de.snx.monstera.state.BattleState;
import de.snx.monstera.state.GameStateManager;

public class ActionForceMonsterChooser extends BattleAction implements IDrawable {

	private int pointer;
	private Battler[] battlers;

	public ActionForceMonsterChooser(BattleState state) {
		super(state);
	}

	@Override
	public void prepare() {
		battlers = state.getPlayerBattlers();
	}

	@Override
	public void keys() {
		if (Keys.DOWN.isPressed() && pointer < battlers.length - 1)
			pointer++;
		if (Keys.UP.isPressed() && pointer > 0)
			pointer--;
		if (Keys.CONFIRM.isPressed())
			switchMonster();
	}

	@Override
	public void update() {
	}

	@Override
	public void render(GameStateManager gsm, Graphics2D g) {
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
			if (i == pointer)
				g.fillPolygon(new Polygon(new int[] { 20, 40, 20 },
						new int[] { 20 + yInc * i, 30 + yInc * i, 40 + yInc * i }, 3));
			g.drawString("Lv:" + b.getLevel(), 50, 40 + yInc * i);
			g.drawString(b.getName(), 50 + 120, 40 + yInc * i);
			g.drawString("(" + b.getHp() + "/" + b.getMaxHP() + ")",
					gsm.windowWidth() - g.getFontMetrics().stringWidth("(" + b.getHp() + "/" + b.getMaxHP() + ")") - 20,
					40 + yInc * i);
		}
	}

	private void switchMonster() {
		Battler selected = battlers[pointer];
		if (selected.getHp() == 0) {
			state.addNextAction(new ActionShowText(state, "This Monster can't fight!"));
			state.addNextAction(new ActionForceMonsterChooser(state));
		} else {
			state.addNextAction(new ActionShowText(state, "Go " + state.getPlayer(pointer).getName() + "!"));
			state.addNextAction(new ActionUseNextMonster(state, true, pointer));
			state.addNextAction(new ActionPlayerChoose(state));
		}
		finished = true;
	}

}
