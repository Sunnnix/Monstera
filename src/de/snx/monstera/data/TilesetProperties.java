package de.snx.monstera.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import de.snx.psf.PSFFileIO;

public class TilesetProperties {

	public static final Propertie EMPTY = new Propertie();

	private HashMap<Integer, Propertie> properties = new HashMap<>();

	@SuppressWarnings("unchecked")
	public void load(PSFFileIO file) {
		try {
			properties.clear();
			((ArrayList<Integer>) file.readArrayList("keys"))
					.forEach(key -> file.room(Integer.toString(key), _S -> properties.put(key, new Propertie(file))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadProperties();
	}

	public void save(PSFFileIO file) {
		try {
			file.write("keys", new ArrayList<>(properties.keySet()));
			properties.forEach((key, prop) -> {
				file.room(Integer.toString(key), _S -> prop.save(file));
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setPropertie(int tileset, int id, Propertie prop) {
		properties.put(getKeyFromPos(tileset, id), prop);
	}

	public void setPropertie(int[] pos, Propertie prop) {
		setPropertie(pos[0], pos[1], prop);
	}

	public void removePropertie(int tileset, int id) {
		properties.remove(getKeyFromPos(tileset, id));
	}

	public void removePropertie(int[] pos) {
		removePropertie(pos[0], pos[1]);
	}

	private static int getKeyFromPos(int tileset, int id) {
		return tileset << 12 | id;
	}

	public Propertie getPropertie(int tileset, int id) {
		Propertie prop = properties.get(getKeyFromPos(tileset, id));
		return prop == null ? EMPTY : prop;
	}

	public Propertie getPropertie(int[] pos) {
		return getPropertie(pos[0], pos[1]);
	}

	public void loadProperties() {
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
			if (!animate)
				return;
			if (src == null || src.isEmpty()) {
				System.err.println("Failed to load animation " + src);
				return;
			}
			try {
				int tileSize = ProjectHandler.getProject().getTilesize();
				BufferedImage root = ImageIO.read(new File(
						ProjectHandler.getProject().getResourcePath() + "graphic/tileset/animations/" + src + ".png"));
				int height = root.getHeight() / tileSize;
				animImg = new BufferedImage[height];
				for (int i = 0; i < height; i++)
					animImg[i] = root.getSubimage(0, tileSize * i, tileSize, tileSize);
			} catch (Exception e) {
				System.err.println("Failed to load animation " + src);
				animate = false;
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
			file.write("anim_tempo", animTempo);
			file.write("src", src);
		}

	}

}
