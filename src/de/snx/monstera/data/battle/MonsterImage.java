package de.snx.monstera.data.battle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.snx.monstera.data.ProjectHandler;

public class MonsterImage {

	public static final String MISSING_IMG = "/de/snx/monstera/graphic/battler/";
	public static final MonsterImage NULL = new MonsterImage(null);

	private BufferedImage[] img = new BufferedImage[2];

	public MonsterImage(String path) {
		loadImage(path);
	}

	private void loadImage(String path) {
		try {
			if (path == null) {
				img[0] = ImageIO.read(getClass().getResource(MISSING_IMG + "missing.png"));
				img[1] = img[0];
			} else {
				img[0] = ImageIO.read(
						new File(ProjectHandler.getProject().getResourcePath() + "graphic/battler/" + path + ".png"));
				img[1] = ImageIO.read(
						new File(ProjectHandler.getProject().getResourcePath() + "graphic/battler/" + path + ".png"));
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				img[0] = ImageIO.read(getClass().getResource(MISSING_IMG + "missing.png"));
				img[1] = img[0];
			} catch (IOException e1) {
				new Exception("Error by loading missing Image!", e1).printStackTrace();
			}
		}
	}

	public BufferedImage get(int pos) {
		return img[pos];
	}

}
