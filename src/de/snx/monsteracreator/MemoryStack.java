package de.snx.monsteracreator;

import de.snx.monstera.data.ProjectHandler;
import de.snx.monstera.data.mapdata.Map;
import de.snx.monstera.data.mapdata.Tile;
import lombok.Getter;

public class MemoryStack {

	@Getter
	private int mapID;
	private Tile[][] tiles;

	public MemoryStack(Map map) {
		this.mapID = map.getID();
		tiles = new Tile[map.getWidth()][map.getHeight()];
		for (int x = 0; x < tiles.length; x++)
			for (int y = 0; y < tiles[0].length; y++)
				this.tiles[x][y] = map.getTile(x, y).clone();
	}

	public void load() {
		Map map = ProjectHandler.getMaps().getValue(mapID);
		for (int x = 0; x < tiles.length; x++)
			for (int y = 0; y < tiles[0].length; y++) {
				Tile tile = tiles[x][y];
				Tile mapTile = map.getTile(x, y);
				mapTile.l1 = tile.l1.clone();
				mapTile.l2 = tile.l2.clone();
				mapTile.l3 = tile.l3.clone();
				mapTile.isBlocking = tile.isBlocking;
			}
	}

}
