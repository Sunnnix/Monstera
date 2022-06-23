package de.snx.monstera.state;

import java.awt.Color;

import de.snx.monstera.global_data.Keys;

/**
 * Currently there is no intro and this GameState is skipped after little time
 * 
 * @author Sunnix
 *
 */
public class IntroState extends GameState {

	private int timer = 10; // Time to next state

	public IntroState(int id) {
		super(id);
		setBackgroundColor(Color.RED);
	}

	@Override
	protected void keyEvents(GameStateManager gsm) {
		if (Keys.UP.isPressed())
			System.out.println("Pressed");
		if (Keys.UP.isPressedAlt())
			System.out.println("Pressed Alt");
	}

	@Override
	protected void update(GameStateManager gsm, int ticks) {
		timer--;
		if (timer == 0)
			gsm.setState(1);
	}

}
