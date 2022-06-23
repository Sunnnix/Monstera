package de.snx.monstera.data;

import de.snx.psf.PSFFileIO;

public interface IResource {

	public String getPath();

	public String getResourceName();

	public void save(Project project, PSFFileIO file);

	public void load(Project project, PSFFileIO file);

	public default void onNewProject(Project project) {}

}
