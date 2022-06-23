package de.snx.monsteracreator.window;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class InfoPanel extends JMenuBar {

	private Window win;

	private JLabel pos, mode, layer;

	public InfoPanel(Window win) {
		this.win = win;
		setLayout(new FlowLayout(FlowLayout.LEFT));
		initInfos();
	}

	private void initInfos() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setPreferredSize(new Dimension(80, 25));
		panel.add(pos = new JLabel());
		add(panel);
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setPreferredSize(new Dimension(180, 25));
		panel.add(this.mode = new JLabel("Mode: Draw Tiles"));
		add(panel);
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(this.layer = new JLabel("Layer: 1"));
		add(panel);
	}

	public void setPos(int x, int y) {
		pos.setText("Pos: " + x + ", " + y);
	}

	public void setMode(String mode) {
		this.mode.setText("Mode: " + mode);
	}

	public void setLayer(int layer) {
		this.layer.setText("Layer: " + (layer + 1));
	}
}
