package de.snx.monstera.map.event;

import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.snx.monstera.creator.CreatorWindow;
import de.snx.monstera.creator.Pair;
import de.snx.monstera.map.Entity;
import de.snx.monstera.map.Map;
import de.snx.monstera.state.GameStateManager;
import de.snx.monstera.state.WorldState;
import de.snx.psf.PSFFileIO;
import lombok.Getter;

public abstract class Event implements Cloneable {

	public static final String REGISTRY_NAME = null;

	@Getter
	protected boolean finished;

	@Getter
	private Entity caller; // The Entity this event comes from

	/**
	 * For loading from file (onLoad)
	 */
	public Event(PSFFileIO file) {
	}

	/**
	 * for creating with defautlValues
	 */
	public Event() {
	}

	public abstract boolean blockAction();

	public abstract void keyEvents(WorldState world, Map map);

	public abstract void update(Map map, WorldState world, GameStateManager gsm);

	public abstract void render(Graphics2D g, WorldState world, Map map);

	public abstract String getEventInfo();

	public abstract void onSave(PSFFileIO file) throws Exception;

	/**
	 * @return is this event global (is no map specific access given) or should the
	 *         event continue when the map is changed
	 */
	public boolean isConstant() {
		return false;
	}

	public Pair<JPanel, Runnable> getEditorDialog(CreatorWindow win) {
		JPanel panel = new JPanel();
		panel.add(new JLabel(
				"Nothing in here! Create you own editor Dialog by implementing the static getEditorDialog from Event class"));
		return new Pair<JPanel, Runnable>(panel, null);
	}

	@Override
	public final String toString() {
		try {
			return getClass().getField("REGISTRY_NAME").get(this) + ": " + getEventInfo();
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return super.toString();
		}
	}

	@Override
	public Event clone() {
		try {
			return (Event) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public abstract void interact(Map map);

	public void setCaller(Entity entity) {
		this.caller = entity;
	}

}
