package de.snx.monstera.global_data;

import java.util.ArrayList;

import de.snx.monstera.battle.BattleGroup;
import de.snx.psf.PSFFileIO;

public class CombatGroups {

	public static final BattleGroup EMPTY = new BattleGroup(0);

	private static ArrayList<BattleGroup> groups = new ArrayList<>();

	public static void loadAll(PSFFileIO file) {
		groups.clear();
		file.room("combat_groups", _s -> {
			int size = file.readInt("size");
			for (int i = 0; i < size; i++)
				file.room("g" + i, __s -> groups.add(new BattleGroup(file)));
		});
	}

	public static void saveAll(PSFFileIO file) {
		file.room("combat_groups", _s -> {
			file.write("size", groups.size());
			for (int i = 0; i < groups.size(); i++) {
				int index = i;
				file.room("g" + i, __s -> groups.get(index).save(file));
			}
		});
	}

	public static void addGroup(BattleGroup group) {
		for (int i = 0; i < groups.size(); i++)
			if (groups.get(i).ID == group.ID) {
				groups.set(i, group);
				return;
			}
		groups.add(group);
	}

	public static BattleGroup getGroup(int id) {
		for (BattleGroup group : groups)
			if (group.ID == id)
				return group;
		return EMPTY;
	}

	public static int getNextID() {
		int id = 1;
		groups.sort((o1, o2) -> o1.ID - o2.ID);
		for (int i = 0; i < groups.size(); i++)
			if (groups.get(i).ID == id)
				id++;
			else
				return id;
		return id;
	}

	public static BattleGroup[] getAll() {
		return groups.toArray(new BattleGroup[0]);
	}

}
