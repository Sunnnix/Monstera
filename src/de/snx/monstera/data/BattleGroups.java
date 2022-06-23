package de.snx.monstera.data;

import java.util.Comparator;

import de.snx.monstera.data.battle.BattleGroup;
import de.snx.psf.PSFFileIO;

public class BattleGroups extends IDSorted<BattleGroup> implements IResource {

	public static final BattleGroup EMPTY = new BattleGroup(0);

	@Override
	public String getPath() {
		return "groups";
	}

	@Override
	public String getResourceName() {
		return "Groups";
	}

	@Override
	public void save(Project project, PSFFileIO file) {
		file.room("combat_groups", _s -> {
			file.write("size", values.size());
			BattleGroup[] groups = values.values().toArray(new BattleGroup[0]);
			for (int i = 0; i < groups.length; i++) {
				int index = i;
				file.room("g" + i, __s -> groups[index].save(file));
			}
		});
	}

	@Override
	public void load(Project project, PSFFileIO file) {
		values.clear();
		file.room("combat_groups", _s -> {
			int size = file.readInt("size");
			for (int i = 0; i < size; i++)
				file.room("g" + i, __s -> putValue(new BattleGroup(file)));
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

	public BattleGroup[] getAll() {
		return values.values().toArray(new BattleGroup[0]);
	}

	@Override
	public BattleGroup getValue(int id) {
		BattleGroup group = super.getValue(id);
		if (group == null)
			return EMPTY;
		return group;
	}

}
