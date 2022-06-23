package de.snx.monstera.data;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import de.snx.monstera.data.TilesetProperties.Propertie;
import de.snx.monsteracreator.Config;
import lombok.Getter;

public class Tileset {

	private Project project;
	private TilesetProperties properties;
	@Getter
	private final int id;
	@Getter
	private final int width, height;
	private BufferedImage fullTileset;
	private BufferedImage[] tiles;

	public Tileset(int id, Project project, TilesetProperties properties, BufferedImage tileset) {
		this.project = project;
		this.properties = properties;
		this.id = id;
		int ts = project.getTilesize();
		if (tileset == null) {
			width = 0;
			height = 0;
			tiles = new BufferedImage[0];
		} else {
			fullTileset = new BufferedImage(tileset.getWidth(), tileset.getHeight(), BufferedImage.TYPE_INT_ARGB);
			width = tileset.getWidth() / ts;
			height = tileset.getHeight() / ts;
			tiles = new BufferedImage[width * height];
			for (int i = 0; i < tiles.length; i++)
				tiles[i] = tileset.getSubimage(i % width * ts, i / width * ts, ts, ts);
		}
	}

	public BufferedImage get(int id, int ticks) {
		if (fullTileset == null)
			return null;
		if (id < 0)
			return null;
		Propertie prop = properties.getPropertie(this.id, id);
		if (prop.animate) {
			int maxTimer = prop.animTempo * prop.animImg.length;
			int value = ticks % maxTimer;
			return prop.animImg[value / prop.animTempo];
		} else
			return tiles[id];
	}

	public BufferedImage full(int ticks) {
		if (fullTileset != null) {
			int ts = project.getTilesize();
			Graphics g = fullTileset.getGraphics();
			g.setColor(Config.getEditorColor(Config.C_TILESET_BACKGROUND));
			g.fillRect(0, 0, fullTileset.getWidth(), fullTileset.getHeight());
			for (int i = 0; i < tiles.length; i++)
				g.drawImage(get(i, ticks), (i % width) * ts, (i / width) * ts, null);
		}
		return fullTileset;
	}

}
