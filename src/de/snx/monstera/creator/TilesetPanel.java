package de.snx.monstera.creator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class TilesetPanel extends JTabbedPane {

	public static final int TILESIZE = 24;
	public static final int COL = 12;

	private CreatorWindow win;

	private HashMap<String, BufferedImage> tileset = new HashMap<>();
	private ArrayList<String> keys = new ArrayList<>();

	public int selected = 0;

	private JTabbedPane outdoor, house, cave;
	private Tileset o_floor, o_object, h_floor, h_object, c_floor, c_object;

	public TilesetPanel(CreatorWindow win) {
		this.win = win;
		outdoor = new JTabbedPane();
		outdoor.add(o_floor = new Tileset(), "Floor");
		outdoor.add(o_object = new Tileset(), "Object");
		house = new JTabbedPane();
		house.add(h_floor = new Tileset(), "Floor");
		house.add(h_object = new Tileset(), "Object");
		cave = new JTabbedPane();
		cave.add(c_floor = new Tileset(), "Floor");
		cave.add(c_object = new Tileset(), "Object");
		add(outdoor, "Outdoor");
		add(house, "House");
		add(cave, "Cave");
		int count = o_floor.loadTiles(0, "outdoor/floor");
		count = o_object.loadTiles(count, "outdoor/object");
		count = h_floor.loadTiles(count, "house/floor");
		count = h_object.loadTiles(count, "house/object");
		count = c_floor.loadTiles(count, "cave/floor");
		count = c_object.loadTiles(count, "cave/object");
		win.repaint();
	}

	public BufferedImage getImage(int id) {
		if (id < 0)
			return null;
		return tileset.get(keys.get(id));
	}

	public String getRegistryName(int id) {
		if (id < 0)
			return "null";
		else
			return keys.get(id);
	}

	public int getIDFromKey(String key) {
		for (int i = 0; i < keys.size(); i++)
			if (keys.get(i).equals(key))
				return i;
		return -1;
	}

	private class Tileset extends JPanel {

		private int start, end;

		public Tileset() {
			setPreferredSize(new Dimension(298, 730));
			MListener l = new MListener();
			addMouseListener(l);
			addMouseMotionListener(l);
		}

		public int loadTiles(int start, String parentPath) {
			this.start = start;
			int counter = start;
			try {
				File[] files = new File("res/de/snx/monstera/graphic/tile/" + parentPath).listFiles(new FileFilter() {
					@Override
					public boolean accept(File f) {
						return f.getName().endsWith(".png");
					}
				});
				for (File file : files) {
					String path = parentPath + "/" + file.getName().substring(0, file.getName().indexOf(".png"));
					BufferedImage img = ImageIO.read(file);
					if (tileset.containsKey(path)) {
						for (int index = 0; index < keys.size(); index++)
							if (keys.get(index).equals(path))
								keys.set(index, path);
					} else
						keys.add(path);
					tileset.put(path, img);
					counter++;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
			end = counter;
			return counter;
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			if (start < keys.size())
				for (int i = start; i < end; i++) {
					BufferedImage img = tileset.get(keys.get(i));
					g.drawImage(img, 5 + (i - start) % COL * TILESIZE, 5 + (i - start) / COL * TILESIZE, TILESIZE,
							TILESIZE, win);
				}
			if (selected >= start && selected < end) {
				g.setColor(Color.GREEN);
				g.drawRect(5 + TILESIZE * ((selected - start) % COL), 5 + TILESIZE * ((selected - start) / COL),
						TILESIZE, TILESIZE);
			}
		}

		private class MListener extends MouseAdapter {

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					int x, y, selected;
					x = e.getX();
					y = e.getY();
					if (x < 5 || x > 5 + TILESIZE * COL || y < 5)
						return;
					x = (x - 5) / TILESIZE;
					y = (y - 5) / TILESIZE;
					selected = start + (x + y * COL);
					if (selected < start || selected >= end || selected >= keys.size())
						return;
					TilesetPanel.this.selected = selected;
					win.repaint();
				}
			}
		}
	}

}
