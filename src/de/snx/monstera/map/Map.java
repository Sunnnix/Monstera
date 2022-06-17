package de.snx.monstera.map;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import de.snx.monstera.Game;
import de.snx.monstera.map.event.Event;
import de.snx.monstera.state.GameStateManager;
import de.snx.monstera.state.WorldState;
import de.snx.psf.PSFFileIO;

public class Map {

	public final int ID;
	public final int WIDTH, HEIGHT;
	private MapData[][] map;
	private ArrayList<Entity> entitys = new ArrayList<>();

	public Map(PSFFileIO file) {
		ID = file.readInt("id");
		int width = 0, height = 0;
		try {
			int[] size = file.readIntArray("size");
			width = size[0];
			height = size[1];
			map = new MapData[width][height];
		} catch (Exception e) {
			e.printStackTrace();
		}
		WIDTH = width;
		HEIGHT = height;
		for (int x = 0; x < WIDTH; x++)
			for (int y = 0; y < HEIGHT; y++) {
				final MapData tile = new MapData(x, y);
				map[x][y] = tile;
				file.room("tile_" + x + "_" + y, _s -> tile.load(file));
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

	public void load(PSFFileIO file) {
		file.room("entitys", s -> {
			int entitySize = file.readInt("size");
			for (int i = 0; i < entitySize; i++) {
				file.room("e" + i, st -> {
					try {
						entitys.add(new Entity(file));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
			}
		});
	}

	public void update(WorldState world, GameStateManager gsm) {
		entitys.forEach(e -> e.update(world, this));
	}

	public void render(WorldState state, GameStateManager gsm, Graphics2D g) {
		Tiles.update();
		int cX = state.getCameraX(), cY = state.getCameraY();
		for (int x = 0; x < WIDTH; x++)
			for (int y = 0; y < HEIGHT; y++)
				map[x][y].renderL1(g, x * Game.S_TILESIZE, y * Game.S_TILESIZE, cX, cY);
		entitys.forEach(e -> e.render(g, cX, cY));
		for (int x = 0; x < WIDTH; x++)
			for (int y = 0; y < HEIGHT; y++)
				map[x][y].renderL2(g, x * Game.S_TILESIZE, y * Game.S_TILESIZE, cX, cY);
	}

	public MapData getTile(int x, int y) {
		if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT)
			return null;
		return map[x][y];
	}

	public Entity getEntity(int id) {
		for (Entity entity : entitys)
			if (entity.id == id)
				return entity;
		return null;
	}

	public void addEntity(Entity e) {
		entitys.add(e);
	}

	public void removeEntity(Entity e) {
		entitys.remove(e);
	}

	public Event getEntityEvent(Point pos, int trigger) {
		for (Entity entity : entitys) {
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
