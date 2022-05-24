package de.snx.monstera;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import de.snx.monstera.map.event.Event;
import de.snx.psf.PSFFileIO;

public class Registry {

	private static HashMap<String, Class<? extends Event>> event = new HashMap<>();

	public static final void registerEvent(Class<? extends Event> clazz) {
		try {
			String name = (String) clazz.getField("REGISTRY_NAME").get(null);
			clazz.getConstructor();
			clazz.getConstructor(PSFFileIO.class);
			if (name == null)
				throw new NullPointerException(
						"The static field REGISTRY_NAME must be implemented");
			event.put(name, clazz);
		} catch (NullPointerException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException
				| NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("Couln't register event " + clazz.getName(), e);
		}
	}

	public static Event createEvent(String key) {
		try {
			if (!event.containsKey(key)) {
				System.err.println("There is no Event with the key " + key);
				return null;
			}
			Class<? extends Event> clazz = event.get(key);
			return (Event) clazz.getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Event createEventFromFile(String key, PSFFileIO file) {
		try {
			if (!event.containsKey(key)) {
				System.err.println("There is no Event with the key " + key);
				return null;
			}
			Class<? extends Event> clazz = event.get(key);
			return (Event) clazz.getConstructor(PSFFileIO.class).newInstance(file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> getAllEvents() {
		return new ArrayList<String>(event.keySet().stream().collect(Collectors.toList()));
	}

}
