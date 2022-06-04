package de.snx.monstera.battle;

import de.snx.monstera.global_data.CombatGroups;
import de.snx.psf.PSFFileIO;
import lombok.Getter;
import lombok.Setter;

public class BattleGroup {

	public final int ID;

	@Getter
	@Setter
	private String name = "";

	@Getter
	@Setter
	private Battler[] battlers;

	@Getter
	@Setter
	private int moneyDrop;

	public BattleGroup(int id) {
		this.ID = id;
		battlers = new Battler[0];
	}

	public BattleGroup() {
		this(CombatGroups.getNextID());
	}

	public BattleGroup(PSFFileIO file) {
		this(file.readInt("id"));
		name = file.readString("name");
		battlers = new Battler[file.readInt("size")];
		for (int i = 0; i < battlers.length; i++) {
			int index = i;
			file.room("b" + i, _s -> battlers[index] = new Battler(file));
		}
	}

	public void save(PSFFileIO file) {
		file.write("id", ID);
		file.write("name", name);
		file.write("size", battlers.length);
		for (int i = 0; i < battlers.length; i++) {
			int index = i;
			file.room("b" + i, _s -> battlers[index].save(file));
		}
	}

	@Override
	public String toString() {
		return (ID >= 100 ? ID : ID >= 10 ? "0" + ID : "00" + ID) + " - " + name;
	}

}
