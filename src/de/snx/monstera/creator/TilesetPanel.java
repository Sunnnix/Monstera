package de.snx.monstera.creator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class TilesetPanel extends JTabbedPane {

	public static final int TILESIZE = 24;

	private CreatorWindow win;

	public int[] selected = new int[] { -1, -1, 1, 1 };

	private JTabbedPane outdoor, house, cave;
	private Tileset[] tileset = new Tileset[6];

	public TilesetPanel(CreatorWindow win) {
		this.win = win;
		outdoor = new JTabbedPane();
		outdoor.add(paneBuilder(tileset[0] = new Tileset(0)), "Floor");
		tileset[0].loadTiles("outdoor_floor");
		outdoor.add(paneBuilder(tileset[1] = new Tileset(1)), "Object");
		tileset[1].loadTiles("outdoor_object");
		house = new JTabbedPane();
		house.add(paneBuilder(tileset[2] = new Tileset(2)), "Floor");
		tileset[2].loadTiles("house_floor");
		house.add(paneBuilder(tileset[3] = new Tileset(3)), "Object");
		tileset[3].loadTiles("house_object");
		cave = new JTabbedPane();
		cave.add(paneBuilder(tileset[4] = new Tileset(4)), "Floor");
		tileset[4].loadTiles("cave_floor");
		cave.add(paneBuilder(tileset[5] = new Tileset(5)), "Object");
		tileset[5].loadTiles("cave_object");
		add(outdoor, "Outdoor");
		add(house, "House");
		add(cave, "Cave");
		win.repaint();
	}

	public int getSWidth() {
		if (selected[0] == -1)
			return 0;
		return tileset[selected[0]].width;
	}

	private JScrollPane paneBuilder(Tileset set) {
		JScrollPane pane = new JScrollPane(set);
		pane.setPreferredSize(new Dimension(307, 730));
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		return pane;
	}

	public BufferedImage getImage(int[] pos) {
		if (pos[0] == -1)
			return null;
		return tileset[pos[0]].get(pos[1]);
	}

	public BufferedImage getTileset(int id) {
		return tileset[id].root;
	}

	private class Tileset extends JPanel {

		private final int id;
		private int width;
		BufferedImage root, tiles[];

		public Tileset(int id) {
			this.id = id;
			setPreferredSize(new Dimension(289, 730));
			MListener l = new MListener();
			addMouseListener(l);
			addMouseMotionListener(l);
		}

		public void loadTiles(String tileset) {
			try {
				root = ImageIO.read(new File("res/de/snx/monstera/graphic/tileset/tileset_" + tileset + ".png"));
				width = root.getWidth() / TILESIZE;
				int height = root.getHeight() / TILESIZE;
				tiles = new BufferedImage[width * height];
				for (int i = 0; i < tiles.length; i++)
					tiles[i] = root.getSubimage(i % width * TILESIZE, i / width * TILESIZE, TILESIZE, TILESIZE);
				setPreferredSize(new Dimension(width * TILESIZE, height * TILESIZE));
				revalidate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			if (tiles == null)
				return;
			for (int i = 0; i < tiles.length; i++) {
				g.drawImage(tiles[i % width + i / width * width], i % width * TILESIZE, i / width * TILESIZE, TILESIZE,
						TILESIZE, null);
			}
			if (selected[0] == id) {
				g.setColor(Color.GREEN);
				g.drawRect(selected[1] % width * TILESIZE, selected[1] / width * TILESIZE, TILESIZE * selected[2],
						TILESIZE * selected[3]);
			}
		}

		public BufferedImage get(int pos) {
			if (pos < 0 || pos >= tiles.length)
				return null;
			return tiles[pos];
		}

		private class MListener extends MouseAdapter {

			private boolean pressed;
			private int startX, startY;

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					pressed = true;
					int x, y, selected;
					x = e.getX() / TILESIZE;
					y = e.getY() / TILESIZE;
					startX = x;
					startY = y;
					selected = x + y * width;
					if (selected < 0 || selected >= tiles.length)
						return;
					TilesetPanel.this.selected = new int[] { id, selected, 1, 1 };
					win.repaint();
				}
			}

			public void mouseDragged(MouseEvent e) {
				if (pressed) {
					int x, y, selected;
					x = e.getX() / TILESIZE;
					y = e.getY() / TILESIZE;
					selected = x + y * width;
					if (selected < 0 || selected >= tiles.length)
						return;
					int x2, y2;
					if (x > startX) {
						x2 = x;
						x = startX;
					} else
						x2 = startX;
					if (y > startY) {
						y2 = y;
						y = startY;
					} else
						y2 = startY;
					selected = x + y * width;
					int width = x2 - x + 1;
					int height = y2 - y + 1;
					TilesetPanel.this.selected = new int[] { id, selected, width, height };
					repaint();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1)
					pressed = false;
			}
		}
	}

}
