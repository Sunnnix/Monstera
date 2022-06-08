package de.snx.monstera.map.event;

import static de.snx.monstera.map.event.TeleportEvent.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.snx.monstera.creator.CreatorWindow;
import de.snx.monstera.creator.Pair;
import de.snx.monstera.map.Map;
import de.snx.monstera.state.GameStateManager;
import de.snx.monstera.state.WorldState;
import de.snx.psf.PSFFileIO;

/**
 * Is used to teleport the Player to another Map
 */
public class TransferPlayerEvent extends Event {

	public static final String REGISTRY_NAME = "Transfer Player";

	private int mapID;
	private int x, y;
	private int direction = -1;
	private boolean animate = true;
	private int phase = 0, timer, maxTime = 15;

	public TransferPlayerEvent(PSFFileIO file) {
		super(file);
		mapID = file.readInt("id");
		x = file.readInt("x");
		y = file.readInt("y");
		direction = file.readInt("direction");
		animate = file.readBoolean("animate");
	}

	public TransferPlayerEvent() {
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
		if (phase == 1 && timer == 0) {
			world.transferPlayer(mapID, x, y);
			if (direction != -1)
				world.getEntity(0).setDirection(direction);
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
		if (phase == 0)
			g.setColor(new Color(0, 0, 0, (int) (255 * ((double) timer / maxTime))));
		else
			g.setColor(new Color(0, 0, 0, (int) (255 * (1 - ((double) timer / maxTime)))));
		g.fillRect(0, 0, world.screenWidth(), world.screenHeight());
	}

	@Override
	public String getEventInfo() {
		return " to [" + mapID + "] (" + x + ", " + y + ") Direction: " + getDirectionString();
	}

	private String getDirectionString() {
		if (direction + 1 < 0 || direction + 1 >= S_DIRECTIONS.length)
			return "";
		else
			return S_DIRECTIONS[direction + 1];
	}

	@Override
	public void onSave(PSFFileIO file) {
		file.write("id", mapID);
		file.write("x", x);
		file.write("y", y);
		file.write("direction", direction);
		file.write("animate", animate);
	}

	@Override
	public void interact(Map map) {
	}

	@Override
	public Pair<JPanel, Runnable> getEditorDialog(CreatorWindow win) {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel grid = new JPanel(new GridLayout(0, 2));
		JTextField f_x, f_y;
		JComboBox<String> direction;
		JCheckBox anim;
		JComboBox<de.snx.monstera.creator.Map> maps;
		de.snx.monstera.creator.Map[] am = win.map.maps.toArray(new de.snx.monstera.creator.Map[0]);
		panel.add(new JLabel("Map to Teleport to:"), BorderLayout.NORTH);
		panel.add(maps = new JComboBox<>(am), BorderLayout.CENTER);
		grid.add(new JLabel("X:"));
		grid.add(f_x = new JTextField(5));
		f_x.setText(Integer.toString(x));
		grid.add(new JLabel("Y:"));
		grid.add(f_y = new JTextField(5));
		f_y.setText(Integer.toString(y));
		grid.add(new JLabel("Direction:"));
		direction = new JComboBox<>(S_DIRECTIONS);
		grid.add(direction);
		grid.add(new JLabel("Animate:"));
		grid.add(anim = new JCheckBox());
		anim.setSelected(animate);
		anim.setSelected(true);
		panel.add(grid, BorderLayout.SOUTH);
		Runnable apply = () -> {
			try {
				this.mapID = ((de.snx.monstera.creator.Map) maps.getSelectedItem()).ID;
				this.x = Integer.parseInt(f_x.getText());
				this.y = Integer.parseInt(f_y.getText());
				this.direction = direction.getSelectedIndex();
				this.animate = anim.isSelected();
			} catch (Exception e) {
				System.err.println("Invalid input.");
			}
		};
		return new Pair<JPanel, Runnable>(panel, apply);
	}

	@Override
	public boolean isConstant() {
		return true;
	}

}
