package de.snx.monstera;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import de.snx.monstera.data.Project;
import de.snx.monstera.data.ProjectHandler;
import de.snx.monstera.event.BattleEvent;
import de.snx.monstera.event.MoveEvent;
import de.snx.monstera.event.TeleportEvent;
import de.snx.monstera.event.TextEvent;
import de.snx.monstera.event.TransferPlayerEvent;
import de.snx.monstera.global_data.EventRegistry;
import de.snx.monsteracreator.Creator;

public class Main {

	/**
	 * Game Process which is used to start the game from the Creator. This variable
	 * is used to be able to end the process.
	 */
	private static Process gameProcess;

	public static void main(String[] args) {
		registerAll();
		if (args.length > 0 && args[0].equals("creator"))
			new Creator();
		else
			new Game(args);
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

	/**
	 * Starts the Game as a new Process from the Data of the Project
	 * 
	 * @param window the Creator Window
	 */
	public static void startGameProcess(de.snx.monsteracreator.window.Window window) {
		try {
			Project project = ProjectHandler.getProject();
			if (project == null) {
				JOptionPane.showMessageDialog(window, "No Project loaded to start a Game on!");
				return;
			}
			ProjectHandler.saveProject(window, false);
			File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			String jar = jarFile.getName();
			if (!jarFile.exists() || !jar.endsWith(".jar")) {
				JOptionPane.showMessageDialog(window, "Can't find Game JAR (" + jar + ")!");
				return;
			}
			window.menu.activateGameStart(false);
			ArrayList<String> commands = new ArrayList<>();
			commands.add("java");
			commands.add("-jar");
			commands.add(jar);
			commands.add("start from creator");
			commands.add(project.getDirectory().getAbsolutePath());
			commands.add(project.getName());
			if (project.isUseHalfFPSMode())
				commands.add("low_fps");
			Process process = new ProcessBuilder(commands.toArray(new String[0])).start();
			gameProcess = process;
			new Thread(() -> {
				while (process.isAlive()) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						window.menu.activateGameStart(true);
						e.printStackTrace();
					}
				}
				window.menu.activateGameStart(true);
			}).start();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			window.menu.activateGameStart(true);
		}
	}

	public static void stopGameProcess(de.snx.monsteracreator.window.Window window) {
		gameProcess.destroy();
		window.menu.activateGameStart(true);
	}

}
