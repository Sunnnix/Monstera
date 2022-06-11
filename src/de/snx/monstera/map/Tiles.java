package de.snx.monstera.map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import de.snx.monstera.Game;

public class Tiles {

	private static BufferedImage[][] tiles; // TODO Load Tilesets

	private static HashMap<String, BufferedImage> graphics = new HashMap<>();

	public static String WHITE = "floor/wood_floor_0";

	public static void loadRes() {
		tiles = new BufferedImage[6][];
		loadTileset(0, "res/de/snx/monstera/graphic/tileset/tileset_outdoor_floor.png");
		loadTileset(1, "res/de/snx/monstera/graphic/tileset/tileset_outdoor_object.png");
		loadTileset(2, "res/de/snx/monstera/graphic/tileset/tileset_house_floor.png");
		loadTileset(3, "res/de/snx/monstera/graphic/tileset/tileset_house_object.png");
		loadTileset(4, "res/de/snx/monstera/graphic/tileset/tileset_cave_floor.png");
		loadTileset(5, "res/de/snx/monstera/graphic/tileset/tileset_cave_object.png");
	}

	private static void loadTileset(int id, String path) {
		try {
			BufferedImage root = ImageIO.read(new File(path));
			int width = root.getWidth() / Game.TILESIZE;
			int height = root.getHeight() / Game.TILESIZE;
			BufferedImage[] tileset = new BufferedImage[width * height];
			for (int i = 0; i < tileset.length; i++)
				tileset[i] = root.getSubimage(i % width * Game.TILESIZE, i / width * Game.TILESIZE, Game.TILESIZE,
						Game.TILESIZE);
			tiles[id] = tileset;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadResources(List<String> tiles) {
		for (Field f : Tiles.class.getFields())
			try {
				loadImage((String) f.get(f));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		for (String name : tiles)
			loadImage(name);
	}

	private static void loadImage(String name) {
		try {
			graphics.put(name, ImageIO.read(Tiles.class.getResource("/de/snx/monstera/graphic/tile/" + name + ".png")));
		} catch (Exception e) {
			if (e instanceof IllegalArgumentException)
				System.err.println("Can't find image (" + name + ".png)");
			else
				System.err.println("Can't load image (" + name + ".png)");
		}
	}

	public static BufferedImage get(int[] pos) {
		if (pos[0] == -1 || pos[1] == -1)
			return null;
		try {
			return tiles[pos[0]][pos[1]];
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			throw new RuntimeException("Couldn't get a tile graphics! Maybe no tileset or the wrong is loaded.", e);
		}
	}

}
