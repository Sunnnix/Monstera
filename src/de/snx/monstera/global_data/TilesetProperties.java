package de.snx.monstera.global_data;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import de.snx.monstera.Game;
import de.snx.psf.PSFFileIO;

public class TilesetProperties {

	public static final Propertie EMPTY = new Propertie();

	private static HashMap<Integer, Propertie> properties = new HashMap<>();

	public static void load(PSFFileIO file) {
		try {
			properties.clear();
			((ArrayList<Integer>) file.readArrayList("keys"))
					.forEach(key -> file.room(Integer.toString(key), _S -> properties.put(key, new Propertie(file))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadProperties();
	}

	public static void save(PSFFileIO file) {
		try {
			file.write("keys", new ArrayList(properties.keySet()));
			properties.forEach((key, prop) -> {
				file.room(Integer.toString(key), _S -> prop.save(file));
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setPropertie(int[] pos, Propertie prop) {
		properties.put(getKeyFromPos(pos), prop);
	}

	public static void removePropertie(int[] pos) {
		properties.remove(getKeyFromPos(pos));
	}

	private static int getKeyFromPos(int[] pos) {
		return pos[0] << 12 | pos[1];
	}

	public static Propertie getPropertie(int[] pos) {
		Propertie prop = properties.get(getKeyFromPos(pos));
		return prop == null ? EMPTY : prop;
	}

	public static void loadProperties() {
		properties.forEach((k, p) -> p.loadAnimImage());
	}

	public static class Propertie {

		public String toolTip;
		public boolean animate;
		public byte animTempo = 15;
		public BufferedImage[] animImg;
		public String src;

		public Propertie() {
		}

		public void loadAnimImage() {
			if (src == null || src.isEmpty())
				return;
			try {
				int tileSize = Game.TILESIZE;
				BufferedImage root = ImageIO.read(getClass()
						.getResource(ResourceStrings.S_TILESET_PATH + "animations/" + src + ResourceStrings.IMG_TYPE));
				int height = root.getHeight() / tileSize;
				animImg = new BufferedImage[height];
				for (int i = 0; i < height; i++)
					animImg[i] = root.getSubimage(0, tileSize * i, tileSize, tileSize);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public Propertie(PSFFileIO file) {
			toolTip = file.readString("tooltip");
			animate = file.readBoolean("anim");
			animTempo = file.readByte("anim_tempo");
			src = file.readString("src");
		}

		public void save(PSFFileIO file) {
			file.write("tooltip", toolTip);
			file.write("anim", animate);
			System.out.println(animTempo);
			file.write("anim_tempo", animTempo);
			file.write("src", src);
		}

	}

}
