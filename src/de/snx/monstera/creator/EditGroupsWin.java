package de.snx.monstera.creator;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;

import javax.swing.*;

import de.snx.monstera.battle.BattleGroup;
import de.snx.monstera.battle.Battler;
import de.snx.monstera.battle.monstertype.MonsterType;
import de.snx.monstera.global_data.CombatGroups;

@SuppressWarnings("serial")
public class EditGroupsWin extends JDialog {

	private CreatorWindow win;

	private JComboBox<BattleGroup> groups;
	private JList<Battler> battlers;
	private DefaultListModel<Battler> model;
	private JTextField id, name, moneyDrop;
	private JButton add, save, cancel;
	private BattleGroup currentSelected;

	public EditGroupsWin(CreatorWindow win) {
		super(win, "Edit Combat Groups", ModalityType.APPLICATION_MODAL);
		this.win = win;
		initPanel();
		onGroupSelected();
		setResizable(false);
		pack();
		setLocationRelativeTo(win);
		setVisible(true);
	}

	private void initPanel() {
		JPanel main = new JPanel(new BorderLayout());
		main.add(groups = new JComboBox<BattleGroup>(CombatGroups.getAll()), BorderLayout.NORTH);
		BattleGroup selected = (BattleGroup) groups.getSelectedItem();
		groups.addActionListener(a -> onGroupSelected());
		currentSelected = selected == null ? new BattleGroup() : selected;
		JPanel panel = new JPanel(new GridLayout(0, 2));
		panel.add(new JLabel("ID:"));
		panel.add(id = new JTextField(12));
		id.setEditable(false);
		id.setText(String.valueOf(currentSelected.ID));
		panel.add(new JLabel("Name:"));
		panel.add(name = new JTextField(currentSelected.getName()));
		panel.add(new JLabel("Money Drop:"));
		panel.add(moneyDrop = new JTextField(String.valueOf(currentSelected.getMoneyDrop())));
		main.add(panel, BorderLayout.CENTER);
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttons.add(add = new JButton("Add"));
		add.addActionListener(a -> addGroup());
		buttons.add(save = new JButton("Save"));
		save.addActionListener(a -> save());
		buttons.add(cancel = new JButton("Cancel"));
		cancel.addActionListener(a -> dispose());
		main.add(buttons, BorderLayout.SOUTH);
		add(main, BorderLayout.WEST);
		model = new DefaultListModel<>();
		battlers = new JList<>(model);
		add(new JScrollPane(battlers), BorderLayout.EAST);
		battlers.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() > 1 && battlers.getSelectedIndex() >= 0) {
					if (MonsterType.hasTypes())
						new EditBattlerDialog(win, battlers.getSelectedValue(), battlers);
					else
						JOptionPane.showMessageDialog(main, "There are no Monstertypes");
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					new ContextMenu(win, battlers.getSelectedIndex(), battlers).show(battlers, e.getX(), e.getY());
				}
			}
		});
	}

	private void onGroupSelected() {
		currentSelected = (BattleGroup) groups.getSelectedItem();
		if (currentSelected == null)
			currentSelected = new BattleGroup(CombatGroups.getNextID());
		id.setText(String.valueOf(currentSelected.ID));
		name.setText(currentSelected.getName());
		moneyDrop.setText(String.valueOf(currentSelected.getMoneyDrop()));
		model.removeAllElements();
		Battler[] tmp = currentSelected.getBattlers();
		for (Battler battler : tmp)
			model.addElement(battler);
		this.revalidate();
	}

	private void addGroup() {
		currentSelected = new BattleGroup();
		CombatGroups.addGroup(currentSelected);
		groups.removeAllItems();
		refreshGroupList();
	}

	private void save() {
		currentSelected.setBattlers(Collections.list(model.elements()).toArray(new Battler[0]));
		currentSelected.setName(name.getText());
		currentSelected.setMoneyDrop(Integer.parseInt(moneyDrop.getText()));
		CombatGroups.addGroup(currentSelected);
		refreshGroupList();
	}

	private void refreshGroupList() {
		groups.removeAllItems();
		BattleGroup[] tmp = CombatGroups.getAll();
		for (BattleGroup battleGroup : tmp)
			groups.addItem(battleGroup);
		groups.setSelectedItem(currentSelected);
	}

	private class EditBattlerDialog extends JDialog {

		public EditBattlerDialog(CreatorWindow win, Battler battler, JList<Battler> list) {
			super(win, "Edit Battler", ModalityType.APPLICATION_MODAL);
			setLayout(new BorderLayout());
			JPanel panel, panel2;
			JSpinner level;
			JComboBox<String> type;
			JButton apply, cancel;
			panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			panel.add(level = new JSpinner(new SpinnerNumberModel(battler.getLevel(), 1, 100, 1)));
			panel.add(type = new JComboBox<String>(MonsterType.getMonsterTypes()));
			type.setSelectedItem(battler.getType().toString());
			panel2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
			panel2.add(apply = new JButton("Apply"));
			apply.addActionListener(a -> {
				battler.setLevel((int) level.getValue());
				battler.setType(MonsterType
						.getMonsterType(Integer.parseInt(((String) type.getSelectedItem()).substring(0, 3))));
				dispose();
				list.revalidate();
				list.repaint();
			});
			panel2.add(cancel = new JButton("Cancel"));
			cancel.addActionListener(a -> dispose());
			add(panel, BorderLayout.CENTER);
			add(panel2, BorderLayout.SOUTH);
			setResizable(false);
			pack();
			setLocationRelativeTo(win);
			setVisible(true);
		}

	}

	private class ContextMenu extends JPopupMenu {

		public ContextMenu(CreatorWindow win, int selectedIndex, JList<Battler> battlers) {
			if (selectedIndex >= 0) {
				add(addMenu("Edit", a -> {
					if (MonsterType.hasTypes())
						new EditBattlerDialog(win, battlers.getSelectedValue(), battlers);
					else
						JOptionPane.showMessageDialog(win, "There are no Monstertypes");
				}));
				add(addMenu("Delete", a -> ((DefaultListModel<Battler>) battlers.getModel()).remove(selectedIndex)));
			}
			add(addMenu("Add", a -> {
				if (MonsterType.hasTypes()) {
					Battler b = new Battler(1, MonsterType.MISSINGNO);
					((DefaultListModel<Battler>) battlers.getModel()).add(selectedIndex + 1, b);
					new EditBattlerDialog(win, b, battlers);
				} else
					JOptionPane.showMessageDialog(win, "There are no Monstertypes");
			}));
		}

		private JMenuItem addMenu(String name, ActionListener l) {
			JMenuItem item = new JMenuItem(name);
			item.addActionListener(l);
			return item;
		}

	}

}
