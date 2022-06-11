package de.snx.monstera.creator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.filechooser.FileFilter;

import de.snx.monstera.battle.Ability;
import de.snx.monstera.battle.MonsterType;
import de.snx.monstera.global_data.CombatGroups;
import de.snx.monstera.global_data.ResourceStrings;
import de.snx.monstera.map.Entity;
import de.snx.psf.PSFFileIO;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
public class MapViewPanel extends JPanel {

	// TODO copy paste events
	// TODO (WIP 2/3) more draw options like (fill, square, circle, etc ) in new
	// MenuBar
	// TODO add to popup default Entitys like (teleport, battle, etc)
	// TODO (WIP 1/2) add possibility to change the appearance of the editor
	// TODO set config for above point
	// TODO export resources
	// TODO to point above create game file and combine all res with data
	// TODO deactivate all Menus when no map is loaded

	private CreatorWindow win;

	@Getter
	@Setter
	private Color c_grid = Color.CYAN, c_blocking = Color.RED, c_numbers = Color.RED;

	public ArrayList<Map> maps = new ArrayList<>();
	public Map selectedMap;

	// Spawnpoint
	private int pMapID = -1;
	public Entity player = new Entity(0);

	@Getter
	@Setter
	private int selectedLayer = 0;
	@Getter
	private Mode mode = Mode.DRAW_TILES;
	@Getter
	@Setter
	private Shape shape = Shape.SINGLE;
	@Getter
	private boolean drawGrid = true;
	private Entity selected, dragged;

	public enum Mode {
		DRAW_TILES, SET_BLOCKING, ENTITY
	}

	public enum Shape {
		SINGLE, RECT, CIRCLE, FILL
	}

	public MapViewPanel(CreatorWindow win) {
		this.win = win;
		player.name = "Player";
		setBackground(Color.BLACK);
		MListener mL = new MListener();
		addMouseListener(mL);
		addMouseMotionListener(mL);
		addKeyListener(new KeyListener());
		setFocusable(true);
	}

	public void setMapSize(int width, int height) {
		selectedMap.setSize(width, height);
		setPreferredSize(new Dimension(width * TilesetPanel.TILESIZE, height * TilesetPanel.TILESIZE));
		revalidate();
		win.repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (selectedMap == null)
			return;
		selectedMap.paint(g, this, win);
	}

	public void selectLayer(int i) {
		selectedLayer = i;
		win.info.setLayer(i);
	}

	public void openSizeDialog() {
		if (selectedMap == null) {
			JOptionPane.showMessageDialog(win, "Currently no Map loaded!");
			return;
		}
		try {
			int width = Integer.parseInt(JOptionPane.showInputDialog(win, "Width:", selectedMap.width));
			int height = Integer.parseInt(JOptionPane.showInputDialog(win, "Height:", selectedMap.height));
			setMapSize(width, height);
		} catch (NumberFormatException e) {
			System.err.println("invalid input.");
		}
	}

	public void newMap() {
		ArrayList<Integer> ids = new ArrayList<>();
		maps.forEach(map -> ids.add(map.ID));
		int id = 0;
		ids.sort(Comparator.naturalOrder());
		for (int in : ids)
			if (id == in)
				id++;
			else
				break;
		try {
			String name = JOptionPane.showInputDialog("Mapname:");
			int width = Integer
					.parseInt(JOptionPane.showInputDialog(win, "Width:", selectedMap != null ? selectedMap.width : 20));
			int height = Integer.parseInt(
					JOptionPane.showInputDialog(win, "Height:", selectedMap != null ? selectedMap.height : 20));
			Map map = new Map(id);
			map.name = name;
			map.setSize(width, height);
			maps.add(map);
			selectMap(id);
		} catch (NumberFormatException e) {
			System.err.println("invalid input.");
		}
	}

	public void deleteMap() {
		int a = JOptionPane.showConfirmDialog(win, "Delete current Map?");
		if (a == JOptionPane.OK_OPTION) {
			maps.remove(selectedMap);
			if (maps.size() > 0)
				selectedMap = maps.get(0);
			else
				selectedMap = null;
			revalidate();
			win.repaint();
		}
	}

	public void selectMapDialog() {
		if (maps.size() == 0)
			JOptionPane.showMessageDialog(win, "There is no map to select");
		else {
			Object s = JOptionPane.showInputDialog(win, "Select map", null, JOptionPane.QUESTION_MESSAGE, null,
					maps.toArray(), selectedMap);
			if (s != null)
				selectMap(((Map) s).ID);
		}
	}

