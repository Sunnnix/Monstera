package de.snx.monsteracreator;

import de.snx.monsteracreator.window.Window;

/**
 * Prepares everything for the creator and starts the CreatorWindow
 * 
 * @author Sunnix
 *
 */
public class Creator {

	private Window window;

	public Creator() {
		loadConfig();
		window = new Window(this);
	}

	private void loadConfig() {
		Config.load();
	}

}
