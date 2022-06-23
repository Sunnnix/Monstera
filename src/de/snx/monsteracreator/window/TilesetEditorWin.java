package de.snx.monsteracreator.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import de.snx.monstera.data.ProjectHandler;
import de.snx.monstera.data.TilesetProperties;
import de.snx.monstera.data.TilesetProperties.Propertie;
import de.snx.monstera.data.Tilesets;
import de.snx.monstera.global_data.ResourceStrings;
import de.snx.monsteracreator.Config;

@SuppressWarnings("serial")
public class TilesetEditorWin extends JDialog {

	private Window window;
	private Tilesets tilesets;

	private JTextField tooltip;
	private Propertie selectedProp;
	private JCheckBox cb_animate;
	private JSpinner animTimer;
	private JTextField tf_src;

	private JComboBox<String> tileChooser;

	private JButton addDelete, save;

	private int[] selected = new int[] { -1, -1 };

	private int width, height;

	public TilesetEditorWin(Window window, Tilesets tilesets) {
		super(window, "Tileset Editor", ModalityType.APPLICATION_MODAL);
		this.window = window;
		this.tilesets = tilesets;
		setLayout(new BorderLayout());
		init();
		loadPropertie();
		pack();
		setLocationRelativeTo(window);
		setVisible(true);
	}

