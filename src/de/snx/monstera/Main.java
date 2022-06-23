package de.snx.monstera;

import de.snx.monstera.event.BattleEvent;
import de.snx.monstera.event.MoveEvent;
import de.snx.monstera.event.TeleportEvent;
import de.snx.monstera.event.TextEvent;
import de.snx.monstera.event.TransferPlayerEvent;
import de.snx.monstera.global_data.Registry;
import de.snx.monsteracreator.Creator;

public class Main {

	public static void main(String[] args) {
		registerAll();
		if (args.length > 0 && args[0].equals("creator"))
			new Creator();
		else
			new Game();
	}

	public static void registerAll() {
		Registry.registerEvent(TextEvent.class);
		Registry.registerEvent(TeleportEvent.class);
		Registry.registerEvent(TransferPlayerEvent.class);
		Registry.registerEvent(BattleEvent.class);
		Registry.registerEvent(MoveEvent.class);
	}

}
