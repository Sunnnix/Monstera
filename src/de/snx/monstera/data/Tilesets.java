package de.snx.monstera.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import de.snx.monsteracreator.window.TilesetEditorWin;
import de.snx.monsteracreator.window.Window;
import de.snx.psf.PSFFileIO;
import lombok.Getter;
import lombok.Setter;

public class Tilesets implements IResource {

	@Getter
	private TilesetProperties properties = new TilesetProperties();
	private Tileset[] tileset = new Tileset[6];

	@Getter
	@Setter
	private int[] selected = new int[] { -1, -1, 1, 1 };

	@Override
	public String getPath() {
		return "graphic/tileset/properties";
	}

	@Override
	public String getResourceName() {
		return "Tilesets";
	}

	@Override
	public void save(Project project, PSFFileIO file) {
		properties.save(file);
	}

	@Override
	public void load(Project project, PSFFileIO file) {
		selected = new int[] { -1, -1, 1, 1 };
		tileset[0] = new Tileset(0, project, properties, loadTileset(project, "outdoor_floor"));
		tileset[1] = new Tileset(1, project, properties, loadTileset(project, "outdoor_object"));
		tileset[2] = new Tileset(2, project, properties, loadTileset(project, "house_floor"));
		tileset[3] = new Tileset(3, project, properties, loadTileset(project, "house_object"));
		tileset[4] = new Tileset(4, project, properties, loadTileset(project, "cave_floor"));
		tileset[5] = new Tileset(5, project, properties, loadTileset(project, "cave_object"));
		properties.load(file);
	}

	@Override
	public void onNewProject(Project project) {
		// TODO
//		copyFromRes(project.getResourcePath() + "tileset", null);
	}

	private BufferedImage loadTileset(Project project, String tileset) {
		File file = new File(project.getResourcePath() + "graphic/tileset/tileset_" + tileset + ".png");
		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			System.err.println("Couldn't load tileset " + tileset);
			return null;
		}
	}

	public void copyFromRes(String outputPath, String file) {
		try {
			File output = new File(outputPath);
			output.mkdirs();
			InputStream stream = getClass().getResourceAsStream("/de/snx/monstera/graphic/tileset");
			Files.copy(stream, output.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void editProperties(Window window) {
		new TilesetEditorWin(window, this);
	}

	public Tileset get(int id) {
		if (id < 0 || id >= tileset.length)
			return null;
		else
			return tileset[id];
	}

	public BufferedImage get(int tileset, int id, int ticks) {
		Tileset ts = get(tileset);
		if (ts == null)
			return null;
		else
			return ts.get(id, ticks);
	}

	public BufferedImage get(int[] pos, int ticks) {
		return get(pos[0], pos[1], ticks);
	}

	public BufferedImage get(int tileset, int id) {
		return get(tileset, id, 0);
	}

	public BufferedImage get(int[] pos) {
		return get(pos[0], pos[1], 0);
	}

	public BufferedImage getFull(int id, int ticks) {
		Tileset ts = get(id);
		if (ts == null)
			return null;
		else
			return ts.full(ticks);
	}

	public BufferedImage getFull(int id) {
		return getFull(id, 0);
	}

	public int getSWidth() {
		if (selected[0] == -1)
			return 0;
		return get(selected[0]).getWidth();
	}

	public void setSelected(int tileset, int id, int width, int height) {
		selected[0] = tileset;
		selected[1] = id;
		selected[2] = width;
		selected[3] = height;
	}

	public void setSelected(int tileset, int id) {
		setSelected(tileset, id, 1, 1);
	}

	public void setSelected(int id, int x, int y, int width, int height) {
		setSelected(id, x + y * get(id).getWidth(), width, height);
	}

	public void setSelected(int id, int x, int y) {
		setSelected(id, x, y, 1, 1);
	}

}
