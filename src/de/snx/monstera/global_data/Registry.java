package de.snx.monstera.global_data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import de.snx.monstera.data.battle.Ability;
import de.snx.monstera.event.Event;
import de.snx.psf.PSFFileIO;

public class Registry {

	private static HashMap<String, Class<? extends Event>> event = new HashMap<>();
	private static HashMap<String, Ability> abilities = new HashMap<>();

	public static final void registerEvent(Class<? extends Event> clazz) {
		try {
			String name = (String) clazz.getField("REGISTRY_NAME").get(null);
			clazz.getConstructor();
			clazz.getConstructor(PSFFileIO.class);
			if (name == null)
				throw new NullPointerException("The static field REGISTRY_NAME must be implemented");
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

	public static Ability registerAbility(Ability ability) {
		abilities.put(ability.getName(), ability);
		return ability;
	}

	public static Ability loadAbility(String key, PSFFileIO file) {
		Ability a = null;
		try {
			a = Ability.class.getConstructor(PSFFileIO.class).newInstance(file);
			abilities.put(a.getName(), a);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return a;
	}

	public static Ability getAbility(String key) {
		return abilities.get(key);
	}

}
