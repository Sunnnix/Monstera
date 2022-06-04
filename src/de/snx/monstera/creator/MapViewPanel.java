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
import java.util.HashSet;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.filechooser.FileFilter;

import de.snx.monstera.battle.Ability;
import de.snx.monstera.battle.monstertype.MonsterType;
import de.snx.monstera.global_data.CombatGroups;
import de.snx.monstera.map.Entity;
import de.snx.psf.PSFFileIO;
import lombok.Getter;

@SuppressWarnings("serial")
public class MapViewPanel extends JPanel {

	private CreatorWindow win;

	public ArrayList<Map> maps = new ArrayList<>();
	public Map selectedMap;

	// Spawnpoint
	private int pMapID = -1;
	public Entity player = new Entity(0);

	private int selectedLayer = 0;
	@Getter
	private Mode mode = Mode.DRAW_TILES;
	@Getter
	private boolean drawGrid = true;
	private Entity selected, dragged;

	public enum Mode {
		DRAW_TILES, SET_BLOCKING, ENTITY
	}

	public MapViewPanel(CreatorWindow win) {
		this.win = win;
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
		win.revalidate();
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
		maps.add(new Map(id));
		selectMap(id);
		openSizeDialog();
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
		win.revalidate();
		win.repaint();
	}

	public void openProject() {
		JFileChooser chooser = new JFileChooser("Creator/output");
		chooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "Map File (.map)";
			}

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".map");
			}
		});
		chooser.showOpenDialog(win);
		File mapData = chooser.getSelectedFile();
		if (mapData == null)
			return;
		maps.clear();
		try (PSFFileIO file = new PSFFileIO(mapData, "r")) {
			Ability.loadAll(file);
			MonsterType.loadAll(file);
			CombatGroups.loadAll(file);
			file.room("maps", _s -> {
				int size = file.readInt("size");
				for (int i = 0; i < size; i++) {
					file.room("m_" + i, __s -> {
						Map map = new Map(file, win.tileset);
						maps.add(map);
					});
					if (i == 0)
						selectMap(0);
				}
			});
			file.room("player", _s -> {
				pMapID = file.readInt("map_id");
				if (pMapID >= 0) {
					player.setPos(file.readDouble("x"), file.readDouble("y"));
					player.setDirection(file.readInt("direction"));
					setPlayerStart((int) player.getX(), (int) player.getY(), pMapID);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		win.revalidate();
		win.repaint();
	}

	public void saveProject() {
		if (pMapID == -1 || !maps.stream().anyMatch(m -> m.ID == pMapID)) {
			JOptionPane.showMessageDialog(win, "The player is not set!");
			return;
		}
		String name = JOptionPane.showInputDialog(win, "Project name:");
		if (name == null || name.isEmpty())
			return;
		HashSet<String> usedKeys = new HashSet<>();
		try (PSFFileIO file = new PSFFileIO("Creator/output/" + name + ".map", "w")) {
			Ability.saveAll(file);
			MonsterType.saveAll(file);
			CombatGroups.saveAll(file);
			file.room("player", _s -> {
				file.write("map_id", pMapID);
				file.write("x", player.getX());
				file.write("y", player.getY());
				file.write("direction", player.getDirection());
			});
			file.room("maps", _s -> {
				file.write("size", maps.size());
				for (int i = 0; i < maps.size(); i++) {
					Map map = maps.get(i);
					file.room("m_" + i, __s -> {
						map.save(file, usedKeys, win.tileset);
					});
				}
			});
			file.write("tile_res", usedKeys.toArray(new String[0]));
		} catch (Exception e) {
			e.printStackTrace();
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

	private void setTile(int x, int y, int id) {
		switch (selectedLayer) {
		case 0:
			selectedMap.map[x][y].l1 = id;
			break;
		case 1:
			selectedMap.map[x][y].l2 = id;
			break;
		case 2:
			selectedMap.map[x][y].l3 = id;
			break;
		default:
			break;
		}
		repaint();
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

	private class MListener extends MouseAdapter {

		private int pX, pY;
		private int pressed = 0;

		@Override
		public void mousePressed(MouseEvent e) {
			int x, y;
			x = e.getX() / TilesetPanel.TILESIZE;
			y = e.getY() / TilesetPanel.TILESIZE;
			pX = x;
			pY = y;
			if (x >= selectedMap.width || y >= selectedMap.height)
				return;
			if (e.getButton() == MouseEvent.BUTTON1) {
				pressed = 1;
				switch (mode) {
				case DRAW_TILES:
					setTile(x, y, win.tileset.selected);
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
						openEntityEditor(dragged);
						win.repaint();
					}
					break;
				default:
					break;
				}
			} else if (e.getButton() == MouseEvent.BUTTON2) {
				pressed = 0;
				switch (selectedLayer) {
				case 0:
					if (selectedMap.map[x][y].l1 != -1) {
						win.tileset.selected = selectedMap.map[x][y].l1;
						win.repaint();
					}
					break;
				case 1:
					if (selectedMap.map[x][y].l2 != -1) {
						win.tileset.selected = selectedMap.map[x][y].l2;
						win.repaint();
					}
					break;
				case 2:
					if (selectedMap.map[x][y].l3 != -1) {
						win.tileset.selected = selectedMap.map[x][y].l3;
						win.repaint();
					}
					break;
				default:
					break;
				}
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				pressed = 2;
				switch (mode) {
				case DRAW_TILES:
					setTile(x, y, -1);
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
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (pressed == 1)
					pressed = 0;
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				if (pressed == 2)
					pressed = 0;
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
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
					setTile(x, y, win.tileset.selected);
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
					setTile(x, y, -1);
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
				add(createMenuItem("Edit", a -> new EntityEditor(win, e)));
				add(createMenuItem("Remove", a -> selectedMap.entitys.remove(e)));
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
