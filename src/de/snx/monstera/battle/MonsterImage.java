package de.snx.monstera.battle;

import static de.snx.monstera.global_data.ResourceStrings.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MonsterImage {

	public static final MonsterImage NULL = new MonsterImage(null);

	private BufferedImage[] img = new BufferedImage[2];

	public MonsterImage(String path) {
		loadImage(path);
	}

	private void loadImage(String path) {
		try {
			if (path == null) {
				img[0] = ImageIO.read(getClass().getResource(MONSTER_IMG + "missing" + IMG_TYPE));
				img[1] = img[0];
			} else {
				img[0] = ImageIO.read(getClass().getResource(MONSTER_IMG + path + IMG_TYPE));
				img[1] = ImageIO.read(getClass().getResource(MONSTER_IMG + path + IMG_TYPE));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage get(int pos) {
		return img[pos];
	}

}
