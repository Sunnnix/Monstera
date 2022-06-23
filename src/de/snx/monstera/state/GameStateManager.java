package de.snx.monstera.state;

import java.awt.Graphics2D;
import java.util.NoSuchElementException;

import de.snx.monstera.Game;

/**
 * 
 * The GSM is responsible for the control of the game phases and is used e.g. to
 * get from the introduction of the game to the current gameplay and is
 * modularly expandable
 * 
 * @author Sunnix
 *
 */
public class GameStateManager {

	private Game game;
	private GameState[] states;
	private GameState currentState;

	public boolean drawDebug;

	public GameStateManager(Game game) {
		this.game = game;
	}

	/**
	 * Register all GameStates and start at the GameState with the given id
	 * 
	 * @param start  GameState id
	 * @param states states
	 */
	public void registerStates(int start, GameState... states) {
		this.states = states;
		setState(start);
	}

	/**
	 * Unloads the old state, set the new state and load the new state
	 * 
	 * @param state next state
	 * @param args  arguments as strings, which can be processed in the
	 *              {@link GameState}
	 */
	public void setState(GameState state, String... args) {
		String log = "Changed State ";
		if (this.currentState != null) {
			log += "from " + currentState.ID + " ";
			this.currentState.unload();
		}
		log += "to " + state.ID;
		this.currentState = state;
		this.currentState.load(args);
		System.out.println(log);
	}

	/**
	 * Unloads the old state, set the new state and load the new state
	 * 
	 * @param id   id of next state
	 * @param args arguments as strings, which can be processed in the
	 *             {@link GameState}
	 * @throws NoSuchElementException if the id don't match any GameState
	 */
	public void setState(int id, String... args) {
		for (GameState state : this.states)
			if (state.ID == id) {
				setState(state, args);
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
