package de.snx.monstera.state;

import java.awt.Color;

/**
 * Currently there is no menu and this GameState is skipped after little time
 * 
 * @author Sunnix
 *
 */
public class MenuState extends GameState {

	private int timer = 10; // Time to next state

	public MenuState(int id) {
		super(id);
		setBackgroundColor(Color.BLUE);
	}

	@Override
	protected void update(GameStateManager gsm, int ticks) {
		timer--;
		if (timer == 0)
			gsm.setState(2);
	}

}
