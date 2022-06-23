package de.snx.monstera.data;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import lombok.Getter;

public class EntityImage {

	public static final EntityImage NULL = new EntityImage(null);

	@Getter
	private String src;
	public String registryName;
	private BufferedImage[] img;

	public EntityImage(File file) {
		if (file == null) {
			int ts = ProjectHandler.getProject().getTilesize();
			src = null;
			BufferedImage tmp = new BufferedImage(ts, (int) (ts * 1.2), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = tmp.createGraphics();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, tmp.getWidth(), tmp.getHeight());
			g.setColor(Color.MAGENTA);
			g.fillRect(0, tmp.getHeight() / 2, tmp.getWidth() / 2, tmp.getHeight() / 2);
			g.fillRect(tmp.getWidth() / 2, 0, tmp.getWidth() / 2, tmp.getHeight() / 2);
			img = new BufferedImage[4 * 3];
			for (int i = 0; i < img.length; i++)
				img[i] = tmp;
		} else
			try {
				src = file.getName().substring(0, file.getName().length() - 3);
				BufferedImage root = ImageIO.read(file);
				BufferedImage src = new BufferedImage((int) (root.getWidth() * 2), (int) (root.getHeight() * 2),
						BufferedImage.TYPE_INT_ARGB);
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
				src = null;
				e.printStackTrace();
			}
	}

	public BufferedImage getImage(int direction, int anim) {
		if (img == null)
			return NULL.getImage(0, 0);
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
