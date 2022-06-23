package de.snx.monstera.data.mapdata;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import de.snx.monstera.Game;
import de.snx.monstera.data.IValueID;
import de.snx.monstera.data.ProjectHandler;
import de.snx.monstera.event.Event;
import de.snx.monstera.global_data.Registry;
import de.snx.monstera.state.WorldState;
import de.snx.psf.PSFFileIO;
import lombok.Getter;
import lombok.Setter;

public class Entity implements IValueID {

	public static final int DIRECTION_WEST = 0;
	public static final int DIRECTION_SOUTH = 1;
	public static final int DIRECTION_EAST = 2;
	public static final int DIRECTION_NORTH = 3;

	public static final int TRIGGER_NONE = 0;
	public static final int TRIGGER_INTERACT = 1;
	public static final int TRIGGER_ON_TOUCH = 2;

	// Attributes
	private int speed = (int) (Game.TICKS * .3);

	// Tech
	public final int id; // player must be every time 0
	public final int s_id; // this is used to point to constant unchangeable events
	public String name = "";
	private int animCount, animIncrease = speed / 2;
	private int anim;
	private ArrayList<Event> events = new ArrayList<>();
	private int eventPointer;
	private String imgRes = "";

	@Getter
	@Setter
	private int eventTrigger = TRIGGER_INTERACT;

	// Values
	@Getter
	private double x, y;
	@Getter
	private int direction = 1;
	private boolean isMoving;
	private int toMove;
	@Getter
	@Setter
	private boolean invisible;

	public Entity(int id) {
		this.id = id;
		this.s_id = -1;
	}

	public Entity(PSFFileIO file) {
		this.id = file.readInt("id");
		this.s_id = file.readInt("s_id");
		this.name = file.readString("name");
		this.x = file.readDouble("x");
		this.y = file.readDouble("y");
		this.invisible = file.readBoolean("invisible");
		this.eventTrigger = file.readInt("trigger");
		file.room("events", s -> {
			int size = file.readInt("size");
			for (int i = 0; i < size; i++) {
				int index = i;
				file.room("e" + index, st -> {
					events.add(Registry.createEventFromFile(file.readString("registry_name"), file));
				});
			}
		});
		eventPointer = file.readInt("e_pointer");
	}

	public void save(PSFFileIO file) throws Exception {
		file.write("id", id);
		file.write("s_id", s_id);
		file.write("name", name);
		file.write("x", x);
		file.write("y", y);
		file.write("invisible", invisible);
		file.write("trigger", eventTrigger);
		file.room("events", s -> {
			file.write("size", events.size());
			for (int i = 0; i < events.size(); i++) {
				int index = i;
				file.room("e" + i, st -> {
					Event e = events.get(index);
					try {
						file.write("registry_name", (String) e.getClass().getField("REGISTRY_NAME").get(null));
						e.onSave(file);
					} catch (Exception e1) {
						throw new RuntimeException(e1);
					}
				});
			}
		});
		file.write("e_pointer", eventPointer);
	}

	@Override
	public int getID() {
		return id;
	}

	/**
	 * @deprecated The id is final and can't be set again
	 */
	@Deprecated
	@Override
	public void setID(int id) {
	}

	public void setImageResource(String imgRes) {
		this.imgRes = imgRes;
	}

	public void update(WorldState world, Map map) {
		move(world, map);
	}

	public void render(Graphics2D g, int offsetX, int offsetY) {
		if (invisible)
			return;
		int ts = ProjectHandler.getProject().getTilesize();
		BufferedImage img = ProjectHandler.getEntityImages().getImage(imgRes).getImage(direction, anim);
		int x = (int) (this.x * ts) + ts / 2 - img.getWidth() / 2 - offsetX;
		int y = (int) (this.y * ts) + ts - img.getHeight() - offsetY;
		g.drawImage(img, x, y, null);
	}

	public void setPos(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void setDirection(int direction) {
		if (!isMoving)
			this.direction = direction;
	}

	public void move(Map map, int direction) {
		if (isMoving || toMove > 0)
			return;
		setDirection(direction);
		toMove = speed;
		Tile tile = null;
		switch (direction) {
		case DIRECTION_NORTH:
			tile = map.getTile((int) x, (int) y - 1);
			break;
		case DIRECTION_SOUTH:
			tile = map.getTile((int) x, (int) y + 1);
			break;
		case DIRECTION_WEST:
			tile = map.getTile((int) x - 1, (int) y);
			break;
		case DIRECTION_EAST:
			tile = map.getTile((int) x + 1, (int) y);
			break;
		default:
			return;
		}
		isMoving = tile != null && !tile.isBlocking;
	}

	private void move(WorldState world, Map map) {
		if (isMoving)
			switch (direction) {
			case DIRECTION_NORTH:
				y -= 1d / speed;
				break;
			case DIRECTION_SOUTH:
				y += 1d / speed;
				break;
			case DIRECTION_WEST:
				x -= 1d / speed;
				break;
			case DIRECTION_EAST:
				x += 1d / speed;
				break;
			default:
				break;
			}
		if (toMove > 0) {
			toMove--;
			if (anim == 0)
				anim++;
			animCount++;
			if (animCount >= animIncrease) {
				animCount = 0;
				anim++;
			}
			if (toMove == 0) {
				x = Math.round(x);
				y = Math.round(y);
				if (isMoving)
					world.onTouchTrigger(this);
				isMoving = false;
			}
		} else {
			anim = 0;
			animCount = 0;
		}
	}

	public String getImageName() {
		return imgRes;
	}

	public Event[] getEvents() {
		return events.toArray(new Event[0]);
	}

	public void setEvents(List<Event> events) {
		this.events.clear();
		this.events.addAll(events);
	}

	public Event getEvent() {
		if (eventPointer >= events.size()) {
			eventPointer = 0;
			return null;
		}
		Event e = events.get(eventPointer);
		e.setCaller(this);
		eventPointer++;
		return e;
	}

	@Override
	public String toString() {
		return (id < 10 ? "00" + id : id < 100 ? "0" + id : id) + " - " + name;
	}

}
