package de.snx.monstera.creator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;

import de.snx.monstera.battle.Ability;
import de.snx.monstera.battle.MonsterType;

@SuppressWarnings("serial")
public class EditMonster extends JDialog {

	private CreatorWindow win;

	private String[] types;

	private JComboBox<String> monsterTypes;
	private JTextField id, name, hp, atk, def, s_atk, s_def, speed, xpDrop, xpNeed, xpStFlat, xpStMult;
	private JComboBox<String> types1, types2;
	private Field[] typeFields = de.snx.monstera.battle.Type.class.getDeclaredFields();
	private ArrayList<String> typesS = new ArrayList<>();
	private JList<AbilityHolder> abilities;
	private DefaultListModel<AbilityHolder> model;
	private JButton add, save, cancel;

	private int currentSelected;

	public EditMonster(CreatorWindow win) {
		super(win, "Edit Monsters");
		this.win = win;
		for (Field field : typeFields)
			if (field.getType().equals(de.snx.monstera.battle.Type.class))
				try {
					typesS.add(((de.snx.monstera.battle.Type) field.get(null)).name);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
		types = typesS.toArray(new String[0]);
		a();
		b();
		onMonsterSelected();
		setResizable(false);
		pack();
		setLocationRelativeTo(win);
		setVisible(true);
	}

	private void a() {
		JPanel main = new JPanel(new BorderLayout());
		main.add(monsterTypes = new JComboBox<String>(MonsterType.getMonsterTypes()), BorderLayout.NORTH);
		String selected = (String) monsterTypes.getSelectedItem();
		monsterTypes.addActionListener(a -> onMonsterSelected());
		currentSelected = selected == null ? -1 : Integer.parseInt(selected.substring(0, 3));
		JPanel panel = new JPanel(new GridLayout(0, 2));
		panel.add(new JLabel("ID:"));
		panel.add(id = new JTextField(12));
		id.setEditable(false);
		panel.add(new JLabel("Name:"));
		panel.add(name = new JTextField());
		panel.add(new JLabel("Type1:"));
		panel.add(types1 = new JComboBox<>(Arrays.copyOfRange(types, 1, types.length)));
		panel.add(new JLabel("Type12:"));
		panel.add(types2 = new JComboBox<>(types));
		panel.add(new JLabel("HP:"));
		panel.add(hp = new JTextField("20"));
		panel.add(new JLabel("ATK:"));
		panel.add(atk = new JTextField("20"));
		panel.add(new JLabel("DEF:"));
		panel.add(def = new JTextField("20"));
		panel.add(new JLabel("S ATK:"));
		panel.add(s_atk = new JTextField("20"));
		panel.add(new JLabel("S DEF:"));
		panel.add(s_def = new JTextField("20"));
		panel.add(new JLabel("SPEED:"));
		panel.add(speed = new JTextField("20"));
		panel.add(new JLabel("XP Drop"));
		panel.add(xpDrop = new JTextField("8"));
		panel.add(new JLabel("XP Need:"));
		panel.add(xpNeed = new JTextField("25"));
		panel.add(new JLabel("XP St. Flat:"));
		panel.add(xpStFlat = new JTextField("5"));
		panel.add(new JLabel("XP St. Mult:"));
		panel.add(xpStMult = new JTextField("1.1"));
		main.add(panel, BorderLayout.CENTER);
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttons.add(add = new JButton("Add"));
		add.addActionListener(a -> addMonster());
		buttons.add(save = new JButton("Save"));
		save.addActionListener(a -> save());
		buttons.add(cancel = new JButton("Cancel"));
		cancel.addActionListener(a -> dispose());
		main.add(buttons, BorderLayout.SOUTH);
		add(main, BorderLayout.WEST);
	}

	private void onMonsterSelected() {
		if (monsterTypes.getSelectedIndex() == -1)
			currentSelected = -1;
		else
			currentSelected = Integer.parseInt(((String) monsterTypes.getSelectedItem()).substring(0, 3));
		MonsterType type = MonsterType.getMonsterType(currentSelected);
		id.setText(String.valueOf(type.ID));
		name.setText(type.name);
		types1.setSelectedItem(type.type1.name);
		types2.setSelectedItem(type.type2.name);
		hp.setText(String.valueOf(type.hp));
		atk.setText(String.valueOf(type.atk));
		def.setText(String.valueOf(type.def));
		s_atk.setText(String.valueOf(type.s_atk));
		s_def.setText(String.valueOf(type.s_def));
		speed.setText(String.valueOf(type.speed));
		xpDrop.setText(String.valueOf(type.xpDrop));
		xpNeed.setText(String.valueOf(type.xpNeed));
		xpStFlat.setText(String.valueOf(type.xpInc));
		xpStMult.setText(String.valueOf(type.xpInc2));
		model.removeAllElements();
		Pair<Byte, Ability>[] abs = type.abilities;
		for (int i = 0; i < abs.length; i++)
			model.addElement(new AbilityHolder(abs[i].object1, abs[i].object2));
		this.revalidate();
	}

	private void b() {
		JScrollPane pane = new JScrollPane();
		pane.setPreferredSize(new Dimension(450, 0));
		model = new DefaultListModel<EditMonster.AbilityHolder>();
		abilities = new JList<>(model);
		abilities.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1)
					if (e.getClickCount() > 1 && abilities.getSelectedIndex() != -1)
						new AddAbilityWin(abilities.getSelectedValue());
				if (e.getButton() == MouseEvent.BUTTON3) {
					new PopUpMenu(abilities.getSelectedIndex()).show(abilities, e.getX(), e.getY());
				}
			}
		});
		pane.setViewportView(abilities);
		add(pane, BorderLayout.CENTER);
	}

	private class AbilityHolder {
		byte level;
		Ability ability;

		public AbilityHolder(int level, Ability ability) {
			this.level = (byte) level;
			this.ability = ability;
		}

		@Override
		public String toString() {
			return "At Lv. " + level + "(" + ability.name + ")";
		}
	}

	private void addMonster() {
		String name = JOptionPane.showInputDialog(this, "Name:");
		if (name == null || name.isEmpty())
			return;
		int id = MonsterType.getNextFreeID();
		currentSelected = id;
		MonsterType type = new MonsterType.Builder(id, name).build();
		MonsterType.addMonsterType(type, id);
		monsterTypes.removeAllItems();
		String[] types = MonsterType.getMonsterTypes();
		for (String string : types)
			monsterTypes.addItem(string);
		monsterTypes.setSelectedItem(
				(type.ID >= 100 ? type.ID : type.ID >= 10 ? "0" + type.ID : "00" + type.ID) + " - " + name);
	}

	private void save() {
		MonsterType.Builder builder = new MonsterType.Builder(Integer.parseInt(id.getText()), name.getText())
				.setType(de.snx.monstera.battle.Type.getTypeFromString((String) types1.getSelectedItem()),
						de.snx.monstera.battle.Type.getTypeFromString((String) types2.getSelectedItem()))
				.setStats(Integer.parseInt(hp.getText()), Integer.parseInt(atk.getText()),
						Integer.parseInt(def.getText()), Integer.parseInt(s_atk.getText()),
						Integer.parseInt(s_def.getText()), Integer.parseInt(speed.getText()))
				.setXPStats(Integer.parseInt(xpDrop.getText()), Integer.parseInt(xpNeed.getText()),
						Integer.parseInt(xpStFlat.getText()), Double.parseDouble(xpStMult.getText()));
		for (int i = 0; i < model.size(); i++) {
			AbilityHolder holder = model.getElementAt(i);
			builder.addAb(holder.level, holder.ability);
		}
		MonsterType type = builder.build();
		MonsterType.addMonsterType(type, currentSelected);
		monsterTypes.removeAllItems();
		String[] types = MonsterType.getMonsterTypes();
		for (String string : types)
			monsterTypes.addItem(string);
		monsterTypes.setSelectedItem(
				(type.ID >= 100 ? type.ID : type.ID >= 10 ? "0" + type.ID : "00" + type.ID) + " - " + name);
	}

	private class AddAbilityWin extends JDialog {

		private AbilityHolder holder;

		private JSpinner level;
		private JComboBox<String> abilities;
		private JButton save, cancel;

		public AddAbilityWin(AbilityHolder ab) {
			super(EditMonster.this);
			this.holder = ab;
			setLayout(new BorderLayout());
			JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
			panel1.add(level = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)));
			panel1.add(abilities = new JComboBox<String>(Ability.getAbilities()));
			add(panel1, BorderLayout.CENTER);
			JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
			panel2.add(save = new JButton("Save"));
			save.addActionListener(a -> save());
			panel2.add(cancel = new JButton("Cancel"));
			cancel.addActionListener(a -> dispose());
			add(panel2, BorderLayout.SOUTH);
			setResizable(false);
			pack();
			setLocationRelativeTo(EditMonster.this);
			setVisible(true);
		}

		private void save() {
			holder.ability = Ability.getAbility((String) abilities.getSelectedItem());
			int lv = (int) level.getValue();
			holder.level = (byte) lv;
			dispose();
			EditMonster.this.abilities.repaint();
		}

	}

	private class PopUpMenu extends JPopupMenu {

		public PopUpMenu(int index) {
			add(getItem("Add", a -> {
				AbilityHolder holder = new AbilityHolder(1, Ability.EMPTY_ABILITY);
				model.add(index + 1, holder);
				new AddAbilityWin(holder);
			}));
			if (index != -1) {
				add(getItem("Edit", a -> new AddAbilityWin(model.getElementAt(index))));
				add(getItem("Delete", a -> model.remove(index)));
			}
		}

		private JMenuItem getItem(String text, ActionListener l) {
			JMenuItem item = new JMenuItem(text);
			item.addActionListener(l);
			return item;
		}

	}

}
