package de.snx.monstera.battle.action;

import java.awt.Graphics2D;

import de.snx.monstera.state.GameStateManager;

public interface IDrawable {

	public void render(GameStateManager gsm, Graphics2D g);

}
