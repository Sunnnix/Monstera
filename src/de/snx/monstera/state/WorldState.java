package de.snx.monstera.state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import de.snx.monstera.Game;
import de.snx.monstera.battle.Ability;
import de.snx.monstera.battle.MonsterType;
import de.snx.monstera.global_data.CombatGroups;
import de.snx.monstera.global_data.Keys;
import de.snx.monstera.map.Entity;
import de.snx.monstera.map.Entitys;
import de.snx.monstera.map.Map;
import de.snx.monstera.map.event.Event;
import de.snx.psf.PSFFileIO;

public class WorldState extends GameState {

	private ArrayList<Map> loadedMaps = new ArrayList<>();
	private Map currentMap;
	private Entity player;
	private Event runningEvent;
	private Rectangle camera;

	public WorldState(int id) {
		super(id);
		// TODO temp solution ====
		camera = new Rectangle(0, 0, Game.DEFAULT_SIZE.width, Game.DEFAULT_SIZE.height);
		player = new Entity(0);
		player.setImage(Entitys.PLAYER);
		// TODO =================
		setBackgroundColor(Color.BLACK);
		String fileName = "test";
		int[] pMapID = new int[] { -1 };
		int[] map_ids = new int[0];
		try (PSFFileIO file = new PSFFileIO("de/snx/monstera/map/" + fileName + ".mgame")) {
			map_ids = file.readIntArray("maps");
			file.room("player", _s -> {
				pMapID[0] = file.readInt("map_id");
				player.setPos(file.readDouble("x"), file.readDouble("y"));
				player.setDirection(file.readInt("direction"));
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		try (PSFFileIO file = new PSFFileIO("de/snx/monstera/map/" + fileName + "/abilities.dat")) {
			Ability.loadAll(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try (PSFFileIO file = new PSFFileIO("de/snx/monstera/map/" + fileName + "/monsters.dat")) {
			MonsterType.loadAll(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try (PSFFileIO file = new PSFFileIO("de/snx/monstera/map/" + fileName + "/groups.dat")) {
			CombatGroups.loadAll(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int map : map_ids) {
			try (PSFFileIO file = new PSFFileIO("de/snx/monstera/map/" + fileName + "/map" + map + ".dat")) {
				Map tmp = new Map(file);
				loadedMaps.add(tmp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (pMapID[0] != -1)
			transferPlayer(pMapID[0], (int) player.getX(), (int) player.getY());
	}

	@Override
	protected void load(String... args) {
	}

	@Override
	protected void keyEvents(GameStateManager gsm) {
		if (Keys.DEBUG.isPressed())
			drawDebug = !drawDebug;
		if (currentMap == null)
			return;
		if (runningEvent != null) {
			if (Keys.CONFIRM.isPressed())
				runningEvent.interact(currentMap);
			runningEvent.keyEvents(this, currentMap);
		} else {
			if (Keys.UP.isPressed())
				player.setDirection(Entity.DIRECTION_NORTH);
			else if (Keys.UP.isHold())
				player.move(currentMap, Entity.DIRECTION_NORTH);
			if (Keys.DOWN.isPressed())
				player.setDirection(Entity.DIRECTION_SOUTH);
			else if (Keys.DOWN.isHold())
				player.move(currentMap, Entity.DIRECTION_SOUTH);
			if (Keys.LEFT.isPressed())
				player.setDirection(Entity.DIRECTION_WEST);
			else if (Keys.LEFT.isHold())
				player.move(currentMap, Entity.DIRECTION_WEST);
			if (Keys.RIGHT.isPressed())
				player.setDirection(Entity.DIRECTION_EAST);
			else if (Keys.RIGHT.isHold())
				player.move(currentMap, Entity.DIRECTION_EAST);
			if (Keys.CONFIRM.isPressed())
				interact();
		}
	}

	@Override
	protected void update(GameStateManager gsm, int ticks) {
		if (currentMap == null)
			return;
		setCameraPos();
		if (runningEvent != null) {
			runningEvent.update(currentMap, this, gsm);
			if (runningEvent.isFinished())
				runningEvent = runningEvent.getCaller().getEvent();
		} else
			currentMap.update(this, gsm);
	}

	@Override
	protected void render(GameStateManager gsm, Graphics2D g) {
		if (currentMap != null)
			currentMap.render(this, gsm, g);
		addDebugText("Pos: " + player.getX() + ", " + player.getY());
		addDebugText("Event: " + runningEvent);
		if (runningEvent != null)
			runningEvent.render(g, this, currentMap);
	}

	public void changeMap(int mapID) {
		for (Map map : loadedMaps)
			if (map.ID == mapID) {
				currentMap = map;
				return;
			}
	}

	private void interact() {
		if (runningEvent == null) {
			Point interactPos = new Point((int) player.getX(), (int) player.getY());
			switch (player.getDirection()) {
			case Entity.DIRECTION_NORTH:
				interactPos.y--;
				break;
			case Entity.DIRECTION_SOUTH:
				interactPos.y++;
				break;
			case Entity.DIRECTION_WEST:
				interactPos.x--;
				break;
			case Entity.DIRECTION_EAST:
				interactPos.x++;
				break;
			default:
				break;
			}
			Event e = currentMap.getEntityEvent(interactPos, Entity.TRIGGER_INTERACT);
			if (e != null)
				runningEvent = e;
		} else
			runningEvent.interact(currentMap);
	}

	private void setCameraPos() {
		int x = (int) (Game.S_TILESIZE / 2 + player.getX() * Game.S_TILESIZE - camera.getWidth() / 2);
		int y = (int) (Game.S_TILESIZE / 2 + player.getY() * Game.S_TILESIZE - camera.getHeight() / 2);
		camera.setLocation(x, y);
	}

	public int screenWidth() {
		return camera.width;
	}

	public int screenHeight() {
		return camera.height;
	}

	public int getCameraX() {
		return camera.x;
	}

	public int getCameraY() {
		return camera.y;
	}

	public void onTouchTrigger(Entity entity) {
		Event e = currentMap.getEntityEvent(new Point((int) entity.getX(), (int) entity.getY()),
				Entity.TRIGGER_ON_TOUCH);
		if (e != null)
			runningEvent = e;
	}

	public void transferPlayer(int mapID, int x, int y) {
		if (currentMap != null)
			currentMap.removeEntity(player);
		changeMap(mapID);
		player.setPos(x, y);
		currentMap.addEntity(player);
	}

	public Entity getEntity(int i) {
		return currentMap.getEntity(i);
	}

}
