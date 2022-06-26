package de.snx.monstera.data;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

public class Project {

	@Getter
	private String name = "project";
	@Getter
	private File directory;
	@Getter
	private final int tilesize;
	@Getter
	@Setter
	private double scale = 1;
	@Getter
	@Setter
	private boolean useHalfFPSMode, fromCreator;

	public Project(int tilesize, String name, File directory) {
		this.tilesize = tilesize;
		this.name = name;
		this.directory = directory;
	}

	public void changeName(String name) {
		if (name == null || name.isEmpty())
			return;
		this.name = name;
	}

	public String getResourcePath() {
		return String.format("%s/%s/", directory.getAbsolutePath(), name);
	}

}
