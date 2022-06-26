package de.snx.monstera.data.mapdata;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import de.snx.monstera.data.IDSorted;
import de.snx.monstera.data.IResource;
import de.snx.monstera.data.IValueID;
import de.snx.monstera.data.Project;
import de.snx.monstera.data.ProjectHandler;
import de.snx.monstera.event.Event;
import de.snx.monstera.state.GameStateManager;
import de.snx.monstera.state.WorldState;
import de.snx.monsteracreator.Config;
import de.snx.monsteracreator.window.MapViewPanel;
import de.snx.monsteracreator.window.MapViewPanel.Mode;
import de.snx.monsteracreator.window.Window;
import de.snx.psf.PSFFileIO;
import lombok.Getter;
import lombok.Setter;

public class Map extends IDSorted<Entity> implements IResource, IValueID {

	private static final Color FRAME = new Color(255, 255, 255, 100);
	private static final Font FONT = new Font("Arial", Font.BOLD, 12);

	private int id;
	@Getter
	@Setter
	private String name = "";
	@Getter
	private int width, height;
	private Tile[][] tiles = new Tile[0][0];

	public Map(int id) {
		this.id = id;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}

	@Override
	public void save(Project project, PSFFileIO file) {
		file.write("id", id);
		file.write("name", name);
		try {
			file.write("size", new int[] { width, height });
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				final Tile tile = tiles[x][y];
				file.room("tile_" + x + "_" + y, s -> {
					try {
						file.write("l1", tile.l1);
						file.write("l2", tile.l2);
						file.write("l3", tile.l3);
						file.write("blocking", tile.isBlocking);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
		file.room("entitys", s -> {
			Entity player = null;
			for (Entry<Integer, Entity> entity : values.entrySet())
				if (entity.getKey() == 0) {
					player = entity.getValue();
					break;
				}
			if (player != null)
				deleteValue(player);
			file.write("size", values.size());
			List<Entity> entitys = new ArrayList<>(values.values());
			for (int i = 0; i < entitys.size(); i++) {
				int index = i;
				file.room("e" + i, st -> {
					try {
						entitys.get(index).save(file);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
			}
			if (player != null)
				putValue(player);
		});
	}

	@Override
	public void load(Project project, PSFFileIO file) {
		id = file.readInt("id");
		name = file.readString("name");
		try {
			int[] size = file.readIntArray("size");
			width = size[0];
			height = size[1];
			tiles = new Tile[width][height];
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				final Tile tile = new Tile(x, y);
				tiles[x][y] = tile;
				file.room("tile_" + x + "_" + y, _s -> {
					try {
						tile.l1 = file.readIntArray("l1");
						tile.l2 = file.readIntArray("l2");
						tile.l3 = file.readIntArray("l3");
						tile.isBlocking = file.readBoolean("blocking");
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
		file.room("entitys", s -> {
			int size = file.readInt("size");
			for (int i = 0; i < size; i++) {
				file.room("e" + i, st -> {
					try {
						Entity entity = new Entity(file);
						putValue(entity);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
			}
		});
	}

	@Override
	public String getPath() {
		return "Map" + id;
	}

	@Override
	public String getResourceName() {
		return "Map";
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		Tile[][] old = tiles;
		tiles = new Tile[width][height];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				if (x < old.length && y < old[0].length)
					tiles[x][y] = old[x][y];
				else
					tiles[x][y] = new Tile(x, y);
	}

	/**
	 * Paint from creator
	 */
	public void paint(Graphics g, MapViewPanel mapView, Window win) {
		int ts = ProjectHandler.getProject().getTilesize();
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				Tile tile = tiles[x][y];
				Image i1, i2, i3, prev;
				de.snx.monstera.data.Tilesets tilesets = ProjectHandler.getTilesets();
				i1 = tilesets.get(tile.l1);
				i2 = tilesets.get(tile.l2);
				i3 = tilesets.get(tile.l3);
				g.drawImage(i1, x * ts, y * ts, ts, ts, null);
				g.drawImage(i2, x * ts, y * ts, ts, ts, null);
				g.drawImage(i3, x * ts, y * ts, ts, ts, null);
				g.setColor(win.map.getBackground());
				if (tile.prev[1] == -1)
					g.fillRect(x * ts, y * ts, ts, ts);
				else {
					prev = tilesets.get(tile.prev);
					g.drawImage(prev, x * ts, y * ts, ts, ts, null);
				}
				switch (mapView.getMode()) {
				case DRAW_TILES:
					g.setColor(Config.getEditorColor(Config.C_MAP_VIEW_LAYER_NUM));
					g.setFont(new Font("Arial", Font.BOLD, 12));
					if (i1 != null)
						g.drawString("1", x * ts + 2, y * ts + 22);
					if (i2 != null)
						g.drawString("2", x * ts + 2 + 12, y * ts + 22);
					if (i3 != null)
						g.drawString("3", x * ts + 2, y * ts + 22 - 12);
					break;
				case SET_BLOCKING:
					if (tile.isBlocking) {
						Color c = Config.getEditorColor(Config.C_MAP_VIEW_BLOCKING);
						g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
						g.fillRect(x * ts, y * ts, ts, ts);
					}
					break;
				default:
					break;
				}
				if (mapView.isDrawGrid()) {
					g.setColor(Config.getEditorColor(Config.C_MAP_VIEW_GRID));
					g.drawRect(x * ts, y * ts, ts, ts);
				}
			}
		if (mapView.getMode() == Mode.ENTITY)
			values.forEach((k, e) -> {
				if (e.id == 0)
					g.setColor(Config.getEditorColor(Config.C_MAP_VIEW_P_BACK));
				else if (e.s_id >= 0)
					g.setColor(Config.getEditorColor(Config.C_MAP_VIEW_S_BACK));
				else
					g.setColor(Config.getEditorColor(Config.C_MAP_VIEW_E_BACK));
				g.fillRect((int) e.getX() * ts + 3, (int) e.getY() * ts + 3, ts - 6, ts - 6);
				if (e.id == 0)
					g.setColor(FRAME);
				else if (e.s_id >= 0)
					g.setColor(FRAME);
				else
					g.setColor(FRAME);
				g.drawRect((int) e.getX() * ts + 2, (int) e.getY() * ts + 2, ts - 4, ts - 4);
				g.setColor(Config.getEditorColor(e.id == 0 ? Config.C_MAP_VIEW_P_NUM : Config.C_MAP_VIEW_E_NUM));
				g.setFont(FONT);
				String id = e.id == 0 ? "P" : Integer.toString(e.id);
				g.drawString(id, (int) e.getX() * ts + ts / 2 - g.getFontMetrics().stringWidth(id) / 2,
						(int) e.getY() * ts + 16);
			});
	}

	public void newEntity(int id) {
		Entity entity = new Entity(id);
		putValue(entity);
	}

	public Entity createNewEntity(int x, int y) {
		Entity e = new Entity(getNextID());
		putValue(e);
		e.setPos(x, y);
		return e;
	}

	public Tile getTile(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return null;
		else
			return tiles[x][y];
	}

	public Entity getEntityFromPos(int x, int y) {
		for (Entity e : values.values())
			if (e.getX() == x && e.getY() == y)
				return e;
		return null;
	}

	public ArrayList<Entity> getEntitysAsList() {
		return new ArrayList<>(values.values());
	}

	@Override
	public String toString() {
		return (id < 10 ? "00" + id : id < 100 ? "0" + id : Integer.toString(id)) + " - " + name;
	}

	@Override
	public int getNextID() {
		Integer[] ids = values.keySet().stream().sorted(Comparator.naturalOrder()).toArray(Integer[]::new);
		int id = 1;
		for (int i : ids) {
			if (i == id)
				id++;
			else if (i > id)
				return id;
		}
		return id;
	}

	public void update(WorldState world, GameStateManager gsm) {
		values.forEach((k, e) -> e.update(world, this));
	}

	/**
	 * Render for Game
	 */
	public void render(WorldState state, GameStateManager gsm, Graphics2D g) {
		Project project = ProjectHandler.getProject();
		int ts = (int) (project.getTilesize() * project.getScale());
		int cX = state.getCameraX(), cY = state.getCameraY();
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				tiles[x][y].renderL1(g, x * ts, y * ts, cX, cY);
		values.forEach((k, e) -> e.render(g, cX, cY));
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				tiles[x][y].renderL2(g, x * ts, y * ts, cX, cY);
	}

	public Event getEntityEvent(Point pos, int trigger) {
		for (Entity entity : values.values()) {
			if (entity.id == 0 || entity.getEventTrigger() != trigger)
				continue;
			else if ((int) entity.getX() == pos.x && entity.getY() == pos.y) {
				Event e = entity.getEvent();
				if (e != null)
					return e.clone();
				else
					return null;
			}
		}
		return null;
	}

}