	public void selectMap(int id) {
		for (Map map : maps)
			if (map.ID == id) {
				selectedMap = map;
				break;
			}
		if (selectedMap == null)
			setPreferredSize(new Dimension(0, 0));
		else
			setPreferredSize(new Dimension(selectedMap.width * TilesetPanel.TILESIZE,
					selectedMap.height * TilesetPanel.TILESIZE));
		revalidate();
		win.repaint();
	}

	public void openProject() {
		JFileChooser chooser = new JFileChooser("Creator/output");
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
		chooser.showOpenDialog(win);
		File projectF = chooser.getSelectedFile();
		if (projectF == null)
			return;
		maps.clear();
		int[] map_ids = new int[0];
		try (PSFFileIO file = new PSFFileIO(projectF, "r")) {
			map_ids = file.readIntArray("maps");
			file.room("player", _s -> {
				pMapID = file.readInt("map_id");
				player.setPos(file.readDouble("x"), file.readDouble("y"));
				player.setDirection(file.readInt("direction"));
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		String folder = projectF.getAbsolutePath().replace(".mgame", "");
		try (PSFFileIO file = new PSFFileIO(new File(folder, "/abilities.dat"), "r")) {
			Ability.loadAll(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try (PSFFileIO file = new PSFFileIO(new File(folder, "/monsters.dat"), "r")) {
			MonsterType.loadAll(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try (PSFFileIO file = new PSFFileIO(new File(folder, "/groups.dat"), "r")) {
			CombatGroups.loadAll(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int map : map_ids) {
			try (PSFFileIO file = new PSFFileIO(new File(folder, "/map" + map + ".dat"), "r")) {
				Map tmp = new Map(file, win.tileset);
				if (map == pMapID)
					tmp.entitys.add(0, player);
				maps.add(tmp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!maps.isEmpty())
			selectMap(maps.get(0).ID);
	}

	public void saveProject() {
		if (pMapID == -1 || !maps.stream().anyMatch(m -> m.ID == pMapID)) {
			JOptionPane.showMessageDialog(win, "The player is not set!");
			return;
		}
		String name = JOptionPane.showInputDialog(win, "Project name:");
		if (name == null || name.isEmpty())
			return;
		try (PSFFileIO file = new PSFFileIO(ResourceStrings.CRT_BASIC_PATH + name + ".mgame", "w")) {
			file.write("maps", (ArrayList<?>) maps.stream().map(m -> m.ID).collect(Collectors.toList()));
			file.room("player", _s -> {
				file.write("map_id", pMapID);
				file.write("x", player.getX());
				file.write("y", player.getY());
				file.write("direction", player.getDirection());
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		try (PSFFileIO file = new PSFFileIO(ResourceStrings.CRT_BASIC_PATH + name + "/abilities.dat", "w")) {
			Ability.saveAll(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try (PSFFileIO file = new PSFFileIO(ResourceStrings.CRT_BASIC_PATH + name + "/monsters.dat", "w")) {
			MonsterType.saveAll(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try (PSFFileIO file = new PSFFileIO(ResourceStrings.CRT_BASIC_PATH + name + "/groups.dat", "w")) {
			CombatGroups.saveAll(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(ResourceStrings.CRT_BASIC_PATH + name + "/map" + 1 + ".dat");
		for (Map map : maps) {
			try (PSFFileIO file = new PSFFileIO(ResourceStrings.CRT_BASIC_PATH + name + "/map" + map.ID + ".dat",
					"w")) {
				map.save(file, win.tileset);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setMode(Mode mode) {
		this.mode = mode;
		switch (mode) {
		case DRAW_TILES:
			win.info.setMode("Draw Tiles");
			break;
		case SET_BLOCKING:
			win.info.setMode("Set Blocking");
			break;
		case ENTITY:
			win.info.setMode("Entity");
			break;
		default:
			win.info.setMode("");
			break;
		}
		win.repaint();
	}

	private Entity newEntity(int id) {
		if (id == -1) {
			id = 1;
			for (Entity entity : selectedMap.entitys)
				if (entity.id >= id)
					id = entity.id + 1;
		} else
			for (Entity entity : selectedMap.entitys)
				if (entity.id == id) {
					System.err.println("The entity with the ID " + id + " is already reserved.");
					return null;
				}
		Entity e = new Entity(id);
		selectedMap.entitys.add(e);
		return e;
	}

	private void openEntityEditor(Entity entity) {
		new EntityEditor(win, entity);
	}

	public void setPlayerStart(int x, int y, int mapID) {
		if (mapID >= 0) {
			maps.forEach(m -> {
				m.entitys.remove(player);
				if (m.ID == mapID) {
					m.entitys.add(player);
					player.setPos(x, y);
					pMapID = mapID;
				}
			});
		}
		win.repaint();
	}

	private Entity createNewEntity(int x, int y) {
		Entity e = newEntity(-1);
		e.setPos(x, y);
		selectedMap.entitys.add(e);
		win.repaint();
		return e;
	}

	private void fillTile(int x, int y, int[] selected, int[] toFill) {
		if (tileEquals(selected, toFill))
			return;
		if (x < 0 || y < 0 || x >= selectedMap.width || y >= selectedMap.height)
			return;
		int[] tile = getTile(x, y);
		if (toFill[1] == -2) {
			toFill[0] = tile[0];
			toFill[1] = tile[1];
		}
		if (tileEquals(tile, toFill)) {
			setTile(x, y, selected);
			fillTile(x + 1, y, selected, toFill);
			fillTile(x - 1, y, selected, toFill);
			fillTile(x, y + 1, selected, toFill);
			fillTile(x, y - 1, selected, toFill);
		}
	}

	private void setTilePrev(int x, int y, int x2, int y2, int[] id) {
		if (x > x2) {
			int tmp = x;
			x = x2;
			x2 = tmp;
		}
		if (y > y2) {
			int tmp = y;
			y = y2;
			y2 = tmp;
		}
		if (shape == Shape.RECT) {
			for (int i = 0; i < selectedMap.width; i++)
				for (int j = 0; j < selectedMap.height; j++) {
					int[] prev = selectedMap.map[i][j].prev;
					if (i < x || j < y || i > x2 || j > y2)
						prev[1] = -2;
					else {
						prev[0] = id[0];
						prev[1] = id[1];
					}
				}
		} else {

		}
	}

	private void copyTileGraphic(int x, int y) {
		if (selectedMap.map[x][y].l1[1] != -1) {
			win.tileset.selected = getTile(x, y);
			win.tileset.repaint();
		}
	}

	private void setFromPrev() {
		for (int x = 0; x < selectedMap.width; x++)
			for (int y = 0; y < selectedMap.height; y++) {
				Tile tile = selectedMap.map[x][y];
				if (tile.prev[1] != -2) {
					int[] id = tile.prev.clone();
					tile.prev[1] = -2;
					setTile(x, y, id);
				}
			}
		repaint();
	}

	private void removeEntity(Entity e) {
		selectedMap.entitys.remove(e);
		repaint();
	}

	private int[] getTile(int x, int y) {
		int[] tmp = new int[] { -1, -1 };
		Tile tile = selectedMap.map[x][y];
		switch (selectedLayer) {
		case 0:
			tmp[0] = tile.l1[0];
			tmp[1] = tile.l1[1];
			break;
		case 1:
			tmp[0] = tile.l2[0];
			tmp[1] = tile.l2[1];
			break;
		case 2:
			tmp[0] = tile.l3[0];
			tmp[1] = tile.l3[1];
			break;
		default:
			break;
		}
		return tmp;
	}

	private void setTile(int x, int y, int[] id) {
		Tile tile = selectedMap.map[x][y];
		switch (selectedLayer) {
		case 0:
			tile.l1[0] = id[0];
			tile.l1[1] = id[1];
			break;
		case 1:
			tile.l2[0] = id[0];
			tile.l2[1] = id[1];
			break;
		case 2:
			tile.l3[0] = id[0];
			tile.l3[1] = id[1];
			break;
		default:
			break;
		}
	}

	private boolean tileEquals(int[] t1, int[] t2) {
		return t1[0] == t2[0] && t1[1] == t2[1];
	}

	private class MListener extends MouseAdapter {

		private int pX, pY, pressX, pressY;
		private int pressed = 0;

		@Override
		public void mousePressed(MouseEvent e) {
			if (selectedMap == null)
				return;
			int x, y;
			x = e.getX() / TilesetPanel.TILESIZE;
			y = e.getY() / TilesetPanel.TILESIZE;
			pX = x;
			pY = y;
			pressX = x;
			pressY = y;
			if (x >= selectedMap.width || y >= selectedMap.height)
				return;
			switch (e.getButton()) {
			case MouseEvent.BUTTON1:
				pressed = 1;
				switch (mode) {
				case DRAW_TILES:
					switch (shape) {
					case SINGLE:
						setTile(x, y, win.tileset.selected);
						break;
					case FILL:
						fillTile(x, y, win.tileset.selected, new int[] { -1, -2 });
						break;
					default:
						setTilePrev(x, y, x, y, win.tileset.selected);
						break;
					}
					repaint();
					break;
				case SET_BLOCKING:
					selectedMap.map[x][y].isBlocking = true;
					repaint();
					break;
				case ENTITY:
					dragged = null;
					for (Entity en : selectedMap.entitys)
						if (en.getX() == x && en.getY() == y) {
							selected = en;
							dragged = en;
							break;
						}
					if (e.getClickCount() == 2) {
						if (dragged == null) {
							dragged = createNewEntity(x, y);
						}
						if (dragged.id != 0)
							openEntityEditor(dragged);
						win.repaint();
					}
					break;
				default:
					break;
				}
				break;
			case MouseEvent.BUTTON2:
				pressed = 0;
				copyTileGraphic(x, y);
				break;
			case MouseEvent.BUTTON3:
				pressed = 2;
				switch (mode) {
				case DRAW_TILES:
					switch (shape) {
					case SINGLE:
						setTile(x, y, new int[] { -1, -1 });
						break;
					case FILL:
						fillTile(x, y, new int[] { -1, -1 }, new int[] { -1, -2 });
						break;
					default:
						setTilePrev(x, y, x, y, new int[] { -1, -1 });
						break;
					}
					repaint();
					break;
				case SET_BLOCKING:
					selectedMap.map[x][y].isBlocking = false;
					repaint();
					break;
				case ENTITY:
					selected = null;
					for (Entity en : selectedMap.entitys)
						if (en.getX() == x && en.getY() == y) {
							selected = en;
							break;
						}
					new EntityPopUp(selected, x, y).show(MapViewPanel.this, e.getX(), e.getY());
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (selectedMap == null)
				return;
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (pressed == 1)
					pressed = 0;
				setFromPrev();
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				if (pressed == 2)
					pressed = 0;
				setFromPrev();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (selectedMap == null)
				return;
			int x, y;
			x = e.getX() / TilesetPanel.TILESIZE;
			y = e.getY() / TilesetPanel.TILESIZE;
			if (x == pX && y == pY)
				return;
			pX = x;
			pY = y;
			if (x >= selectedMap.width || x < 0 || y >= selectedMap.height || y < 0)
				return;
			if (pressed == 1) {
				switch (mode) {
				case DRAW_TILES:
					switch (shape) {
					case SINGLE:
						setTile(x, y, win.tileset.selected);
						break;
					case FILL:
						break;
					default:
						setTilePrev(pressX, pressY, x, y, win.tileset.selected);
						break;
					}
					repaint();
					break;
				case SET_BLOCKING:
					selectedMap.map[x][y].isBlocking = true;
					repaint();
					break;
				case ENTITY:
					if (dragged == null)
						break;
					dragged.setPos(x, y);
					repaint();
					break;
				default:
					break;
				}
			} else if (pressed == 2) {
				switch (mode) {
				case DRAW_TILES:
					switch (shape) {
					case SINGLE:
						setTile(x, y, new int[] { -1, -1 });
						break;
					case FILL:
						break;
					default:
						setTilePrev(pressX, pressY, x, y, new int[] { -1, -1 });
						break;
					}
					repaint();
					break;
				case SET_BLOCKING:
					selectedMap.map[x][y].isBlocking = false;
					repaint();
					break;
				default:
					break;
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			win.info.setPos(e.getX() / TilesetPanel.TILESIZE, e.getY() / TilesetPanel.TILESIZE);
		}

	}

	private class KeyListener extends KeyAdapter {

		public void keyPressed(KeyEvent k) {
			if (k.getKeyCode() == KeyEvent.VK_DELETE)
				if (mode == Mode.ENTITY && selected != null) {
					selectedMap.entitys.remove(selected);
					selected = null;
					win.repaint();
				}
		};

	}

	private class EntityPopUp extends JPopupMenu {

		public EntityPopUp(Entity e, int x, int y) {
			if (e != null) {
				add(new JLabel(e.toString()));
				add(new JSeparator());
				if (e.id != 0)
					add(createMenuItem("Edit", a -> new EntityEditor(win, e)));
				add(createMenuItem("Remove", a -> removeEntity(e)));
				add(new JSeparator());
			}
			add(createMenuItem("Add New", a -> openEntityEditor(createNewEntity(x, y))));
			add(createMenuItem("Set Player", a -> setPlayerStart(x, y, selectedMap.ID)));
		}

		private JMenuItem createMenuItem(String label, ActionListener a) {
			JMenuItem item = new JMenuItem(label);
			if (a != null)
				item.addActionListener(a);
			return item;
		}

	}

}
