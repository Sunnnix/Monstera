package de.snx.monsteracreator.window;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import de.snx.monstera.data.ProjectHandler;
import de.snx.monstera.data.mapdata.Entity;
import de.snx.monstera.data.mapdata.Map;
import de.snx.monstera.data.mapdata.Tile;
import de.snx.monsteracreator.Config;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
public class MapViewPanel extends JPanel {

	private Window win;

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

	public MapViewPanel(Window win) {
		this.win = win;
		MListener mL = new MListener();
		addMouseListener(mL);
		addMouseMotionListener(mL);
		addKeyListener(new KeyListener());
		setFocusable(true);
	}

	public void loadMap(Map map) {
		if (map == null)
			return;
		int ts = ProjectHandler.getProject().getTilesize();
		setPreferredSize(new Dimension(map.getWidth() * ts, map.getHeight() * ts));
		revalidate();
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		setBackground(Config.getEditorColor(Config.C_MAP_VIEW_BACKGROUND));
		super.paint(g);
		if (!ProjectHandler.isProjectLoaded())
			return;
		Map map = ProjectHandler.getMaps().getSelected();
		if (map == null)
			return;
		map.paint(g, this, win);
	}

	public void selectLayer(int i) {
		selectedLayer = i;
		win.info.setLayer(i);
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

	private void openEntityEditor(Entity entity) {
		new EntityEditor(win, entity);
	}

	private void fillTile(int x, int y, int[] selected, int[] toFill) {
		Map map = ProjectHandler.getMaps().getSelected();
		if (map == null)
			return;
		if (tileEquals(selected, toFill))
			return;
		if (x < 0 || y < 0 || x >= map.getWidth() || y >= map.getHeight())
			return;
		selected[2] = 1;
		selected[3] = 1;
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
		Map map = ProjectHandler.getMaps().getSelected();
		if (map == null)
			return;
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
			for (int i = 0; i < map.getWidth(); i++)
				for (int j = 0; j < map.getHeight(); j++) {
					int[] prev = map.getTile(i, j).prev;
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
		Map map = ProjectHandler.getMaps().getSelected();
		if (map == null)
			return;
		if (map.getTile(x, y).l1[1] != -1) {
			ProjectHandler.getTilesets().setSelected(getTile(x, y));
			win.tileset.repaint();
		}
	}

	private void setFromPrev() {
		Map map = ProjectHandler.getMaps().getSelected();
		if (map == null)
			return;
		for (int x = 0; x < map.getWidth(); x++)
			for (int y = 0; y < map.getHeight(); y++) {
				Tile tile = map.getTile(x, y);
				if (tile.prev[1] != -2) {
					int[] id = tile.prev.clone();
					tile.prev[1] = -2;
					setTile(x, y, new int[] { id[0], id[1], 1, 1 });
				}
			}
		repaint();
	}

	private void removeEntity(Entity e) {
		Map map = ProjectHandler.getMaps().getSelected();
		if (map == null)
			return;
		map.deleteValue(e.id);
		repaint();
	}

	private int[] getTile(int x, int y) {
		int[] tmp = new int[] { -1, -1, 1, 1 };
		Map map = ProjectHandler.getMaps().getSelected();
		if (map == null)
			return tmp;
		Tile tile = map.getTile(x, y);
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
		Map map = ProjectHandler.getMaps().getSelected();
		if (map == null)
			return;
		for (int tX = 0; tX < id[2] && x + tX < map.getWidth(); tX++)
			for (int tY = 0; tY < id[3] && y + tY < map.getHeight(); tY++) {
				Tile tile = map.getTile(x + tX, y + tY);
				switch (selectedLayer) {
				case 0:
					tile.l1[0] = id[0];
					tile.l1[1] = id[1] + tX + tY * ProjectHandler.getTilesets().getSWidth();
					break;
				case 1:
					tile.l2[0] = id[0];
					tile.l2[1] = id[1] + tX + tY * ProjectHandler.getTilesets().getSWidth();
					break;
				case 2:
					tile.l3[0] = id[0];
					tile.l3[1] = id[1] + tX + tY * ProjectHandler.getTilesets().getSWidth();
					break;
				default:
					break;
				}
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
			if (!ProjectHandler.isProjectLoaded())
				return;
			Map map = ProjectHandler.getMaps().getSelected();
			if (map == null)
				return;
			int x, y;
			int tilesize = ProjectHandler.getProject().getTilesize();
			x = e.getX() / tilesize;
			y = e.getY() / tilesize;
			pX = x;
			pY = y;
			pressX = x;
			pressY = y;
			if (x >= map.getWidth() || y >= map.getHeight())
				return;
			switch (e.getButton()) {
			case MouseEvent.BUTTON1:
				pressed = 1;
				switch (mode) {
				case DRAW_TILES:
					switch (shape) {
					case SINGLE:
						setTile(x, y, ProjectHandler.getTilesets().getSelected());
						break;
					case FILL:
						fillTile(x, y, ProjectHandler.getTilesets().getSelected(), new int[] { -1, -2, 1, 1 });
						break;
					default:
						setTilePrev(x, y, x, y, ProjectHandler.getTilesets().getSelected());
						break;
					}
					repaint();
					break;
				case SET_BLOCKING:
					map.getTile(x, y).isBlocking = true;
					repaint();
					break;
				case ENTITY:
					dragged = null;
					Entity en = map.getEntityFromPos(x, y);
					if (en != null) {
						selected = en;
						dragged = en;
					}
					if (e.getClickCount() == 2) {
						if (dragged == null) {
							dragged = map.createNewEntity(x, y);
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
						setTile(x, y, new int[] { -1, -1, 1, 1 });
						break;
					case FILL:
						fillTile(x, y, new int[] { -1, -1, 1, 1 }, new int[] { -1, -2, 1, 1 });
						break;
					default:
						setTilePrev(x, y, x, y, new int[] { -1, -1, 1, 1 });
						break;
					}
					repaint();
					break;
				case SET_BLOCKING:
					map.getTile(x, y).isBlocking = false;
					repaint();
					break;
				case ENTITY:
					selected = null;
					Entity en = map.getEntityFromPos(x, y);
					if (en != null)
						selected = en;
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
			if (!ProjectHandler.isProjectLoaded())
				return;
			Map map = ProjectHandler.getMaps().getSelected();
			if (map == null)
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
			if (!ProjectHandler.isProjectLoaded())
				return;
			Map map = ProjectHandler.getMaps().getSelected();
			if (map == null)
				return;
			int x, y;
			int tilesize = ProjectHandler.getProject().getTilesize();
			x = e.getX() / tilesize;
			y = e.getY() / tilesize;
			if (x == pX && y == pY)
				return;
			pX = x;
			pY = y;
			if (x >= map.getWidth() || x < 0 || y >= map.getHeight() || y < 0)
				return;
			if (pressed == 1) {
				switch (mode) {
				case DRAW_TILES:
					switch (shape) {
					case SINGLE:
						setTile(x, y, ProjectHandler.getTilesets().getSelected());
						break;
					case FILL:
						break;
					default:
						setTilePrev(pressX, pressY, x, y, ProjectHandler.getTilesets().getSelected());
						break;
					}
					repaint();
					break;
				case SET_BLOCKING:
					map.getTile(x, y).isBlocking = true;
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
						setTile(x, y, new int[] { -1, -1, 1, 1 });
						break;
					case FILL:
						break;
					default:
						setTilePrev(pressX, pressY, x, y, new int[] { -1, -1, 1, 1 });
						break;
					}
					repaint();
					break;
				case SET_BLOCKING:
					map.getTile(x, y).isBlocking = false;
					repaint();
					break;
				default:
					break;
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (!ProjectHandler.isProjectLoaded())
				return;
			int tilesize = ProjectHandler.getProject().getTilesize();
			win.info.setPos(e.getX() / tilesize, e.getY() / tilesize);
		}

	}

	private class KeyListener extends KeyAdapter {

		public void keyPressed(KeyEvent k) {
			if (!ProjectHandler.isProjectLoaded())
				return;
			Map map = ProjectHandler.getMaps().getSelected();
			if (map == null)
				return;
			if (k.getKeyCode() == KeyEvent.VK_DELETE)
				if (mode == Mode.ENTITY && selected != null) {
					map.deleteValue(selected);
					selected = null;
					win.repaint();
				}
		};

	}

	private class EntityPopUp extends JPopupMenu {

		public EntityPopUp(Entity e, int x, int y) {
			if (!ProjectHandler.isProjectLoaded())
				return;
			Map map = ProjectHandler.getMaps().getSelected();
			if (e != null) {
				add(new JLabel(e.toString()));
				add(new JSeparator());
				if (e.id != 0)
					add(createMenuItem("Edit", a -> new EntityEditor(win, e)));
				add(createMenuItem("Remove", a -> removeEntity(e)));
				add(new JSeparator());
			}
			add(createMenuItem("Add New", a -> openEntityEditor(map.createNewEntity(x, y))));
			add(createMenuItem("Set Player", a -> {
				ProjectHandler.getMaps().setPlayerStart(map.getID(), x, y);
				MapViewPanel.this.repaint();
			}));
		}

		private JMenuItem createMenuItem(String label, ActionListener a) {
			JMenuItem item = new JMenuItem(label);
			if (a != null)
				item.addActionListener(a);
			return item;
		}

	}

}
