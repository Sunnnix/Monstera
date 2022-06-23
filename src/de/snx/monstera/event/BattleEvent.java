package de.snx.monstera.event;

import java.awt.Graphics2D;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import de.snx.monstera.data.ProjectHandler;
import de.snx.monstera.data.battle.BattleGroup;
import de.snx.monstera.data.mapdata.Map;
import de.snx.monstera.state.GameStateManager;
import de.snx.monstera.state.WorldState;
import de.snx.monstera.util.Pair;
import de.snx.monsteracreator.window.Window;
import de.snx.psf.PSFFileIO;

public class BattleEvent extends Event {

	public static final String REGISTRY_NAME = "Battle Event";

	private int groupID;

	public BattleEvent() {
	}

	public BattleEvent(PSFFileIO file) {
		super(file);
		groupID = file.readInt("group_id");
	}

	@Override
	public boolean blockAction() {
		return true;
	}

	@Override
	public void keyEvents(WorldState world, Map map) {
	}

	@Override
	public void update(Map map, WorldState world, GameStateManager gsm) {
		gsm.setState(3, String.valueOf(groupID));
		finished = true;
	}

	@Override
	public void render(Graphics2D g, WorldState world, Map map) {

	}

	@Override
	public String getEventInfo() {
		return String.valueOf(ProjectHandler.getGroups().getValue(groupID));
	}

	@Override
	public void onSave(PSFFileIO file) throws Exception {
		file.write("group_id", groupID);
	}

	@Override
	public void interact(Map map) {
	}

	@Override
	public Pair<JPanel, Runnable> getEditorDialog(Window win) {
		JPanel panel = new JPanel();
		Runnable onApply;
		JComboBox<BattleGroup> groups = new JComboBox<>(ProjectHandler.getGroups().getAll());
		groups.setSelectedItem(ProjectHandler.getGroups().getValue(groupID));
		panel.add(groups);
		onApply = () -> {
			if (groups.getSelectedIndex() != -1)
				groupID = ((BattleGroup) groups.getSelectedItem()).ID;
		};
		return new Pair<JPanel, Runnable>(panel, onApply);
	}

}
