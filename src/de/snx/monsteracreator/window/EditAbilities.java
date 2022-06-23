package de.snx.monsteracreator.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import de.snx.monstera.data.ProjectHandler;
import de.snx.monstera.data.battle.Ability;

@SuppressWarnings("serial")
public class EditAbilities extends JDialog {

	private Window win;

	private JComboBox<Ability> abilities;
	private ActionListener abilitiesL;

	private JTextField name, desc, power, acc, ap, prio, critM;
	private JComboBox<String> type, cat;
	private JButton b_add, b_delete, b_save;

	private Ability ability;

	private String currentSelected;

	public EditAbilities(Window win) {
		super(win, "Abilities Editor");
		this.win = win;
		setLayout(new BorderLayout());
		initPanel();
		onLoadAbility();
		setResizable(false);
		pack();
		setLocationRelativeTo(win);
		setVisible(true);
	}

	private void initPanel() {
		abilities = new JComboBox<>(ProjectHandler.getAbilities().getAll());
		add(abilities, BorderLayout.NORTH);
		abilitiesL = a -> onAbilitySelect();
		abilities.addActionListener(abilitiesL);
		JPanel panel = new JPanel(new GridLayout(0, 2));
		panel.add(new JLabel("Name:"));
		panel.add(name = new JTextField(12));
		panel.add(new JLabel("Description:"));
		panel.add(desc = new JTextField());
		panel.add(new JLabel("Type:"));
		panel.add(type = new JComboBox<>(de.snx.monstera.data.battle.Type.getTypes()));
		panel.add(new JLabel("Category"));
		panel.add(cat = new JComboBox<>(
				Arrays.asList(Ability.Category.values()).stream().map(c -> c.name()).toArray(String[]::new)));
		panel.add(new JLabel("Power:"));
		panel.add(power = getNumricField(false));
		panel.add(new JLabel("Accuracy:"));
		panel.add(acc = getNumricField(false));
		panel.add(new JLabel("Uses:"));
		panel.add(ap = getNumricField(false));
		panel.add(new JLabel("Priority"));
		panel.add(prio = getNumricField(false));
		panel.add(new JLabel("Crit Multiplier"));
		panel.add(critM = getNumricField(true));
		add(panel, BorderLayout.CENTER);
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttons.add(b_add = new JButton("Add"));
		b_add.addActionListener(a -> addNew());
		buttons.add(b_delete = new JButton("Delete"));
		b_delete.addActionListener(a -> deleteCurrent());
		buttons.add(b_save = new JButton("Save"));
		b_save.addActionListener(a -> save());
		add(buttons, BorderLayout.SOUTH);
	}

	private JTextField getNumricField(boolean isDouble) {
		JTextField field = new JTextField();
		((PlainDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
					throws BadLocationException {
				Document doc = fb.getDocument();
				StringBuilder sb = new StringBuilder();
				sb.append(doc.getText(0, doc.getLength()));
				sb.insert(offset, string);

				test(sb.toString());
				super.insertString(fb, offset, string, attr);
			}

			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
					throws BadLocationException {
				Document doc = fb.getDocument();
				StringBuilder sb = new StringBuilder();
				sb.append(doc.getText(0, doc.getLength()));
				sb.replace(offset, offset + length, text);

				test(sb.toString());
				super.replace(fb, offset, length, text, attrs);
			}

			@Override
			public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
				Document doc = fb.getDocument();
				StringBuilder sb = new StringBuilder();
				sb.append(doc.getText(0, doc.getLength()));
				sb.delete(offset, offset + length);

				test(sb.toString());
				super.remove(fb, offset, length);
			}

			private void test(String text) {
				try {
					if (isDouble)
						Double.parseDouble(text);
					else
						Integer.parseInt(text);
					field.setForeground(Color.BLACK);
				} catch (NumberFormatException e) {
					field.setForeground(Color.RED);
				}
			}
		});
		return field;
	}

	private void onLoadAbility() {
		ability = (Ability) abilities.getSelectedItem();
		if (ability == null)
			return;
		name.setText(ability.getName());
		desc.setText(ability.getDesc());
		type.setSelectedItem(ability.getType().name);
		cat.setSelectedItem(ability.getCat().name());
		power.setText(String.valueOf(ability.getPower()));
		acc.setText(String.valueOf(ability.getAcc()));
		ap.setText(String.valueOf(ability.getAP()));
		prio.setText(String.valueOf(ability.getPriority()));
		critM.setText(String.valueOf(ability.getCritMultiplyer()));
	}

	private void addNew() {
		String name = JOptionPane.showInputDialog(win, "Name:");
		if (name == null)
			return;
		ProjectHandler.getAbilities().newValue();
		abilities.removeActionListener(abilitiesL);
		abilities.removeAllItems();
		Ability[] a = ProjectHandler.getAbilities().getAll();
		for (Ability ability : a)
			abilities.addItem(ability);
		currentSelected = name;
		abilities.addActionListener(abilitiesL);
		abilities.setSelectedItem(currentSelected);
	}

	private void deleteCurrent() {
		abilities.removeActionListener(abilitiesL);
		abilities.addActionListener(abilitiesL);
	}

	private void save() {
//		if (name.getText().isEmpty()) {
//			JOptionPane.showMessageDialog(this, "Name can't be empty!");
//			return;
//		}
//		if (!checkValid(power, "Power") || !checkValid(acc, "Accuracy") || !checkValid(ap, "Uses")
//				|| !checkValid(prio, "Priority") || !checkValid(critM, "Crit Multiplier"))
//			return;
//		ProjectHandler.getAbilities().putValue(new Ability.Builder(name.getText(),
//				de.snx.monstera.data.battle.Type.getTypeFromString((String) type.getSelectedItem()),
//				Category.valueOf((String) cat.getSelectedItem())).setDescription(desc.getText())
//						.setPower(Integer.parseInt(power.getText())).setAccuracy(Integer.parseInt(acc.getText()))
//						.setAP(Integer.parseInt(ap.getText())).setPriority(Integer.parseInt(prio.getText()))
//						.setCritMultiplyer(Double.parseDouble(critM.getText())).build(),
//				currentSelected);
//		currentSelected = name.getText();
//		abilities.removeActionListener(abilitiesL);
//		abilities.removeAllItems();
//		String[] a = Ability.getAbilities();
//		for (String string : a)
//			abilities.addItem(string);
//		abilities.addActionListener(abilitiesL);
//		abilities.setSelectedItem(currentSelected);
//		onLoadAbility();
	}

	private boolean checkValid(JTextField field, String name) {
		if (field.getForeground().equals(Color.RED)) {
			JOptionPane.showMessageDialog(this, "Field " + name + " is invalid!");
			return false;
		}
		return true;
	}

	private void onAbilitySelect() {
		currentSelected = (String) abilities.getSelectedItem();
		onLoadAbility();
	}

}
