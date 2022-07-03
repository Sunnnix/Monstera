package de.snx.monsteracreator.window;

import static de.snx.monstera.util.WindowUtils.*;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuBar;

import de.snx.monstera.Main;
import de.snx.monsteracreator.MemoryStacks;
import de.snx.monsteracreator.window.MapViewPanel.Mode;
import de.snx.monsteracreator.window.MapViewPanel.Shape;
import lombok.Getter;

/**
 * for all imagebutton menus
 */
@SuppressWarnings("serial")
public class ToolBar extends JMenuBar {

	private static final String buttonPath = "/de/snx/monstera/graphic/creator/";

	private Window win;

	@Getter
	private JButton b_undo, b_redo;

	private JButton b_draw, b_blocking, b_entity, b_layer, b_single_tile, b_rect_tile, b_circle_tile, b_fill_tile;
	@Getter
	private JButton b_start_game;

	private ImageIcon[] layerImg;

	public ToolBar(Window win) {
		this.win = win;
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(b_undo = button("undo", false, "Undo's latest change on Map", e -> {
			MemoryStacks.loadUndoStack();
			win.menu.checkUndoRedo();
			win.repaint();
		}));
		b_undo.setEnabled(false);
		add(b_redo = button("redo", false, "Redo's latest change on Map", e -> {
			MemoryStacks.loadRedoStack();
			win.menu.checkUndoRedo();
			win.repaint();
		}));
		b_redo.setEnabled(false);
		add(space(10, 0));
		add(b_draw = button("draw", "Draw Mode", a -> win.setMode(Mode.DRAW_TILES)));
		b_draw.setSelected(true);
		add(b_blocking = button("blocking", "Blocking Mode", a -> win.setMode(Mode.SET_BLOCKING)));
		add(b_entity = button("entity", "Entity Mode", a -> win.setMode(Mode.ENTITY)));
		add(space(0, 0));
		layerImg = new ImageIcon[3];
		layerImg[0] = new ImageIcon(getClass().getResource(buttonPath + "layer1.png"));
		layerImg[1] = new ImageIcon(getClass().getResource(buttonPath + "layer2.png"));
		layerImg[2] = new ImageIcon(getClass().getResource(buttonPath + "layer3.png"));
		add(b_layer = button("layer1", false, "Switch Layer", a -> {
			int layer = win.map.getSelectedLayer();
			if (layer == 2)
				layer = 0;
			else
				layer++;
			win.setLayer(layer);
		}));
		add(space(10, 0));
		add(b_single_tile = button("single_tile", "Draw Single Tile", a -> win.setDrawShape(Shape.SINGLE)));
		b_single_tile.setSelected(true);
		add(b_rect_tile = button("rectangle_tile", "Draw in Rectangle Shape", a -> win.setDrawShape(Shape.RECT)));
		add(b_circle_tile = button("circle_tile", "Draw in Circle Shape", a -> win.setDrawShape(Shape.CIRCLE)));
		add(b_fill_tile = button("fill_tile", "Fill Tiles", a -> win.setDrawShape(Shape.FILL)));
		add(space(10, 0));
		add(b_start_game = button("start_game", "Start Game", e -> {
			if (b_start_game.isSelected())
				Main.stopGameProcess(win);
			else
				Main.startGameProcess(win);
		}));
	}

	private JButton button(String img, boolean sImg, String tooltip, ActionListener a) {
		JButton b = new JButton(new ImageIcon(getClass().getResource(buttonPath + img + ".png")));
		b.setMinimumSize(new Dimension(24, 24));
		b.setPreferredSize(new Dimension(24, 24));
		b.setMaximumSize(new Dimension(24, 24));
		b.addActionListener(a);
		if (sImg)
			b.setSelectedIcon(new ImageIcon(getClass().getResource(buttonPath + img + "_s.png")));
		b.setToolTipText(tooltip);
		return b;
	}

	private JButton button(String img, String tooltip, ActionListener a) {
		return button(img, true, tooltip, a);
	}

	public void setLayer(int layer) {
		b_layer.setIcon(layerImg[layer]);
	}

	public void setMode(Mode mode) {
		b_draw.setSelected(false);
		b_blocking.setSelected(false);
		b_entity.setSelected(false);
		switch (mode) {
		case DRAW_TILES:
			b_draw.setSelected(true);
			break;
		case SET_BLOCKING:
			b_blocking.setSelected(true);
			break;
		case ENTITY:
			b_entity.setSelected(true);
			break;
		default:
			break;
		}
	}

	public void setShape(Shape shape) {
		b_single_tile.setSelected(false);
		b_rect_tile.setSelected(false);
		b_circle_tile.setSelected(false);
		b_fill_tile.setSelected(false);
		switch (shape) {
		case SINGLE:
			b_single_tile.setSelected(true);
			break;
		case RECT:
			b_rect_tile.setSelected(true);
			break;
		case CIRCLE:
			b_circle_tile.setSelected(true);
			break;
		case FILL:
			b_fill_tile.setSelected(true);
			break;
		default:
			break;
		}
	}

}
