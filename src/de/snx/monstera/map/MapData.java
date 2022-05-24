package de.snx.monstera.map;

import java.awt.Graphics2D;

import de.snx.monstera.Game;
import de.snx.psf.PSFFileIO;

public class MapData {

	public final int X, Y;
	public boolean blocking;

	private String l1, l2, l3;

	public MapData(int x, int y) {
		this.X = x;
		this.Y = y;
	}

	public void renderL1(Graphics2D g, int x, int y, double offsetX, double offsetY) {
		g.drawImage(Tiles.get(l1), (int) (x - offsetX), (int) (y - offsetY), Game.TILESIZE, Game.TILESIZE, null);
		g.drawImage(Tiles.get(l2), (int) (x - offsetX), (int) (y - offsetY), Game.TILESIZE, Game.TILESIZE, null);
	}

	public void renderL2(Graphics2D g, int x, int y, double offsetX, double offsetY) {
		g.drawImage(Tiles.get(l3), (int) (x - offsetX), (int) (y - offsetY), Game.TILESIZE, Game.TILESIZE, null);
	}

	public void setLayer(int layer, String key) {
		switch (layer) {
		case 1:
			l1 = key;
			break;
		case 2:
			l2 = key;
			break;
		case 3:
			l3 = key;
			break;
		default:
			break;
		}
	}

	public void load(PSFFileIO file) {
		l1 = file.readString("l1");
		l2 = file.readString("l2");
		l3 = file.readString("l3");
		blocking = file.readBoolean("blocking");
	}

}
