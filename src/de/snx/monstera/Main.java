package de.snx.monstera;

import de.snx.monstera.creator.CreatorWindow;
import de.snx.monstera.global_data.Registry;
import de.snx.monstera.map.event.BattleEvent;
import de.snx.monstera.map.event.MoveEvent;
import de.snx.monstera.map.event.TeleportEvent;
import de.snx.monstera.map.event.TextEvent;
import de.snx.monstera.map.event.TransferPlayerEvent;

public class Main {

	public static void main(String[] args) {
		if (args.length > 0 && "creator".equals(args[0]))
			new CreatorWindow();
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
