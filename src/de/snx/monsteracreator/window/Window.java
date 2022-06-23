package de.snx.monsteracreator.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.snx.monstera.data.ProjectHandler;
import de.snx.monsteracreator.Config;
import de.snx.monsteracreator.Creator;
import de.snx.monsteracreator.window.MapViewPanel.Mode;
import de.snx.monsteracreator.window.MapViewPanel.Shape;

@SuppressWarnings("serial")
public class Window extends JFrame {

	public static final String TITLE = "Pokemon Creator";

	private Creator creator;

	public MenuBar menu;
	public ToolBar tools;
	public TilesetPanel tileset;
	public MapViewPanel map;
	public InfoPanel info;

	public Window(Creator creator) {
		super(TITLE);
		this.creator = creator;
		setLayout(new BorderLayout());
		initPanels();
		initFrame();
		map.requestFocusInWindow();
	}

	private void initPanels() {
		JPanel bar = new JPanel(new BorderLayout());
		menu = new MenuBar(this);
		bar.add(menu, BorderLayout.NORTH);
		tools = new ToolBar(this);
		bar.add(tools, BorderLayout.SOUTH);
		add(bar, BorderLayout.NORTH);
		tileset = new TilesetPanel(this);
		add(tileset, BorderLayout.WEST);
		map = new MapViewPanel(this);
		JScrollPane scroll = new JScrollPane(map);
		scroll.setPreferredSize(new Dimension(900, 0));
		add(scroll, BorderLayout.CENTER);
		info = new InfoPanel(this);
		add(info, BorderLayout.SOUTH);
	}

	private void initFrame() {
		pack();
		Rectangle bounds = Config.windowBounds;
		if (bounds.width == 0)
			setLocationRelativeTo(null);
		else {
			setLocation(bounds.x, bounds.y);
			setSize(bounds.width, bounds.height);
		}
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Config.windowBounds.setBounds(getX(), getY(), getWidth(), getHeight());
				Config.save();
			}
		});
		setVisible(true);
	}
	
	public void loadAll() {
		tileset.loadTilesets();
		map.loadMap(ProjectHandler.getMaps().getSelected());
	}

	public void setLayer(int layer) {
		info.setLayer(layer);
		map.setSelectedLayer(layer);
		tools.setLayer(layer);
	}

	public void setMode(Mode mode) {
		info.setMode(mode.name());
		map.setMode(mode);
		tools.setMode(mode);
	}

	public void setDrawShape(Shape shape) {
		map.setShape(shape);
		tools.setShape(shape);
	}

}
