package de.snx.monstera.data;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import de.snx.monstera.data.mapdata.Entity;
import de.snx.monstera.data.mapdata.Map;
import de.snx.monstera.state.GameStateManager;
import de.snx.monstera.state.WorldState;
import de.snx.monstera.util.WindowUtils;
import de.snx.monsteracreator.window.Window;
import de.snx.psf.PSFFileIO;
import lombok.Getter;

public class Maps extends IDSorted<Map> {

	@Getter
	private Map selected;

	private int startMap = -1;
	@Getter
	private Entity player = new Entity(0);

	@Getter
	private int animTImer;

	public Maps() {
		player.name = "Player";
		player.setImageResource("player");
	}

	public void save(Project project) {
		File mapsFolder = new File(project.getDirectory(), "/" + project.getName() + "/maps");
		if (!mapsFolder.exists())
			mapsFolder.mkdir();
		values.forEach((id, map) -> {
			try (PSFFileIO file = new PSFFileIO(new File(mapsFolder, "/map" + id + ".dat"), "w")) {
				map.save(project, file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void load(Project project) {
		values.clear();
		File mapsDirectory = new File(project.getDirectory(), "/" + project.getName() + "/maps/");
		if (!mapsDirectory.exists())
			mapsDirectory.mkdirs();
		File[] mapFiles = mapsDirectory.listFiles(f -> f.getName().startsWith("map") && f.getName().endsWith(".dat"));
		for (File mapFile : mapFiles) {
			try (PSFFileIO file = new PSFFileIO(mapFile, "r")) {
				Map map = new Map(0);
				map.load(project, file);
				putValue(map);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (values.size() > 0)
			selected = (Map) values.values().toArray()[0];
	}

	public void newMapDialog(Window window) {
		new EditMapDialog(window, true);
	}

	public void showSwitchDialog(Window window) {
		if (values.isEmpty()) {
			JOptionPane.showMessageDialog(window, "There is no Map to switch to!", "Switch Map", JOptionPane.OK_OPTION);
			return;
		}
		Map map = (Map) JOptionPane.showInputDialog(window, "Select Map:", "Switch Map", JOptionPane.OK_CANCEL_OPTION,
				null, values.values().toArray(), selected);
		if (map == null)
			return;
		selected = map;
		window.map.loadMap(selected);
	}

	public void editMap(Window window) {
		if (selected == null) {
			JOptionPane.showMessageDialog(window, "No map is selected to edit!", "Edit Map", JOptionPane.OK_OPTION);
			return;
		}
		new EditMapDialog(window, false);
	}

	public void deleteCurrent(Window window) {
		if (selected == null) {
			JOptionPane.showMessageDialog(window, "No map is selected to delete!", "Delete Map", JOptionPane.OK_OPTION);
			return;
		}
		if (JOptionPane.showConfirmDialog(window,
				"Are you sure to delete the current map?\r\nThe map cannot be retrieved.", "Delete Map?",
				JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
			deleteValue(selected);
			if (values.isEmpty())
				selected = null;
			else
				selected = (Map) values.values().toArray()[0];
			window.map.loadMap(null);
		}
	}

	public void newEntity(int id) {
		if (selected == null)
			return;
		selected.newEntity(id);
	}

	public Entity createNewEntity(int x, int y) {
		if (selected == null)
			return null;
		return selected.createNewEntity(x, y);
	}

	public void setPlayerStart(int mapID, int x, int y) {
		values.forEach((key, map) -> map.deleteValue(player));
		startMap = mapID;
		player.setPos(x, y);
		if (mapID >= 0) {
			Map map = values.get(mapID);
			if (map != null)
				map.putValue(player);
		}
	}

	public Map[] getMapsAsArray() {
		return values.values().toArray(new Map[0]);
	}

	public int getPlayerMapStart() {
		return startMap;
	}

	public void transferPlayer(int mapID, int x, int y) {
		if (selected != null)
			selected.deleteValue(player);
		selected = values.get(mapID);
		player.setPos(x, y);
		selected.putValue(player);
	}

	public void update(WorldState worldState, GameStateManager gsm) {
		animTImer++;
		selected.update(worldState, gsm);
	}

	@SuppressWarnings("serial")
	private class EditMapDialog extends JDialog {

		private Window window;
		private boolean create;

		private JTextField id, name;
		private JSpinner width, height;

		public EditMapDialog(Window window, boolean create) {
			super(window, "Create new Map", ModalityType.APPLICATION_MODAL);
			this.window = window;
			this.create = create;
			setLayout(new BorderLayout());
			add(initValues(), BorderLayout.CENTER);
			add(initButtons(), BorderLayout.SOUTH);
			setResizable(false);
			pack();
			setLocationRelativeTo(window);
			setVisible(true);
		}

		private JPanel initValues() {
			JPanel panel = new JPanel(new GridLayout(0, 1));
			int tWidth = 50, cWidth = 80;
			panel.add(WindowUtils.createRow("ID:", id = new JTextField(String.valueOf(getNextID())), tWidth, cWidth));
			id.setEnabled(false);
			panel.add(WindowUtils.createRow("Name:", name = new JTextField(create ? "" : selected.getName()), tWidth,
					cWidth));
			panel.add(WindowUtils.createRow("Width:",
					width = new JSpinner(new SpinnerNumberModel(create ? 20 : selected.getWidth(), 16, 500, 1)), tWidth,
					cWidth));
			panel.add(WindowUtils.createRow("Height",
					height = new JSpinner(new SpinnerNumberModel(create ? 20 : selected.getHeight(), 14, 500, 1)),
					tWidth, cWidth));
			return panel;
		}

		private JPanel initButtons() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			JButton apply, cancel;
			panel.add(apply = new JButton(create ? "Create" : "Apply"));
			panel.add(cancel = new JButton("Cancel"));
			apply.addActionListener(a -> {
				Map map;
				if (create) {
					map = new Map(Integer.parseInt(id.getText()));
					map.setName(name.getText());
					map.setSize((int) width.getValue(), (int) height.getValue());
					putValue(map);
					selected = map;
				} else {
					map = selected;
					map.setName(name.getText());
					map.setSize((int) width.getValue(), (int) height.getValue());
				}
				window.map.loadMap(map);
				dispose();
			});
			cancel.addActionListener(a -> dispose());
			return panel;
		}

	}

}
