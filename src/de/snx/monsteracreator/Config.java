package de.snx.monsteracreator;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import org.json.JSONObject;

public class Config {

	public static Rectangle windowBounds = new Rectangle();
	public static String projectsPath = "";
	private static Color[] editorColors;
	public static final int C_MAP_VIEW_BACKGROUND = 0;
	public static final int C_TILESET_BACKGROUND = 1;
	public static final int C_MAP_VIEW_GRID = 2;
	public static final int C_MAP_VIEW_LAYER_NUM = 3;
	public static final int C_MAP_VIEW_BLOCKING = 4;
	public static final int C_MAP_VIEW_E_NUM = 5;// Event
	public static final int C_MAP_VIEW_P_NUM = 6;// Player Event
	public static final int C_MAP_VIEW_E_BACK = 7;
	public static final int C_MAP_VIEW_P_BACK = 8;
	public static final int C_TILESET_SELECTED = 9;
	public static final int C_TILESET_E_SELECTED = 10;
	public static final int C_TILESET_E_PROPERTIE = 11;
	public static final int C_MAP_VIEW_S_BACK = 12; // Special event

	static {
		editorColors = new Color[13];
		editorColors[0] = Color.BLACK;
		editorColors[1] = new Color(255, 255, 230);
		editorColors[2] = Color.CYAN;
		editorColors[3] = Color.RED;
		editorColors[4] = Color.RED;
		editorColors[5] = Color.RED;
		editorColors[6] = Color.MAGENTA;
		editorColors[7] = new Color(100, 150, 200, 180);
		editorColors[8] = new Color(160, 80, 200, 180);
		editorColors[9] = Color.GREEN;
		editorColors[10] = Color.GREEN;
		editorColors[11] = Color.MAGENTA;
		editorColors[12] = new Color(80, 200, 80, 100);
	}

	public static void load() {
		try {
			File configFile = new File("creator.config");
			if (configFile.exists()) {
				JSONObject json = new JSONObject(new String(Files.readAllBytes(configFile.toPath())));
				loadWindowSettings(json);
				loadPaths(json);
				loadColors(json);
			} else {
				configFile.createNewFile();
				save();
			}
		} catch (IOException e) {
			new IOException("Error in loading config!", e).printStackTrace();
		}
	}

	public static void save() {
		JSONObject json = new JSONObject();
		saveWindowSettings(json);
		savePaths(json);
		saveColors(json);
		try (FileWriter writer = new FileWriter(new File("creator.config"))) {
			writer.write(json.toString());
		} catch (Exception e) {
			new IOException("Error in writing config!", e).printStackTrace();
		}
	}

	private static void loadWindowSettings(JSONObject json) {
		JSONObject winJson = json.getJSONObject("window");
		windowBounds.x = winJson.getInt("x");
		windowBounds.y = winJson.getInt("y");
		windowBounds.width = winJson.getInt("width");
		windowBounds.height = winJson.getInt("height");
	}

	private static void saveWindowSettings(JSONObject json) {
		JSONObject winJson = new JSONObject();
		winJson.put("x", windowBounds.x);
		winJson.put("y", windowBounds.y);
		winJson.put("height", windowBounds.height);
		winJson.put("width", windowBounds.width);
		json.put("window", winJson);
	}

	private static void loadPaths(JSONObject json) {
		JSONObject pathJson = json.getJSONObject("paths");
		projectsPath = pathJson.getString("projects_path");
	}

	private static void savePaths(JSONObject json) {
		JSONObject pathJson = new JSONObject();
		pathJson.put("projects_path", projectsPath);
		json.put("paths", pathJson);
	}

	private static void loadColors(JSONObject json) {
		JSONObject colorsJson = json.getJSONObject("colors");
		for (int i = 0; i < editorColors.length; i++)
			editorColors[i] = new Color(colorsJson.getInt(String.valueOf(i)));
	}

	private static void saveColors(JSONObject json) {
		JSONObject colorsJson = new JSONObject();
		for (int i = 0; i < editorColors.length; i++)
			colorsJson.put(String.valueOf(i), editorColors[i].getRGB());
		json.put("colors", colorsJson);
	}

	public static Color getEditorColor(int id) {
		if (id < 0 || id >= editorColors.length)
			return Color.BLACK;
		return editorColors[id];
	}

}
