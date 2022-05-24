package de.snx.monstera.battle;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BattlerImage {

	private BufferedImage[] img = new BufferedImage[2];

	public BattlerImage(String path) {
		loadImage(path);
	}

	private void loadImage(String path) {
		try {
			if (path == null) {
				img[0] = ImageIO.read(getClass().getResource("/de/snx/monstera/graphic/battler/" + "missing" + ".png"));
				img[1] = img[0];
			} else {
				img[0] = ImageIO.read(getClass().getResource("/de/snx/monstera/graphic/battler/" + path + "_0.png"));
				img[1] = ImageIO.read(getClass().getResource("/de/snx/monstera/graphic/battler/" + path + "_1.png"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage get(int pos) {
		return img[pos];
	}

}
