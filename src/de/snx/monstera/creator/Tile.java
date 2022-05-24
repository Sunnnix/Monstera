package de.snx.monstera.creator;

public class Tile {

	public final int X, Y;
	public int l1 = -1, l2 = -1, l3 = -1;
	public boolean isBlocking;
	
	public Tile(int x, int y) {
		this.X = x;
		this.Y = y;
	}
	
}
