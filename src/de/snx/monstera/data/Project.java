package de.snx.monstera.data;

import java.io.File;

import lombok.Getter;

public class Project {

	@Getter
	private String name = "project";
	@Getter
	private File directory;
	@Getter
	private final int tilesize;

	public Project(int tilesize, String name, File directory) {
		this.tilesize = tilesize;
		this.name = name;
		this.directory = directory;
	}

	public void changeName(String name) {
		if (name == null || name.isEmpty())
			return;
	}

	public String getResourcePath() {
		return String.format("%s/%s/", directory.getAbsolutePath(), name);
	}

}
