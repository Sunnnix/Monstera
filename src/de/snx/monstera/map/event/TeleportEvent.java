package de.snx.monstera.map.event;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.snx.monstera.creator.CreatorWindow;
import de.snx.monstera.creator.Pair;
import de.snx.monstera.map.Entity;
import de.snx.monstera.map.Map;
import de.snx.monstera.state.GameStateManager;
import de.snx.monstera.state.WorldState;
import de.snx.psf.PSFFileIO;

public class TeleportEvent extends Event {

	public static final String REGISTRY_NAME = "TeleportEvent";

	private int x, y;
	private int direction = -1;
	private boolean animate = true;
	private int phase = 0, timer, maxTime = 15;
	private int entityID;

	public TeleportEvent(PSFFileIO file) {
		super(file);
		x = file.readInt("x");
		y = file.readInt("y");
		entityID = file.readInt("entity");
		animate = file.readBoolean("animate");
	}

	public TeleportEvent() {
	}

	@Override
	public boolean blockAction() {
		return false;
	}

	@Override
	public void keyEvents(WorldState world, Map map) {
	}

	@Override
	public void update(Map map, WorldState world, GameStateManager gsm) {
		if (!animate || phase == 1 && timer == 0) {
			Entity e = map.getEntity(entityID);
			if (e != null)
				e.setPos(x, y);
			if (!animate)
				finished = true;
		} else {
			if (timer == maxTime)
				if (phase == 0) {
					phase = 1;
					timer = 0;
					return;
				} else
					finished = true;
		}
		if (!finished)
			timer++;
	}

	@Override
	public void render(Graphics2D g, WorldState world, Map map) {
		if (animate) {
			if (phase == 0)
				g.setColor(new Color(0, 0, 0, (int) (255 * ((double) timer / maxTime))));
			else
				g.setColor(new Color(0, 0, 0, (int) (255 * (1 - ((double) timer / maxTime)))));
			g.fillRect(0, 0, world.screenWidth(), world.screenHeight());
		}
	}

	@Override
	public String getEventInfo() {
		return entityID + " to " + x + ", " + y;
	}

	@Override
	public void onSave(PSFFileIO file) {
		file.write("x", x);
		file.write("y", y);
		file.write("entity", entityID);
		file.write("animate", animate);
	}

	@Override
	public void interact(Map map) {
	}

	@Override
	public Pair<JPanel, Runnable> getEditorDialog(CreatorWindow win) {
		JPanel panel = new JPanel(new GridLayout(0, 2));
		JTextField f_x, f_y;
		JCheckBox anim;
		ArrayList<Entity> es = win.map.selectedMap.entitys;
		Entity[] aes = new Entity[es.size() + 1];
		for (int i = 0; i < aes.length; i++)
			if (i == 0)
				aes[i] = win.map.player;
			else
				aes[i] = es.get(i - 1);
		JComboBox<Entity> entitys = new JComboBox<Entity>(aes);
		panel.add(new JLabel("X:"));
		panel.add(f_x = new JTextField(5));
		f_x.setText(Integer.toString(x));
		panel.add(new JLabel("Y:"));
		panel.add(f_y = new JTextField(5));
		f_y.setText(Integer.toString(y));
		panel.add(new JLabel("Animate"));
		panel.add(anim = new JCheckBox());
		anim.setSelected(animate);
		anim.setSelected(true);
		panel.add(entitys);
		Runnable apply = () -> {
			try {
				this.x = Integer.parseInt(f_x.getText());
				this.y = Integer.parseInt(f_y.getText());
				this.entityID = ((Entity) entitys.getSelectedItem()).id;
			} catch (Exception e) {
				System.err.println("Invalid input.");
			}
		};
		return new Pair<JPanel, Runnable>(panel, apply);
	}

}
