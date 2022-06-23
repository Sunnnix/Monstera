package de.snx.monstera.data.mapdata;

import java.awt.Graphics2D;

import de.snx.monstera.data.ProjectHandler;
import de.snx.monstera.data.Tilesets;

public class Tile {

	public final int X, Y;
	public int[] l1 = new int[] { -1, -1 }, l2 = new int[] { -1, -1 }, l3 = new int[] { -1, -1 };
	public boolean isBlocking;
	public int[] prev = new int[] { -1, -2 };

	public Tile(int x, int y) {
		this.X = x;
		this.Y = y;
	}

	public void renderL1(Graphics2D g, int x, int y, double offsetX, double offsetY) {
		Tilesets tilesets = ProjectHandler.getTilesets();
		int ts = ProjectHandler.getProject().getTilesize();
		int animTimer = ProjectHandler.getMaps().getAnimTImer();
		g.drawImage(tilesets.get(l1, animTimer), (int) (x - offsetX), (int) (y - offsetY), ts, ts, null);
		g.drawImage(tilesets.get(l2, animTimer), (int) (x - offsetX), (int) (y - offsetY), ts, ts, null);
	}

	public void renderL2(Graphics2D g, int x, int y, double offsetX, double offsetY) {
		int ts = ProjectHandler.getProject().getTilesize();
		g.drawImage(ProjectHandler.getTilesets().get(l3, ProjectHandler.getMaps().getAnimTImer()), (int) (x - offsetX),
				(int) (y - offsetY), ts, ts, null);
	}

}
