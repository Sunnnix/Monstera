package de.snx.monstera.data;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

/**
 * List of entity graphics loaded from graphics/entity folder
 */
public class EntityImages {

	private HashMap<String, EntityImage> images = new HashMap<>();

	public void load(Project project) {
		images.clear();
		File directory = new File(project.getResourcePath(), "graphic/entity");
		if (directory.exists()) {
			File[] graphics = directory.listFiles(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.getName().endsWith(".png");
				}
			});
			for (File file : graphics) {
				EntityImage image = new EntityImage(file);
				images.put(image.getSrc(), image);
			}
		} else
			directory.mkdirs();
	}

	public EntityImage getImage(String src) {
		return images.getOrDefault(src, EntityImage.NULL);
	}

}
