package de.snx.monstera.creator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import de.snx.monstera.battle.Ability;

public class EditMonster extends JDialog {

	private CreatorWindow win;

	private String[] types;

	private JTextField id, name, hp, atk, def, s_atk, s_def, speed, xpDrop, xpNeed, xpStFlat, xpStMult;
	private JComboBox<String> types1, types2;
	private Field[] typeFields = de.snx.monstera.battle.Type.class.getDeclaredFields();
	private ArrayList<String> typesS = new ArrayList<>();
	private JList<AbilityHolder> abilities;

	public EditMonster(CreatorWindow win) {
		super(win, "Edit Monsters");
		this.win = win;
		for (Field field : typeFields)
			if (field.getType().equals(de.snx.monstera.battle.Type.class))
				typesS.add(field.getName());
		types = typesS.toArray(new String[0]);
		a();
		b();
		setResizable(false);
		pack();
		setLocationRelativeTo(win);
		setVisible(true);
	}

	private void a() {
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
		add(panel, BorderLayout.WEST);
	}

	private void b() {
		JScrollPane pane = new JScrollPane();
		pane.setPreferredSize(new Dimension(450, 0));
		abilities = new JList<>();
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

}