	private void init() {
		JPanel tmp = new JPanel(new BorderLayout());
		JPanel propertie = new JPanel(new GridLayout(0, 1));
		propertie.add(createRow("Tooltip", tooltip = new JTextField()));
		propertie.add(createRow("Animated Tiles:", cb_animate = new JCheckBox()));
		cb_animate.addActionListener(a -> {
			if (selectedProp != null)
				selectedProp.animate = cb_animate.isSelected();
		});
		propertie.add(createRow("Animation Timer:", animTimer = new JSpinner(new SpinnerNumberModel(15, 1, 120, 1))));
		propertie.add(createRow("Image Source:", tf_src = new JTextField()));
		tf_src.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!tf_src.isEnabled())
					return;
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() > 1)
					openResourceDialog();
			}
		});
		tmp.add(propertie, BorderLayout.NORTH);
		JPanel buttons = new JPanel();
		buttons.add(addDelete = new JButton("Add"));
		addDelete.addActionListener(a -> {
			if (selected[0] == -1)
				return;
			if (selectedProp == null) {
				tilesets.getProperties().setPropertie(selected, new Propertie());
				loadPropertie();
			} else {
				tilesets.getProperties().removePropertie(selected);
				loadPropertie();
			}
		});
		buttons.add(save = new JButton("Save"));
		save.addActionListener(a -> {
			if (cb_animate.isSelected() && tf_src.getText().isEmpty()) {
				JOptionPane.showMessageDialog(TilesetEditorWin.this, "Please select an Image Source!");
				return;
			}
			selectedProp.toolTip = tooltip.getText();
			selectedProp.animate = cb_animate.isSelected();
			selectedProp.animTempo = (byte) (int) animTimer.getValue();
			selectedProp.src = tf_src.getText();
			if (selectedProp.animate)
				selectedProp.loadAnimImage();
		});
		tmp.add(buttons, BorderLayout.SOUTH);
		add(tmp, BorderLayout.WEST);
		JPanel tilesetRoot = new JPanel(new BorderLayout());
		tileChooser = new JComboBox<>(new String[] { "Outdoor - Floor", "Outdoor - Object", "House - Floor",
				"House - Object", "Cave - Floor", "Cave - Object" });
		tileChooser.addActionListener(a -> loadTileset());
		tilesetRoot.add(tileChooser, BorderLayout.NORTH);
		JScrollPane scroll = new JScrollPane(createTilesetView());
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		tilesetRoot.add(scroll, BorderLayout.CENTER);
		add(tilesetRoot, BorderLayout.CENTER);
	}

	private JPanel createRow(String text, JComponent comp) {
		JPanel panel = new JPanel();
		JLabel label = new JLabel(text);
		label.setPreferredSize(new Dimension(100, 20));
		panel.add(label);
		comp.setPreferredSize(new Dimension(100, 20));
		panel.add(comp);
		return panel;
	}

	private JPanel createTilesetView() {
		JPanel panel = new JPanel() {

			BufferedImage img;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				int tileSize = ProjectHandler.getProject().getTilesize();
				int index = tileChooser.getSelectedIndex();
				BufferedImage tmp = ProjectHandler.getTilesets().getFull(index);
				if (tmp == null)
					return;
				if (!tmp.equals(img)) {
					img = tmp;
					setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
				}
				width = img.getWidth() / tileSize;
				height = img.getHeight() / tileSize;
				g.drawImage(img, 0, 0, null);
				for (int x = 0; x < width; x++)
					for (int y = 0; y < height; y++) {
						if (tilesets.getProperties()
								.getPropertie(new int[] { index, x + y * width }) != TilesetProperties.EMPTY) {
							int pX, pY;
							pX = x * tileSize;
							pY = y * tileSize;
							g.setColor(Config.getEditorColor(Config.C_TILESET_E_PROPERTIE));
							g.drawRect(pX, pY, tileSize, tileSize);
							g.drawRect(pX + 1, pY + 1, tileSize - 2, tileSize - 2);
							for (int i = 0; i < tileSize / 2; i++)
								g.drawLine(pX + 2 * i, pY, pX + tileSize, pY + tileSize - 2 * i);
							g.drawLine(pX, pY, pX + tileSize, pY + tileSize);
							for (int i = 0; i < tileSize / 2; i++)
								g.drawLine(pX, pY + 2 * i, pX + tileSize - 2 * i, pY + tileSize);
						}
						if (selected[0] == tileChooser.getSelectedIndex() && selected[1] % (width) == x
								&& selected[1] / (width) == y) {
							g.setColor(Config.getEditorColor(Config.C_TILESET_E_SELECTED));
							g.drawRect(x * tileSize, y * tileSize, tileSize, tileSize);
						}
					}
			}
		};
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					int tileSize = ProjectHandler.getProject().getTilesize();
					int x, y;
					x = e.getX() / tileSize;
					y = e.getY() / tileSize;
					if (x < 0 || x >= width || y < 0 || y >= height)
						return;
					selected[0] = tileChooser.getSelectedIndex();
					selected[1] = x + y * width;
					loadPropertie();
				}
			}
		});
		panel.setPreferredSize(new Dimension(289, 730));
		return panel;
	}

	private void openResourceDialog() {
		JFileChooser chooser = new JFileChooser(new File(ResourceStrings.TILESET_PATH + "animations/"));
		chooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "PNG File";
			}

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".png");
			}
		});
		chooser.showOpenDialog(this);
		File f = chooser.getSelectedFile();
		if (f == null)
			return;
		tf_src.setText(f.getName().substring(0, f.getName().length() - 4));
	}

	private void loadTileset() {
		repaint();
	}

	private void loadPropertie() {
		tooltip.setText("");
		cb_animate.setSelected(false);
		animTimer.setValue(15);
		tf_src.setText("");
		selectedProp = tilesets.getProperties().getPropertie(selected);
		if (selectedProp == TilesetProperties.EMPTY) {
			activateAll(false);
			selectedProp = null;
		} else {
			activateAll(true);
			tooltip.setText(selectedProp.toolTip);
			cb_animate.setSelected(selectedProp.animate);
			animTimer.setValue((int) selectedProp.animTempo);
			tf_src.setText(selectedProp.src);
		}
		repaint();
	}

	private void activateAll(boolean activate) {
		tooltip.setEnabled(activate);
		cb_animate.setEnabled(activate);
		animTimer.setEnabled(activate);
		tf_src.setEnabled(activate);
		tf_src.setEditable(false);
		addDelete.setText(activate ? "Delete" : "Add");
		save.setEnabled(activate);
	}

}
