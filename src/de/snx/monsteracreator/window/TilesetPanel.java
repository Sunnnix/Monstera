package de.snx.monsteracreator.window;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import de.snx.monstera.data.ProjectHandler;
import de.snx.monstera.data.TilesetProperties.Propertie;
import de.snx.monstera.data.Tilesets;
import de.snx.monsteracreator.Config;

@SuppressWarnings("serial")
public class TilesetPanel extends JTabbedPane {

	private Window win;
	private JTabbedPane outdoor, house, cave;

	private int animTimer;

	public TilesetPanel(Window win) {
		this.win = win;
		outdoor = new JTabbedPane();
		outdoor.add(paneBuilder(new Tileset(0)), "Floor");
		outdoor.add(paneBuilder(new Tileset(1)), "Object");
		house = new JTabbedPane();
		house.add(paneBuilder(new Tileset(2)), "Floor");
		house.add(paneBuilder(new Tileset(3)), "Object");
		cave = new JTabbedPane();
		cave.add(paneBuilder(new Tileset(4)), "Floor");
		cave.add(paneBuilder(new Tileset(5)), "Object");
		add(outdoor, "Outdoor");
		add(house, "House");
		add(cave, "Cave");
		win.repaint();
		new Thread(() -> {
			boolean running = true;
			while (running) {
				try {
					Thread.sleep(1000 / 60);
					animTimer++;
					repaint();
				} catch (InterruptedException e) {
					running = false;
					e.printStackTrace();
				}
			}
		}).start();
	}

	private JScrollPane paneBuilder(Tileset set) {
		JScrollPane pane = new JScrollPane(set);
		pane.setPreferredSize(new Dimension(307, 730));
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		return pane;
	}

	public void loadTilesets() {

	}

	private class Tileset extends JPanel {

		private final int id;

		public Tileset(int id) {
			this.id = id;
			setPreferredSize(new Dimension(289, 730));
			MListener l = new MListener();
			addMouseListener(l);
			addMouseMotionListener(l);
		}

		@Override
		public void paint(Graphics g) {
			setBackground(Config.getEditorColor(Config.C_TILESET_BACKGROUND));
			super.paint(g);
			if (ProjectHandler.getProject() == null)
				return;
			int ts = ProjectHandler.getProject().getTilesize();
			Tilesets tilesets = ProjectHandler.getTilesets();
			if (tilesets == null)
				return;
			BufferedImage tileset = tilesets.getFull(id, animTimer);
			if (tileset == null)
				return;
			g.drawImage(tileset, 0, 0, null);
			int[] selected = tilesets.getSelected();
			if (selected[0] == id) {
				g.setColor(Config.getEditorColor(Config.C_TILESET_SELECTED));
				g.drawRect(selected[1] % tilesets.getSWidth() * ts, selected[1] / tilesets.getSWidth() * ts,
						ts * selected[2], ts * selected[3]);
			}
		}

		private class MListener extends MouseAdapter {

			private boolean pressed;
			private int startX, startY;

			@Override
			public void mousePressed(MouseEvent e) {
				if (ProjectHandler.getProject() == null)
					return;
				if (e.getButton() == MouseEvent.BUTTON1) {
					pressed = true;
					int x, y;
					int ts = ProjectHandler.getProject().getTilesize();
					x = e.getX() / ts;
					y = e.getY() / ts;
					startX = x;
					startY = y;
					ProjectHandler.getTilesets().setSelected(id, x, y);
					win.repaint();
				}
			}

			public void mouseDragged(MouseEvent e) {
				if (ProjectHandler.getProject() == null)
					return;
				if (pressed) {
					int x, y;
					int ts = ProjectHandler.getProject().getTilesize();
					x = e.getX() / ts;
					y = e.getY() / ts;
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
					int width = x2 - x + 1;
					int height = y2 - y + 1;
					ProjectHandler.getTilesets().setSelected(id, x, y, width, height);
					repaint();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1)
					pressed = false;
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (ProjectHandler.getProject() == null)
					return;
				int x, y;
				int ts = ProjectHandler.getProject().getTilesize();
				x = e.getX() / ts;
				y = e.getY() / ts;
				Propertie prop = ProjectHandler.getTilesets().getProperties()
						.getPropertie(new int[] { id, x + y * ProjectHandler.getTilesets().getSWidth() });
				Tileset.this.setToolTipText(prop.toolTip);
			}
		}
	}

}
