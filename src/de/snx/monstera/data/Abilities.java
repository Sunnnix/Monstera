package de.snx.monstera.data;

import java.util.ArrayList;

import de.snx.monstera.data.battle.Ability;
import de.snx.monstera.data.battle.Type;
import de.snx.psf.PSFFileIO;

public class Abilities extends IDSorted<Ability> implements IResource {

	@Override
	public String getPath() {
		return "abilities";
	}

	@Override
	public String getResourceName() {
		return "Abilities";
	}

	@Override
	public void save(Project project, PSFFileIO file) {
		file.room("abilities", _s -> {
			file.write("size", values.size());
			ArrayList<Ability> list = new ArrayList<>(values.values());
			for (int i = 0; i < list.size(); i++) {
				final int index = i;
				file.room("a" + i, __S -> list.get(index).save(file));
			}
		});
	}

	@Override
	public void load(Project project, PSFFileIO file) {
		values.clear();
		file.room("abilities", _s -> {
			int size = file.readInt("size");
			for (int i = 0; i < size; i++)
				file.room("a" + i, __s -> {
					final Ability a = new Ability(file);
					putValue(a);
				});
		});
	}

	public Ability[] getAll() {
		return values.values().toArray(new Ability[0]);
	}

	public void newValue() {
		Ability a = new Ability.Builder(getNextID(), "", Type.NORMAL).build();
	}

}
