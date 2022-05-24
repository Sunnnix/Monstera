package de.snx.monstera.map;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

public class Tiles {

	private static HashMap<String, BufferedImage> graphics = new HashMap<>();

	public static String WHITE = "floor/wood_floor_0";

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

	public static BufferedImage get(String key) {
		return graphics.get(key);
	}

}
