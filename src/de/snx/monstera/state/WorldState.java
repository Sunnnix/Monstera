package de.snx.monstera.state;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import de.snx.monstera.Game;
import de.snx.monstera.data.ProjectHandler;
import de.snx.monstera.data.mapdata.Entity;
import de.snx.monstera.data.mapdata.Map;
import de.snx.monstera.event.Event;
import de.snx.monstera.global_data.Keys;

public class WorldState extends GameState {

	private Event runningEvent;
	private Rectangle camera;

	public WorldState(int id) {
		super(id);
		camera = new Rectangle(0, 0, Game.DEFAULT_SIZE.width, Game.DEFAULT_SIZE.height);
//		// TODO temp solution ====
//		player = new Entity(0);
//		player.setImage(Entitys.PLAYER);
//		// TODO =================
//		setBackgroundColor(Color.BLACK);
//		String fileName = "test";
//		int[] pMapID = new int[] { -1 };
//		int[] map_ids = new int[0];
//		try (PSFFileIO file = new PSFFileIO(ResourceStrings.TILESET_PATH + "properties.prop", "r")) {
//			ProjectHandler.getTilesets().getProperties().load(file);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try (PSFFileIO file = new PSFFileIO("de/snx/monstera/map/" + fileName + ".mgame")) {
//			map_ids = file.readIntArray("maps");
//			file.room("player", _s -> {
//				pMapID[0] = file.readInt("map_id");
//				player.setPos(file.readDouble("x"), file.readDouble("y"));
//				player.setDirection(file.readInt("direction"));
//			});
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try (PSFFileIO file = new PSFFileIO("de/snx/monstera/map/" + fileName + "/abilities.dat")) {
//			Ability.loadAll(file);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try (PSFFileIO file = new PSFFileIO("de/snx/monstera/map/" + fileName + "/monsters.dat")) {
//			MonsterType.loadAll(file);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try (PSFFileIO file = new PSFFileIO("de/snx/monstera/map/" + fileName + "/groups.dat")) {
//			CombatGroups.loadAll(file);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		for (int map : map_ids) {
//			try (PSFFileIO file = new PSFFileIO("de/snx/monstera/map/" + fileName + "/map" + map + ".dat")) {
//				Map tmp = new Map(file);
//				loadedMaps.add(tmp);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		if (pMapID[0] != -1)
//			ProjectHandler.getMaps().transferPlayer(pMapID[0], (int) player.getX(), (int) player.getY());
	}

	@Override
	protected void load(String... args) {
	}

	@Override
	protected void keyEvents(GameStateManager gsm) {
		Entity player = ProjectHandler.getMaps().getPlayer();
		if (Keys.DEBUG.isPressed())
			drawDebug = !drawDebug;
		Map map = ProjectHandler.getMaps().getSelected();
		if (runningEvent != null) {
			if (Keys.CONFIRM.isPressed())
				runningEvent.interact(map);
			runningEvent.keyEvents(this, map);
		} else {
			if (Keys.UP.isPressed())
				player.setDirection(Entity.DIRECTION_NORTH);
			else if (Keys.UP.isHold())
				player.move(map, Entity.DIRECTION_NORTH);
			if (Keys.DOWN.isPressed())
				player.setDirection(Entity.DIRECTION_SOUTH);
			else if (Keys.DOWN.isHold())
				player.move(map, Entity.DIRECTION_SOUTH);
			if (Keys.LEFT.isPressed())
				player.setDirection(Entity.DIRECTION_WEST);
			else if (Keys.LEFT.isHold())
				player.move(map, Entity.DIRECTION_WEST);
			if (Keys.RIGHT.isPressed())
				player.setDirection(Entity.DIRECTION_EAST);
			else if (Keys.RIGHT.isHold())
				player.move(map, Entity.DIRECTION_EAST);
			if (Keys.CONFIRM.isPressed())
				interact();
		}
	}

	@Override
	protected void update(GameStateManager gsm, int ticks) {
		setCameraPos();
		if (runningEvent != null) {
			runningEvent.update(ProjectHandler.getMaps().getSelected(), this, gsm);
			if (runningEvent.isFinished())
				runningEvent = runningEvent.getCaller().getEvent();
		} else
			ProjectHandler.getMaps().update(this, gsm);
	}

	@Override
	protected void render(GameStateManager gsm, Graphics2D g) {
		Entity player = ProjectHandler.getMaps().getPlayer();
		Map map = ProjectHandler.getMaps().getSelected();
		map.render(this, gsm, g);
		addDebugText("Pos: " + player.getX() + ", " + player.getY());
		addDebugText("Event: " + runningEvent);
		if (runningEvent != null)
			runningEvent.render(g, this, map);
	}

	private void interact() {
		if (runningEvent == null) {
			Entity player = ProjectHandler.getMaps().getPlayer();
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
			Event e = ProjectHandler.getMaps().getSelected().getEntityEvent(interactPos, Entity.TRIGGER_INTERACT);
			if (e != null)
				runningEvent = e;
		} else
			runningEvent.interact(ProjectHandler.getMaps().getSelected());
	}

	private void setCameraPos() {
		int tilesize = ProjectHandler.getProject().getTilesize();
		Entity player = ProjectHandler.getMaps().getPlayer();
		int x = (int) (tilesize / 2 + player.getX() * tilesize - camera.getWidth() / 2);
		int y = (int) (tilesize / 2 + player.getY() * tilesize - camera.getHeight() / 2);
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
		Event e = ProjectHandler.getMaps().getSelected()
				.getEntityEvent(new Point((int) entity.getX(), (int) entity.getY()), Entity.TRIGGER_ON_TOUCH);
		if (e != null)
			runningEvent = e;
	}

}
