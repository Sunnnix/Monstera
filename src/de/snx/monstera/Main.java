package de.snx.monstera;

import de.snx.monstera.event.BattleEvent;
import de.snx.monstera.event.MoveEvent;
import de.snx.monstera.event.TeleportEvent;
import de.snx.monstera.event.TextEvent;
import de.snx.monstera.event.TransferPlayerEvent;
import de.snx.monstera.global_data.EventRegistry;
import de.snx.monsteracreator.Creator;

public class Main {

	public static void main(String[] args) {
		registerAll();
		if (args.length > 0 && args[0].equals("creator"))
			new Creator();
		else
			new Game();
	}

	/**
	 * All events should be registered here
	 */
	public static void registerAll() {
		EventRegistry.registerEvent(TextEvent.class);
		EventRegistry.registerEvent(TeleportEvent.class);
		EventRegistry.registerEvent(TransferPlayerEvent.class);
		EventRegistry.registerEvent(BattleEvent.class);
		EventRegistry.registerEvent(MoveEvent.class);
	}

}
