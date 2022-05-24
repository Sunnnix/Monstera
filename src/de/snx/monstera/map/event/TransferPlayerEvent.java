package de.snx.monstera.map.event;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;

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

public class TransferPlayerEvent extends Event {

	public static final String REGISTRY_NAME = "Transfer Player";

	private int x, y, mapID;
	private int direction = -1;
	private int phase = 0, timer, maxTime = 15;

	public TransferPlayerEvent(PSFFileIO file) {
		super(file);
		x = file.readInt("x");
		y = file.readInt("y");
		mapID = file.readInt("id");
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
		return " to [" + mapID + "] " + x + ", " + y;
	}

	@Override
	public void onSave(PSFFileIO file) {
		file.write("x", x);
		file.write("y", y);
		file.write("id", mapID);
	}

	@Override
	public void interact(Map map) {
	}

	@Override
	public Pair<JPanel, Runnable> getEditorDialog(CreatorWindow win) {
		JPanel panel = new JPanel(new GridLayout(0, 2));
		JTextField f_x, f_y;
		JComboBox<de.snx.monstera.creator.Map> maps;
		de.snx.monstera.creator.Map[] am = win.map.maps.toArray(new de.snx.monstera.creator.Map[0]);
		panel.add(new JLabel("X:"));
		panel.add(f_x = new JTextField(5));
		f_x.setText(Integer.toString(x));
		panel.add(new JLabel("Y:"));
		panel.add(f_y = new JTextField(5));
		f_x.setText(Integer.toString(y));
		panel.add(maps = new JComboBox<>(am));
		int mapsID = 0;
		for (int i = 0; i < am.length; i++)
			if (am[i].ID == mapsID) {
				maps.setSelectedIndex(i);
				break;
			}
		Runnable apply = () -> {
			try {
				this.x = Integer.parseInt(f_x.getText());
				this.y = Integer.parseInt(f_y.getText());
				this.mapID = ((de.snx.monstera.creator.Map) maps.getSelectedItem()).ID;
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
