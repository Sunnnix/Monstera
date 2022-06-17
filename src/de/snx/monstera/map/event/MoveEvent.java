package de.snx.monstera.map.event;

import java.awt.Graphics2D;

import de.snx.monstera.map.Map;
import de.snx.monstera.state.GameStateManager;
import de.snx.monstera.state.WorldState;
import de.snx.psf.PSFFileIO;

public class MoveEvent extends Event {

	public static final String REGISTRY_NAME = "Move NPC";

	private boolean blockActions;

	public MoveEvent() {
	}

	public MoveEvent(PSFFileIO file) {
	}

	@Override
	public boolean blockAction() {
		return blockActions;
	}

	@Override
	public void keyEvents(WorldState world, Map map) {
	}

	@Override
	public void update(Map map, WorldState world, GameStateManager gsm) {
		
	}

	@Override
	public void render(Graphics2D g, WorldState world, Map map) {
	}

	@Override
	public String getEventInfo() {
		return "";
	}

	@Override
	public void onSave(PSFFileIO file) throws Exception {
	}

	@Override
	public void interact(Map map) {
	}

}
