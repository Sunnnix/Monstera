package de.snx.monstera.state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.snx.monstera.Game;
import de.snx.monstera.Keys;
import de.snx.monstera.battle.Battler;
import de.snx.monstera.battle.Battler.AbilityData;
import de.snx.monstera.battle.action.ActionEncounter;
import de.snx.monstera.battle.action.BattleAction;
import lombok.Setter;

public class BattleState extends GameState {

	private Battler[] player = new Battler[6], enemy = new Battler[6];

	// Textbox
	private String[] text = new String[0];

	private ArrayList<BattleAction> action = new ArrayList<>();

	@Setter
	private boolean showPlayer, showEnemy, showPlayerGUI, showEnemyGUI;
	@Setter
	private int pOffsetX, pOffsetY, eOffsetX, eOffsetY;

	public BattleState(int id) {
		super(id);
		setBackgroundColor(Color.WHITE);
		player[0] = new Battler("Monster 1", 12);
	}

	@Override
	protected void load() {
		showPlayer = false;
		showEnemy = false;
		showPlayerGUI = false;
		showEnemyGUI = false;
		enemy[0] = new Battler("Monster 2", 3);
		text = new String[0];
		action.clear();
		action.add(new ActionEncounter(this, true));
	}

	@Override
	protected void render(GameStateManager gsm, Graphics2D g) {
		renderMonster(gsm, g);
		renderTextBox(gsm, g);
		renderGUI(gsm, g);
		addDebugText("Action: " + (action.isEmpty() ? "-" : action.get(0).getClass().getSimpleName()));
	}

	private void renderMonster(GameStateManager gsm, Graphics2D g) {
		BufferedImage b1 = enemy[0].getImage(0);
		BufferedImage b2 = player[0].getImage(1);
		int b1X, b1Y, b2X, b2Y;
		b1X = gsm.windowWidth() - 180 + eOffsetX;
		b2X = 130 + pOffsetX;
		b1Y = 130 + eOffsetY;
		b2Y = gsm.windowHeight() - 255 + pOffsetY;
		if (showEnemy)
			g.drawImage(b1, b1X - (int) (b1.getWidth() * Game.SCALE / 2), b1Y - (int) (b1.getHeight() * Game.SCALE / 2),
					(int) (b1.getWidth() * Game.SCALE), (int) (b1.getHeight() * Game.SCALE), null);
		if (showPlayer)
			g.drawImage(b2, b2X - (int) (b2.getWidth() * Game.SCALE / 2), b2Y - (int) (b2.getHeight() * Game.SCALE / 2),
					(int) (b2.getWidth() * Game.SCALE), (int) (b2.getHeight() * Game.SCALE), null);
	}

	private void renderTextBox(GameStateManager gsm, Graphics2D g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, gsm.windowHeight() - 160, gsm.windowWidth(), 160);
		g.setFont(new Font("Arial", Font.BOLD, 32));

