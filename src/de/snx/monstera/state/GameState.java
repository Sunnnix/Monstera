package de.snx.monstera.state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

import de.snx.monstera.global_data.Keys;

/**
 * 
 * Main process of the current event. <br>
 * Everything that the game should do in the runtime can be determined here.
 * 
 * @author Sunnix
 *
 */
public abstract class GameState {

	private static final Color DEBUG_BACKGROUND_COLOR = new Color(0, 0, 0, 100), DEBUG_TEXT_COLOR = Color.WHITE;
	private static final Font DEBUG_FONT = new Font("Arial", Font.BOLD, 12);

	public final int ID;

	private ArrayList<String> debugText = new ArrayList<>();
	private Color backgroundColor;

	public GameState(int id) {
		this.ID = id;
	}

	protected void unload() {

	}

	protected void load(String... args) {

	}

	protected final void _update(GameStateManager gsm, int ticks) {
		if (Keys.DEBUG.isPressed())
			gsm.drawDebug = !gsm.drawDebug;
		keyEvents(gsm);
		Keys.update();
		update(gsm, ticks);
		debugText.clear();
	}

	/**
	 * Here all Key events are called to work properly
	 * 
	 * @see Keys
	 */
	protected void keyEvents(GameStateManager gsm) {

	}

	protected void update(GameStateManager gsm, int ticks) {

	}

	protected final void _render(GameStateManager gsm, Graphics2D g, int fps) {
		addDebugText("FPS: " + fps);
		if (backgroundColor != null) {
			g.setColor(backgroundColor);
			g.fillRect(0, 0, gsm.windowWidth(), gsm.windowHeight());
		}
		render(gsm, g);
		if (gsm.drawDebug)
			drawDebug(g);
	}

	protected void render(GameStateManager gsm, Graphics2D g) {

	}

	/**
	 * Here text should be added in the
	 * {@link GameState#update(GameStateManager, int)} or
	 * {@link GameState#render(GameStateManager, Graphics2D)} to display it
	 * correctly in the debug menu
	 * 
	 * @param text text to show
	 */
	public void addDebugText(String text) {
		debugText.add(text);
	}

	private void drawDebug(Graphics2D g) {
		g.setColor(DEBUG_BACKGROUND_COLOR);
		g.setFont(DEBUG_FONT);
		int width = 0;
		for (String string : debugText) {
			int sWidth = g.getFontMetrics().stringWidth(string);
			if (sWidth > width)
				width = sWidth;
		}
		g.fillRect(0, 0, width + 4, 2 + 12 * debugText.size());
		g.setColor(DEBUG_TEXT_COLOR);
		for (int i = 0; i < debugText.size(); i++) {
			g.drawString(debugText.get(i), 2, 12 + i * 12);
		}
		debugText.clear();
	}

	public void setBackgroundColor(Color color) {
		backgroundColor = color;
	}

}
