package de.snx.monstera.state;

import java.awt.Graphics2D;
import java.util.NoSuchElementException;

import de.snx.monstera.Game;

public class GameStateManager {

	private Game game;
	private GameState[] states;
	private GameState currentState;

	public GameStateManager(Game game) {
		this.game = game;
	}

	public void registerStates(int start, GameState... states) {
		this.states = states;
		setState(start);
	}

	public void setState(GameState state) {
		String log = "Changed State ";
		if (this.currentState != null) {
			log += "from " + currentState.ID + " ";
			this.currentState.unload();
		}
		log += "to " + state.ID;
		this.currentState = state;
		this.currentState.load();
		System.out.println(log);
	}

	public void setState(int id) {
		for (GameState state : this.states)
			if (state.ID == id) {
				setState(state);
				return;
			}
		throw new NoSuchElementException("There is no state with the ID " + id);
	}

	public void update(int ticks) {
		if (this.currentState == null)
			return;
		currentState._update(this, ticks);
	}

	public void render(Graphics2D g, int fps) {
		if (this.currentState == null)
			return;
		currentState._render(this, g, fps);
	}

	public int windowWidth() {
		return game.getScreenWidth();
	}

	public int windowHeight() {
		return game.getScreenHeight();
	}

}
