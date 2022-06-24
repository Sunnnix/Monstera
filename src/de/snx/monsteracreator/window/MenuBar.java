package de.snx.monsteracreator.window;

import static java.awt.event.InputEvent.*;
import static java.awt.event.KeyEvent.*;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import de.snx.monstera.Main;
import de.snx.monstera.data.ProjectHandler;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {

	private Window win;

	private JMenu edit, mode, layer;
	private JMenuItem startGame, stopGame;

	public MenuBar(Window win) {
		this.win = win;
		initMenus();
	}

	private void initMenus() {
		add(initFileMenu(new JMenu("File")));
		add(initEditMenu(edit = new JMenu("Edit")));
		add(initModeMenu(mode = new JMenu("Mode")));
		add(initLayerMenu(layer = new JMenu("Layer")));
		add(initHelpMenu(new JMenu("Help")));
		activateAll(false);
	}

	private JMenu initFileMenu(JMenu menu) {
		bindMenu(menu, new JMenuItem("New Project"), null, e -> ProjectHandler.newProject(win));
		bindMenu(menu, new JMenuItem("Open Project"), null, e -> ProjectHandler.loadProject(win));
		menu.add(new JSeparator());
		bindMenu(menu, new JMenuItem("Save Project"), KeyStroke.getKeyStroke(VK_S, CTRL_MASK),
				e -> ProjectHandler.saveProject(win, false));
		bindMenu(menu, new JMenuItem("Save Project as..."), KeyStroke.getKeyStroke(VK_S, CTRL_MASK | SHIFT_MASK),
				e -> ProjectHandler.saveProject(win, true));
		menu.add(new JSeparator());
		bindMenu(menu, new JMenuItem("Customize Editor"), null, e -> new CustomizerWin(win));
		menu.add(new JSeparator());
		bindMenu(menu, new JMenuItem("Exit"), null, e -> System.exit(0));
		return menu;
	}

	private JMenu initEditMenu(JMenu menu) {
		bindMenu(menu, new JMenuItem("New Map"), KeyStroke.getKeyStroke(VK_N, CTRL_MASK),
				e -> ProjectHandler.getMaps().newMapDialog(win));
		bindMenu(menu, new JMenuItem("Switch Map"), KeyStroke.getKeyStroke(VK_O, CTRL_MASK),
				e -> ProjectHandler.getMaps().showSwitchDialog(win));
		bindMenu(menu, new JMenuItem("Edit Map"), null, e -> ProjectHandler.getMaps().editMap(win));
		bindMenu(menu, new JMenuItem("Delete Map"), KeyStroke.getKeyStroke(VK_D, CTRL_MASK),
				e -> ProjectHandler.getMaps().deleteCurrent(win));
		menu.add(new JSeparator());
		bindMenu(menu, new JMenuItem("Edit Tileset Properties"), KeyStroke.getKeyStroke(VK_T, ALT_MASK),
				e -> ProjectHandler.getTilesets().editProperties(win));
		bindMenu(menu, new JMenuItem("Edit Abilities"), KeyStroke.getKeyStroke(VK_A, ALT_MASK),
				e -> new EditAbilities(win));
		bindMenu(menu, new JMenuItem("Edit Monsters"), KeyStroke.getKeyStroke(VK_M, ALT_MASK),
				e -> new EditMonster(win));
		bindMenu(menu, new JMenuItem("Edit Groups"), KeyStroke.getKeyStroke(VK_G, ALT_MASK),
				e -> new EditGroupsWin(win));
		menu.add(new JSeparator());
		bindMenu(menu, startGame = new JMenuItem("Start Game"), KeyStroke.getKeyStroke(VK_F11, 0),
				e -> Main.startGameProcess(win));
		bindMenu(menu, stopGame = new JMenuItem("Stop Game"), KeyStroke.getKeyStroke(VK_F11, 0),
				e -> Main.stopGameProcess(win));
		stopGame.setEnabled(false);
		return menu;
	}

	private JMenu initModeMenu(JMenu menu) {
		ArrayList<JMenuItem> group = new ArrayList<>();
		group.add(bindMenu(menu, new JMenuItem("Draw Tiles"), KeyStroke.getKeyStroke(VK_F1, 0), e -> {
			group.forEach(i -> i.setSelected(false));
			((JMenuItem) e.getSource()).setSelected(true);
			win.setMode(MapViewPanel.Mode.DRAW_TILES);
		}));
		group.add(bindMenu(menu, new JMenuItem("Set Blocking"), KeyStroke.getKeyStroke(VK_F2, 0), e -> {
			group.forEach(i -> i.setSelected(false));
			((JMenuItem) e.getSource()).setSelected(true);
			win.setMode(MapViewPanel.Mode.SET_BLOCKING);
		}));
		group.add(bindMenu(menu, new JMenuItem("Entity"), KeyStroke.getKeyStroke(VK_F3, 0), e -> {
			group.forEach(i -> i.setSelected(false));
			((JMenuItem) e.getSource()).setSelected(true);
			win.setMode(MapViewPanel.Mode.ENTITY);
		}));
		return menu;
	}

	private JMenu initLayerMenu(JMenu menu) {
		ArrayList<JMenuItem> group = new ArrayList<>();
		JMenuItem item = bindMenu(menu, new JMenuItem("Layer 1 (Floor)"), KeyStroke.getKeyStroke(VK_1, 0), e -> {
			group.forEach(i -> i.setSelected(false));
			((JMenuItem) e.getSource()).setSelected(false);
			win.setLayer(0);
		});
		item.setSelected(true);
		group.add(item);
		group.add(bindMenu(menu, new JMenuItem("Layer 2 (Ground)"), KeyStroke.getKeyStroke(VK_2, 0), e -> {
			group.forEach(i -> i.setSelected(false));
			((JMenuItem) e.getSource()).setSelected(false);
			win.setLayer(1);
		}));
		group.add(bindMenu(menu, new JMenuItem("Layer 3 (Above)"), KeyStroke.getKeyStroke(VK_3, 0), e -> {
			group.forEach(i -> i.setSelected(false));
			((JMenuItem) e.getSource()).setSelected(false);
			win.setLayer(2);
		}));
		return menu;
	}

	private JMenu initHelpMenu(JMenu menu) {
		bindMenu(menu, new JMenuItem("Help"), KeyStroke.getKeyStroke(VK_H, KeyEvent.CTRL_MASK),
				e -> System.out.println("Help"));// TODO add Manual
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

	public void activateAll(boolean activate) {
		edit.setEnabled(activate);
		mode.setEnabled(activate);
		layer.setEnabled(activate);
		activateGameStart(activate);
	}

	public void activateGameStart(boolean activate) {
		if (win.tools != null)
			win.tools.getB_start_game().setSelected(!activate);
		startGame.setEnabled(activate);
		stopGame.setEnabled(!activate);

	}

}
