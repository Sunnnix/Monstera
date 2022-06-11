package de.snx.monstera.map;

import java.awt.Graphics2D;

import de.snx.monstera.Game;
import de.snx.psf.PSFFileIO;

public class MapData {

	public final int X, Y;
	public boolean blocking;

	private int[] l1, l2, l3;

	public MapData(int x, int y) {
		this.X = x;
		this.Y = y;
	}

	public void renderL1(Graphics2D g, int x, int y, double offsetX, double offsetY) {
		g.drawImage(Tiles.get(l1), (int) (x - offsetX), (int) (y - offsetY), Game.S_TILESIZE, Game.S_TILESIZE, null);
		g.drawImage(Tiles.get(l2), (int) (x - offsetX), (int) (y - offsetY), Game.S_TILESIZE, Game.S_TILESIZE, null);
	}

	public void renderL2(Graphics2D g, int x, int y, double offsetX, double offsetY) {
		g.drawImage(Tiles.get(l3), (int) (x - offsetX), (int) (y - offsetY), Game.S_TILESIZE, Game.S_TILESIZE, null);
	}

	public void load(PSFFileIO file) {
		try {
			l1 = file.readIntArray("l1");
			l2 = file.readIntArray("l2");
			l3 = file.readIntArray("l3");
			blocking = file.readBoolean("blocking");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
