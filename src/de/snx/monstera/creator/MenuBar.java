package de.snx.monstera.creator;

import static java.awt.event.InputEvent.*;
import static java.awt.event.KeyEvent.*;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {

	private CreatorWindow win;

	public MenuBar(CreatorWindow win) {
		this.win = win;
		initMenus();
	}

	private void initMenus() {
		add(initFileMenu(new JMenu("File")));
		add(initEditMenu(new JMenu("Edit")));
		add(initModeMenu(new JMenu("Mode")));
		add(initLayerMenu(new JMenu("Layer")));
	}

	private JMenu initFileMenu(JMenu menu) {
		bindMenu(menu, new JMenuItem("New Project"), null, e -> win.map.newMap());
		bindMenu(menu, new JMenuItem("Open Project"), null, e -> win.map.openProject());
		bindMenu(menu, new JMenuItem("Save Project"), KeyStroke.getKeyStroke(VK_S, CTRL_MASK), e -> win.map.saveProject());
		return menu;
	}

	private JMenu initEditMenu(JMenu menu) {
		bindMenu(menu, new JMenuItem("New Map"), KeyStroke.getKeyStroke(VK_N, CTRL_MASK), e -> win.map.newMap());
		bindMenu(menu, new JMenuItem("Switch Map"), KeyStroke.getKeyStroke(VK_O, CTRL_MASK),
				e -> win.map.selectMapDialog());
		bindMenu(menu, new JMenuItem("Set Mapsize"), null, e -> win.map.openSizeDialog());
		return menu;
	}

	private JMenu initModeMenu(JMenu menu) {
		ArrayList<JMenuItem> group = new ArrayList<>();
		group.add(bindMenu(menu, new JMenuItem("Draw Tiles"), KeyStroke.getKeyStroke(VK_F1, 0), e -> {
			group.forEach(i -> i.setSelected(false));
			((JMenuItem) e.getSource()).setSelected(true);
			win.map.setMode(MapViewPanel.Mode.DRAW_TILES);
		}));
		group.add(bindMenu(menu, new JMenuItem("Set Blocking"), KeyStroke.getKeyStroke(VK_F2, 0), e -> {
			group.forEach(i -> i.setSelected(false));
			((JMenuItem) e.getSource()).setSelected(true);
			win.map.setMode(MapViewPanel.Mode.SET_BLOCKING);
		}));
		group.add(bindMenu(menu, new JMenuItem("Entity"), KeyStroke.getKeyStroke(VK_F3, 0), e -> {
			group.forEach(i -> i.setSelected(false));
			((JMenuItem) e.getSource()).setSelected(true);
			win.map.setMode(MapViewPanel.Mode.ENTITY);
		}));
		return menu;
	}

	private JMenu initLayerMenu(JMenu menu) {
		ArrayList<JMenuItem> group = new ArrayList<>();
		JMenuItem item = bindMenu(menu, new JMenuItem("Layer 1 (Floor)"), KeyStroke.getKeyStroke(VK_1, 0), e -> {
			group.forEach(i -> i.setSelected(false));
			((JMenuItem) e.getSource()).setSelected(false);
			win.map.selectLayer(0);
		});
		item.setSelected(true);
		group.add(item);
		group.add(bindMenu(menu, new JMenuItem("Layer 2 (Ground)"), KeyStroke.getKeyStroke(VK_2, 0), e -> {
			group.forEach(i -> i.setSelected(false));
			((JMenuItem) e.getSource()).setSelected(false);
			win.map.selectLayer(1);
		}));
		group.add(bindMenu(menu, new JMenuItem("Layer 3 (Above)"), KeyStroke.getKeyStroke(VK_3, 0), e -> {
			group.forEach(i -> i.setSelected(false));
			((JMenuItem) e.getSource()).setSelected(false);
			win.map.selectLayer(2);
		}));
		return menu;
	}

	private JMenuItem bindMenu(JMenu menu, JMenuItem item, KeyStroke accelator, ActionListener listener) {
		menu.add(item);
		if (listener != null)
			item.addActionListener(listener);
		if (accelator != null)
			item.setAccelerator(accelator);
		return item;
	}

}
