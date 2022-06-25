package de.snx.monstera.data;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import de.snx.monsteracreator.Config;
import de.snx.monsteracreator.window.Window;
import de.snx.psf.PSFFileIO;
import lombok.Getter;

/**
 * Handles all Project properties and saving from the Resources
 */
public class ProjectHandler {

	@Getter
	private static Project project;
	@Getter
	private static Abilities abilities;
	@Getter
	private static Monsters monsters;
	@Getter
	private static BattleGroups groups;
	@Getter
	private static Tilesets tilesets;
	@Getter
	private static Maps maps;
	@Getter
	private static EntityImages entityImages;

	public static void newProject(Window window) {
		JFileChooser chooser = new JFileChooser(Config.projectsPath);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showOpenDialog(window) != JFileChooser.APPROVE_OPTION)
			return;
		Config.projectsPath = chooser.getSelectedFile().getAbsolutePath();
		Integer tilesize = (Integer) JOptionPane.showInputDialog(window, "Select Tilesize:", "Tilesize",
				JOptionPane.QUESTION_MESSAGE, null, new Integer[] { 24, 48, 64 }, 24);
		if (tilesize == null)
			return;
		String name = "";
		while (name.isEmpty()) {
			name = JOptionPane.showInputDialog(window, "Enter Project name:");
			if (name == null)
				return;
			if (name.isEmpty())
				JOptionPane.showMessageDialog(window, "Project name can't be empty!");
		}
		project = new Project(tilesize, name, chooser.getSelectedFile());
		new File(project.getResourcePath()).mkdirs();
		setUpResources();
		ResourceHandler.onNewProject(project);
		maps = new Maps();
		saveProject(window, false);
		window.menu.activateAll(true);
		window.menu.getHalfFPSMode().setSelected(project.isUseHalfFPSMode());
		window.loadAll();
		window.repaint();
	}

	public static void saveProject(Window window, boolean chooseFile) {
		File mGame;
		if (chooseFile) {
			JFileChooser chooser = new JFileChooser(Config.projectsPath);
			chooser.setFileFilter(new FileFilter() {

				@Override
				public String getDescription() {
					return "Monstera Game (.mgame)";
				}

				@Override
				public boolean accept(File f) {
					return f.isDirectory() || f.getName().endsWith(".mgame");
				}
			});
			if (chooser.showSaveDialog(window) != JFileChooser.APPROVE_OPTION)
				return;
			mGame = chooser.getSelectedFile();
		} else
			mGame = new File(project.getDirectory(), "/" + project.getName() + ".mgame");
		Config.projectsPath = mGame.getParent();
		try (PSFFileIO file = new PSFFileIO(mGame, "w")) {
			file.write("tilesize", project.getTilesize());
			file.write("scale", project.getScale());
			file.write("name", project.getName());
			file.write("low-fps", project.isUseHalfFPSMode());
			ResourceHandler.saveResources(project);
			maps.save(project);
			file.room("player", _s -> {
				file.write("map_id", maps.getPlayerMapStart());
				file.write("x", maps.getPlayer().getX());
				file.write("y", maps.getPlayer().getY());
			});
		} catch (Exception e) {
			JOptionPane.showMessageDialog(window, "Can't save project (" + e.getMessage() + ")");
			e.printStackTrace();
		}
	}

	/**
	 * For Creator
	 */
	public static void loadProject(Window window) {
		JFileChooser chooser = new JFileChooser(Config.projectsPath);
		chooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "Monstera Game (.mgame)";
			}

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".mgame");
			}
		});
		if (chooser.showOpenDialog(window) != JFileChooser.APPROVE_OPTION)
			return;
		Config.projectsPath = chooser.getSelectedFile().getParent();
		try (PSFFileIO file = new PSFFileIO(chooser.getSelectedFile(), "r")) {
			int tilesize = file.readInt("tilesize", 24);
			String name = file.readString("name", null);
			if (name == null) {
				String fName = chooser.getSelectedFile().getName();
				String proName = fName.substring(0, fName.lastIndexOf(".mgame"));
				name = JOptionPane.showInputDialog(window,
						"Looks like you are using the old Project format.\nWould you tell me the project name?",
						proName);
				if (name == null)
					name = proName;
			}
			project = new Project(tilesize, name, chooser.getSelectedFile().getParentFile());
			project.setScale(file.readDouble("scale", 1));
			project.setUseHalfFPSMode(file.readBoolean("low-fps", false));
			setUpResources();
			maps = new Maps();
			maps.load(project);
			file.room("player", _s -> {
				maps.setPlayerStart(file.readInt("map_id", -1), (int) file.readDouble("x"), (int) file.readDouble("y"));
			});
			entityImages = new EntityImages();
			entityImages.load(project);
			ResourceHandler.loadResources(project);
			window.menu.activateAll(true);
			window.menu.getHalfFPSMode().setSelected(project.isUseHalfFPSMode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		window.loadAll();
		window.repaint();
	}

	/**
	 * For Game
	 * 
	 * @param projectArgs the project path and name, if the user starts this game
	 *                    from the creator, otherwise null
	 */
	public static boolean loadProject(JFrame frame, String[] projectArgs) {
		File projectFile;
		if (projectArgs == null) {
			JFileChooser chooser = new JFileChooser(Config.projectsPath);
			chooser.setFileFilter(new FileFilter() {

				@Override
				public String getDescription() {
					return "Monstera Game (.mgame)";
				}

				@Override
				public boolean accept(File f) {
					return f.isDirectory() || f.getName().endsWith(".mgame");
				}
			});
			if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION)
				return false;
			projectFile = chooser.getSelectedFile();
			Config.projectsPath = projectFile.getParent();
		} else {
			projectFile = new File(projectArgs[0], projectArgs[1] + ".mgame");
		}
		try (PSFFileIO file = new PSFFileIO(projectFile, "r")) {
			int tilesize = file.readInt("tilesize", 24);
			String name = file.readString("name");
			project = new Project(tilesize, name, projectFile.getParentFile());
			project.setScale(file.readDouble("scale", 1));
			project.setUseHalfFPSMode(file.readBoolean("low-fps", false));
			setUpResources();
			maps = new Maps();
			maps.load(project);
			file.room("player", _s -> {
				maps.setPlayerStart(file.readInt("map_id", -1), (int) file.readDouble("x"), (int) file.readDouble("y"));
			});
			entityImages = new EntityImages();
			entityImages.load(project);
			ResourceHandler.loadResources(project);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static void setUpResources() {
		ResourceHandler.clear();
		ResourceHandler.addResource(abilities = new Abilities());
		ResourceHandler.addResource(monsters = new Monsters());
		ResourceHandler.addResource(groups = new BattleGroups());
		ResourceHandler.addResource(tilesets = new Tilesets());
	}

	public static boolean isProjectLoaded() {
		return project != null;
	}

}
