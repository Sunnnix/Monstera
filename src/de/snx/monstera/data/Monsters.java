package de.snx.monstera.data;

import java.util.Comparator;

import de.snx.monstera.data.battle.MonsterType;
import de.snx.psf.PSFFileIO;

public class Monsters extends IDSorted<MonsterType> implements IResource {

	@Override
	public String getPath() {
		return "monsters";
	}

	@Override
	public String getResourceName() {
		return "Monsters";
	}

	@Override
	public void save(Project project, PSFFileIO file) {
		file.room("monsters", _s -> {
			file.write("size", values.size());
			MonsterType[] types = values.values().toArray(new MonsterType[0]);
			for (int i = 0; i < values.size(); i++) {
				final MonsterType type = types[i];
				file.room("m" + i, __s -> {
					type.save(file);
				});
			}
		});
	}

	@Override
	public void load(Project project, PSFFileIO file) {
		values.clear();
		file.room("monsters", _s -> {
			int size = file.readInt("size");
			for (int i = 0; i < size; i++) {
				file.room("m" + i, __s -> {
					putValue(new MonsterType(file));
				});
			}
		});
	}

	@Override
	public int getNextID() {
		Integer[] ids = values.keySet().stream().sorted(Comparator.naturalOrder()).toArray(Integer[]::new);
		int id = 1;
		for (int i : ids) {
			if (i == id)
				id++;
			else
				return i;
		}
		return id;
	}

	public String[] getAll() {
		return values.values().stream().map(t -> t.toString()).toArray(String[]::new);
	}

	@Override
	public MonsterType getValue(int id) {
		MonsterType type = super.getValue(id);
		if (type == null)
			return MonsterType.MISSINGNO;
		return type;
	}

}
