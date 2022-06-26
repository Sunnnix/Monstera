package de.snx.monsteracreator.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import de.snx.monstera.data.ProjectHandler;
import de.snx.monstera.data.mapdata.Entity;
import de.snx.monstera.event.Event;

@SuppressWarnings("serial")
public class EntityEditor extends JDialog {

	private Window win;
	private Entity entity;

	private JList<Event> list;
	private DefaultListModel<Event> listModel;

	private JTextField f_name, f_x, f_y, f_image;
	private JCheckBox cb_invisible;

	private JComboBox<String> trigger;

	public EntityEditor(Window win, Entity entity) {
		super(win, "Entity Editor", ModalityType.APPLICATION_MODAL);
		this.win = win;
		this.entity = entity;
		setLayout(new BorderLayout());
		initEventList();
		initInfos();
		initButtons();
		setResizable(false);
		pack();
		setLocationRelativeTo(win);
		setVisible(true);
	}

	private void initEventList() {
		ListMouseListener l = new ListMouseListener();
		list = new JList<>(listModel = new DefaultListModel<>());
		Event[] eEvents = entity.getEvents();
		for (Event event : eEvents)
			listModel.addElement(event.clone());
		list.addMouseListener(l);
		JScrollPane scroll = new JScrollPane(list);
		scroll.setPreferredSize(new Dimension(650, 600));
		add(scroll, BorderLayout.CENTER);
	}

	private void initInfos() {
		JPanel root = new JPanel(new FlowLayout());
		JPanel panel = new JPanel(new GridLayout(0, 1));
		JTextField f_id;
		panel.add(createRow("ID:", f_id = new JTextField(Integer.toString(entity.id))));
		f_id.setEditable(false);
		panel.add(createRow("Name:", f_name = new JTextField(entity.name)));
		panel.add(createRow("X:", f_x = new JTextField(Integer.toString((int) entity.getX()))));
		panel.add(createRow("Y:", f_y = new JTextField(Integer.toString((int) entity.getY()))));
		panel.add(createRow("Invisible:", cb_invisible = new JCheckBox()));
		cb_invisible.setSelected(entity.isInvisible());
		JPanel imagePanel = new JPanel(new BorderLayout());
		imagePanel.add(f_image = new JTextField(entity.getImageName()), BorderLayout.WEST);
		JButton deleteImageB;
		try {
			deleteImageB = new JButton(new ImageIcon(
					ImageIO.read(getClass().getResource("/de/snx/monstera/graphic/creator/icon_trash.png"))));
		} catch (Exception e) {
			deleteImageB = new JButton("X");
		}
		deleteImageB.setPreferredSize(new Dimension(20, 20));
		deleteImageB.addActionListener(e -> f_image.setText(""));
		imagePanel.add(deleteImageB, BorderLayout.EAST);
		panel.add(createRow("Image:", imagePanel));
		f_image.setEditable(false);
		f_image.setPreferredSize(new Dimension(155, 20));
		f_image.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() > 1) {
					JFileChooser chooser = new JFileChooser(
							new File(ProjectHandler.getProject().getResourcePath() + "graphic/entity"));
					chooser.setFileFilter(new FileFilter() {

						@Override
						public String getDescription() {
							return "Image (.png)";
						}

						@Override
						public boolean accept(File f) {
							return f.getName().toLowerCase().endsWith(".png");
						}
					});
					chooser.showOpenDialog(EntityEditor.this);
					File file = chooser.getSelectedFile();
					if (file == null)
						return;
					f_image.setText(file.getName().substring(0, file.getName().length() - 4));
				}
			}
		});
		panel.add(createRow("Trigger:", trigger = new JComboBox<>(new String[] { "None", "Interact", "On Touch" })));
		int te = entity.getEventTrigger();
		trigger.setSelectedIndex(te < 0 ? 1 : te);
		root.add(panel);
		add(root, BorderLayout.WEST);
	}

	private JPanel createRow(String text, JComponent comp) {
		JPanel panel = new JPanel();
		JLabel label = new JLabel(text);
		label.setPreferredSize(new Dimension(60, 20));
		panel.add(label);
		comp.setPreferredSize(new Dimension(180, 20));
		panel.add(comp);
		return panel;
	}

	private void initButtons() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		JButton apply = new JButton("Apply");
		apply.addActionListener(a -> applyChanges(false));
		JButton applyNClose = new JButton("Apply and close");
		applyNClose.addActionListener(a -> applyChanges(true));
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(a -> dispose());
		panel.add(apply);
		panel.add(applyNClose);
		panel.add(cancel);
		add(panel, BorderLayout.SOUTH);
	}

	private void applyChanges(boolean close) {
		entity.name = f_name.getText();
		try {
			entity.setPos(Integer.parseInt(f_x.getText()), Integer.parseInt(f_y.getText()));
		} catch (NumberFormatException e) {
		}
		entity.setInvisible(cb_invisible.isSelected());
		entity.setEventTrigger(trigger.getSelectedIndex());
		entity.setEvents(Collections.list(listModel.elements()));
		entity.setImageResource(f_image.getText());
		if (close)
			dispose();
	}

	private class ListMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1)
				if (e.getClickCount() == 2) {
					int index = list.getSelectedIndex();
					if (index == -1)
						return;
					try {
						new EventDialog(win, listModel.get(index), EntityEditor.this, null);
					} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
							| SecurityException e1) {
						e1.printStackTrace();
					}
				}
			if (e.getButton() == MouseEvent.BUTTON3) {
				int index = list.getSelectedIndex() + 1;
				if (index == -1)
					return;
				new PopUp(index).show(list, e.getX(), e.getY());
			}
		}

	}

	private class PopUp extends JPopupMenu {

		public PopUp(int index) {
			JMenuItem add = new JMenuItem("Add");
			add.addActionListener(a -> new EventPicker(win, entity, EntityEditor.this, event -> {
				if (event != null)
					listModel.add(index, event);
			}));
			add(add);
			if (index - 1 >= 0) {
				JMenuItem del = new JMenuItem("Delete");
				del.addActionListener(a -> listModel.remove(index - 1));
				add(del);
			}
		}

	}

}
