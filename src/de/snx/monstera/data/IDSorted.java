package de.snx.monstera.data;

import java.util.Comparator;
import java.util.HashMap;

public abstract class IDSorted<Value extends IValueID> {

	protected HashMap<Integer, Value> values = new HashMap<>();

	public Value getValue(int id) {
		return values.get(id);
	}

	public void putValue(Value v) {
		values.put(v.getID(), v);
	}

	public void deleteValue(Value v) {
		deleteValue(v.getID());
	}

	public void deleteValue(int id) {
		values.remove(id);
	}

	public void changeValueID(int prev, int next) {
		Value v = values.get(prev);
		v.setID(next);
		values.remove(prev);
		values.put(next, v);
	}

	public boolean isIdInUse(int id) {
		return values.get(id) != null;
	}

	public int getNextID() {
		Integer[] ids = values.keySet().stream().sorted(Comparator.naturalOrder()).toArray(Integer[]::new);
		int id = 0;
		for (int i : ids) {
			if (i == id)
				id++;
			else
				return i;
		}
		return id;
	}

	public boolean isEmpty() {
		return values.isEmpty();
	}

}
