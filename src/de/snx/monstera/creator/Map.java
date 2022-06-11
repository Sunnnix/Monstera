package de.snx.monstera.creator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import de.snx.monstera.creator.MapViewPanel.Mode;
import de.snx.monstera.map.Entity;
import de.snx.psf.PSFFileIO;

public class Map {

	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	private static final Color BLUR = new Color(100, 150, 200, 100);
	private static final Color FRAME = new Color(100, 200, 255, 255);
	private static final Color P_BLUR = new Color(160, 80, 200, 100);
	private static final Color P_FRAME = new Color(200, 80, 255, 255);
	private static final Color S_BLUR = new Color(80, 200, 80, 100);
	private static final Color S_FRAME = new Color(80, 255, 80, 255);
	private static final Font FONT = new Font("Arial", Font.BOLD, 12);

	public final int ID;
	public String name = "";

	public int width, height;
	public Tile[][] map = new Tile[0][0];
	public ArrayList<Entity> entitys = new ArrayList<>();

	public Map(int id) {
		this.ID = id;
	}

	public Map(PSFFileIO file, TilesetPanel tileset) {
		ID = file.readInt("id");
		name = file.readString("name");
		try {
			int[] size = file.readIntArray("size");
			width = size[0];
			height = size[1];
			map = new Tile[width][height];
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				final Tile tile = new Tile(x, y);
				map[x][y] = tile;
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
						entitys.add(entity);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
			}
		});
	}

	public void save(PSFFileIO file, TilesetPanel tileset) {
		file.write("id", ID);
		file.write("name", name);
		try {
			file.write("size", new int[] { width, height });
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				final Tile tile = map[x][y];
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
			for (Entity entity : entitys)
				if (entity.id == 0) {
					player = entity;
					break;
				}
			if (player != null)
				entitys.remove(player);
			file.write("size", entitys.size());
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
				entitys.add(player);
		});
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		Tile[][] old = map;
		map = new Tile[width][height];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				if (x < old.length && y < old[0].length)
					map[x][y] = old[x][y];
				else
					map[x][y] = new Tile(x, y);
	}

	public void paint(Graphics g, MapViewPanel mapView, CreatorWindow win) {
		int ts = TilesetPanel.TILESIZE;
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				Tile tile = map[x][y];
				Image i1, i2, i3, prev;
				i1 = win.tileset.getImage(tile.l1);
				i2 = win.tileset.getImage(tile.l2);
				i3 = win.tileset.getImage(tile.l3);
				g.drawImage(i1, x * ts, y * ts, ts, ts, null);
				g.drawImage(i2, x * ts, y * ts, ts, ts, null);
				g.drawImage(i3, x * ts, y * ts, ts, ts, null);
				g.setColor(win.map.getBackground());
				if (tile.prev[1] == -1)
					g.fillRect(x * ts, y * ts, ts, ts);
				else {
					prev = win.tileset.getImage(tile.prev);
					g.drawImage(prev, x * ts, y * ts, ts, ts, null);
				}
				switch (mapView.getMode()) {
				case DRAW_TILES:
					g.setColor(mapView.getC_numbers());
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
						Color c = mapView.getC_blocking();
						g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
						g.fillRect(x * ts, y * ts, ts, ts);
					}
					break;
				default:
					break;
				}
				if (mapView.isDrawGrid()) {
					g.setColor(mapView.getC_grid());
					g.drawRect(x * ts, y * ts, ts, ts);
				}
			}
		if (mapView.getMode() == Mode.ENTITY)
			entitys.forEach(e -> {
				if (e.id == 0)
					g.setColor(P_BLUR);
				else if (e.s_id >= 0)
					g.setColor(S_BLUR);
				else
					g.setColor(BLUR);
				g.fillRect((int) e.getX() * ts + 3, (int) e.getY() * ts + 3, ts - 6, ts - 6);
				if (e.id == 0)
					g.setColor(P_FRAME);
				else if (e.s_id >= 0)
					g.setColor(S_FRAME);
				else
					g.setColor(FRAME);
				g.drawRect((int) e.getX() * ts + 2, (int) e.getY() * ts + 2, ts - 4, ts - 4);
				g.setColor(e.id == 0 ? Color.MAGENTA : mapView.getC_numbers());
				g.setFont(FONT);
				String id = e.id == 0 ? "P" : Integer.toString(e.id);
				g.drawString(id, (int) e.getX() * ts + ts / 2 - g.getFontMetrics().stringWidth(id) / 2,
						(int) e.getY() * ts + 16);
			});
	}

	@Override
	public String toString() {
		return (ID < 10 ? "00" + ID : ID < 100 ? "0" + ID : Integer.toString(ID)) + " - " + name;
	}

}
