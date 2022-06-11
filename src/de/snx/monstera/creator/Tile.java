package de.snx.monstera.creator;

public class Tile {

	public final int X, Y;
	public int[] l1 = new int[] {-1, -1}, l2 = new int[] {-1, -1}, l3 = new int[] {-1, -1};
	public boolean isBlocking;
	public int[] prev = new int[] {-1, -2};
	
	public Tile(int x, int y) {
		this.X = x;
		this.Y = y;
	}
	
}
