package de.snx.monsteracreator.window;

import static de.snx.monsteracreator.Config.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CustomizerWin extends JDialog {

	private Window win;

	public CustomizerWin(Window win) {
		super(win, "Customizer", ModalityType.APPLICATION_MODAL);
		this.win = win;
		setLayout(new GridLayout(0, 1));
		init();
		pack();
		setLocationRelativeTo(win);
		setVisible(true);
	}

	private void init() {
		add(picker("Map Background", C_MAP_VIEW_BACKGROUND));
		add(picker("Map Grid", C_MAP_VIEW_GRID));
		add(picker("Map Layer Number", C_MAP_VIEW_LAYER_NUM));
		add(picker("Map Blocking Field", C_MAP_VIEW_BLOCKING));
		add(picker("Map Entity Number", C_MAP_VIEW_E_NUM));
		add(picker("Map Entity Background", C_MAP_VIEW_E_BACK));
		add(picker("Map Player Number", C_MAP_VIEW_P_NUM));
		add(picker("Map Player Background", C_MAP_VIEW_P_BACK));
		add(picker("Map Special Entity Background", C_MAP_VIEW_S_BACK));
		add(picker("Tileset Background", C_TILESET_BACKGROUND));
		add(picker("Tileset Selection", C_TILESET_SELECTED));
		add(picker("Tileset Editor Selection", C_TILESET_E_SELECTED));
		add(picker("Tileset Editor Propertie", C_TILESET_E_PROPERTIE));
	}

	private JPanel picker(String text, int colorID) {
		return picker(text, colorID, 255);
	}

	private JPanel picker(String text, int colorID, int alpha) {
		Color in = getEditorColor(colorID);
		JPanel panel = new JPanel();
		JLabel label = new JLabel(text + ":");
		label.setPreferredSize(new Dimension(200, 15));
		panel.add(label);
		JButton button = new JButton("");
		button.setPreferredSize(new Dimension(40, 15));
		button.setBackground(in);
		panel.add(button);
		button.addActionListener(action -> {
			Color out = JColorChooser.showDialog(CustomizerWin.this, text, in);
			if (out == null)
				return;
			out = new Color(out.getRed(), out.getGreen(), out.getBlue(), alpha);
			button.setBackground(out);
			setEditorColor(colorID, out);
			win.repaint();
		});
		return panel;
	}

}
