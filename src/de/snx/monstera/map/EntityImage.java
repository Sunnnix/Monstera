package de.snx.monstera.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import de.snx.monstera.Game;

public class EntityImage {

	public String registryName;
	private BufferedImage[] img;

	public EntityImage(String res) {
		this.registryName = res;
		if (res == null) {
			BufferedImage tmp = new BufferedImage(Game.S_TILESIZE, (int) (Game.S_TILESIZE * 1.2),
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = tmp.createGraphics();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, tmp.getWidth(), tmp.getHeight());
			g.setColor(Color.MAGENTA);
			g.fillRect(0, tmp.getHeight() / 2, tmp.getWidth() / 2, tmp.getHeight() / 2);
			g.fillRect(tmp.getWidth() / 2, 0, tmp.getWidth() / 2, tmp.getHeight() / 2);
			img = new BufferedImage[4 * 3];
			for (int i = 0; i < img.length; i++)
				img[i] = tmp;
		} else if (res.isEmpty()) {
			BufferedImage tmp = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
			img = new BufferedImage[4 * 3];
			for (int i = 0; i < img.length; i++)
				img[i] = tmp;
		} else
			try {
				BufferedImage root = ImageIO
						.read(getClass().getResource("/de/snx/monstera/graphic/entity/" + res + ".png"));
				BufferedImage src = new BufferedImage((int) (root.getWidth() * Game.SCALE),
						(int) (root.getHeight() * Game.SCALE), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = src.createGraphics();
				g.drawImage(root, 0, 0, src.getWidth(), src.getHeight(), null);
				img = new BufferedImage[4 * 3];
				int xPart, yPart;
				xPart = src.getWidth() / 4;
				yPart = src.getHeight() / 3;
				for (int x = 0; x < 4; x++)
					for (int y = 0; y < 3; y++)
						img[x * 3 + y] = src.getSubimage(xPart * x, yPart * y, xPart, yPart);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public BufferedImage getImage(int direction, int anim) {
		if (anim != 0)
			if (anim % 4 == 0)
				anim = 2;
			else if (anim % 2 == 0)
				anim = 1;
			else
				anim = 0;
		return img[direction * 3 + anim];
	}

}
