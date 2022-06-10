package de.snx.monstera.creator;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;

import de.snx.monstera.creator.MapViewPanel.Mode;
import de.snx.monstera.creator.MapViewPanel.Shape;

/**
 * for all imagebutton menus
 */
@SuppressWarnings("serial")
public class ToolBar extends JMenuBar {

	private static final String buttonPath = "/de/snx/monstera/graphic/creator/";

	private CreatorWindow win;

	private JButton b_draw, b_blocking, b_entity, b_layer, b_single_tile, b_rect_tile, b_circle_tile, b_fill_tile;

	private ImageIcon[] layerImg;

	public ToolBar(CreatorWindow win) {
		this.win = win;
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(b_draw = button("draw", "Draw Mode", a -> win.setMode(Mode.DRAW_TILES)));
		b_draw.setSelected(true);
		add(b_blocking = button("blocking", "Blocking Mode", a -> win.setMode(Mode.SET_BLOCKING)));
		add(b_entity = button("entity", "Entity Mode", a -> win.setMode(Mode.ENTITY)));
		add(new JSeparator());
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
		add(new JSeparator());
		add(b_single_tile = button("single_tile", "Draw Single Tile", a -> win.setDrawShape(Shape.SINGLE)));
		b_single_tile.setSelected(true);
		add(b_rect_tile = button("rectangle_tile", "Draw in Rectangle Shape", a -> win.setDrawShape(Shape.RECT)));
		add(b_circle_tile = button("circle_tile", "Draw in Circle Shape", a -> win.setDrawShape(Shape.CIRCLE)));
		add(b_fill_tile = button("fill_tile", "Fill Tiles", a -> win.setDrawShape(Shape.FILL)));
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