		int x = 12;
		int y = gsm.windowHeight() - 160 + 38;
		int ySpace = 35;
		int pos = 0;
		g.setColor(Color.BLACK);
		for (int i = 0; i < text.length; i++) {
			g.drawString(text[i], x, y + ySpace * pos);
			pos++;
		}
	}

	private void renderGUI(GameStateManager gsm, Graphics2D g) {
		int x, y;
		float hp;
		Battler b;
		if (showEnemyGUI) {
			// enemy GUI
			x = 20;
			y = 20;
			g.setColor(Color.WHITE);
			g.fillRect(x, y, 300, 120);

			b = enemy[0];

			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.PLAIN, 32));
			g.drawString(b.getName(), x + 10, y + 10 + 15);
			g.setFont(new Font("Arial", Font.BOLD, 24));
			g.drawString(":L" + b.getLevel(), x + 150, y + 35 + 15);
			g.drawString("HP:", x + 10, y + 60 + 15);
			hp = (float) b.getDHP() / b.getMaxHP();
			g.setColor(hp > .5 ? Color.GREEN : hp > .2 ? Color.YELLOW : Color.RED);
			g.fillRoundRect(x + 55, y + 63, (int) (230 * hp), 10, 6, 6);
			g.setColor(Color.BLACK);
			g.drawRoundRect(x + 55, y + 63, 230, 10, 6, 6);
			g.fillRect(x, y + 50, 10, 35);
			g.fillOval(x, y + 80, 10, 10);
			g.fillRect(x + 7, y + 80, 290, 10);
			g.fillPolygon(new int[] { x + 290, x + 290, x + 320 }, new int[] { y + 70, y + 90, y + 90 }, 3);
		}
		if (showPlayerGUI) {
			// Player GUI
			x = gsm.windowWidth() - 20 - 300;
			y = gsm.windowHeight() - 20 - 120 - 160;
			g.setColor(Color.WHITE);
			g.fillRect(x, y, 300, 120);

			b = player[0];

			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.PLAIN, 32));
			g.drawString(b.getName(), x + 10, y + 10 + 15);
			g.setFont(new Font("Arial", Font.BOLD, 24));
			g.drawString(":L" + b.getLevel(), x + 150, y + 35 + 15);
			g.drawString("HP:", x + 10, y + 60 + 15);
			hp = (float) b.getDHP() / b.getMaxHP();
			g.setColor(hp > .5 ? Color.GREEN : hp > .2 ? Color.YELLOW : Color.RED);
			g.fillRoundRect(x + 55, y + 63, (int) (230 * hp), 10, 6, 6);
			g.setColor(Color.BLACK);
			g.drawRoundRect(x + 55, y + 63, 230, 10, 6, 6);
			String hpT = b.getHp() + "/";
			g.drawString(hpT, x + 240 - g.getFontMetrics().stringWidth(hpT), y + 95);
			String mhp = String.valueOf(b.getMaxHP());
			g.drawString(mhp, x + 280 - g.getFontMetrics().stringWidth(mhp), y + 95);
			g.setColor(Color.DARK_GRAY);
			g.fillRoundRect(x + 55, y + 103, 230, 10, 6, 6);
			float xp = (float) b.getDXP() / b.getXpToNextLv();
			g.setColor(Color.CYAN);
			g.fillRoundRect(x + 55, y + 103, (int) (230 * xp), 10, 6, 6);
			g.setColor(Color.BLACK);
			g.drawRoundRect(x + 55, y + 103, 230, 10, 6, 6);
			g.fillRect(x + 290, y + 50, 10, 72);
			g.fillOval(x + 290, y + 117, 10, 10);
			g.fillRect(x + 3, y + 117, 290, 10);
			g.fillPolygon(new int[] { x + 3, x + 3, x + 3 - 30 }, new int[] { y + 107, y + 127, y + 127 }, 3);
		}
	}

	@Override
	protected void update(GameStateManager gsm, int ticks) {
		if (action.isEmpty())
			gsm.setState(2);
		else {
			BattleAction action = this.action.get(0);
			action.update();
			if (action.isFinished())
				this.action.remove(0);
		}
	}

	@Override
	protected void keyEvents(GameStateManager gsm) {
		if (Keys.DEBUG.isPressed())
			drawDebug = !drawDebug;
		if (!action.isEmpty())
			action.get(0).keys();
	}

	public void setText(String[] text) {
		this.text = text;
	}

	public AbilityData[] getPlayerMoves() {
		return player[0].getAbilitys();
	}

	public AbilityData[] getEnemyMoves() {
		return enemy[0].getAbilitys();
	}

	public void setNextAction(BattleAction action) {
		this.action.add(1, action);
	}

	public void addNextAction(BattleAction action) {
		this.action.add(action);
	}

	public Battler getPlayer() {
		return player[0];
	}

	public Battler getEnemy() {
		return enemy[0];
	}

	public void removeNextAction() {
		if (action.size() > 1)
			action.remove(1);
	}

	public void clearAction() {
		if (action.isEmpty())
			return;
		BattleAction a = action.get(0);
		action.clear();
		action.add(a);
	}
}
