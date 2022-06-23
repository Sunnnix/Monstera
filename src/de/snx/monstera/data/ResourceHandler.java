package de.snx.monstera.data;

import java.io.File;
import java.util.ArrayList;

import de.snx.psf.PSFFileIO;

public class ResourceHandler {

	private static ArrayList<IResource> resources = new ArrayList<>();

	public static void addResource(IResource res) {
		resources.add(res);
	}

	public static void removeResource(IResource res) {
		resources.remove(res);
	}

	public static void saveResources(Project project) throws Exception {
		for (IResource res : resources) {
			try (PSFFileIO file = new PSFFileIO(
					new File(project.getDirectory(), "/" + project.getName() + "/" + res.getPath() + ".dat"), "w")) {
				res.save(project, file);
			} catch (Exception e) {
				throw e;
			}
		}
	}

	public static void loadResources(Project project) {
		for (IResource res : resources) {
			try (PSFFileIO file = new PSFFileIO(
					new File(project.getDirectory(), "/" + project.getName() + "/" + res.getPath() + ".dat"), "r")) {
				res.load(project, file);
			} catch (Exception e) {
				new Exception("Error loading " + res.getResourceName(), e).printStackTrace();
			}
		}
	}

	public static void clear() {
		resources.clear();
	}

	public static void onNewProject(Project project) {
		resources.forEach(r -> r.onNewProject(project));
	}
}
